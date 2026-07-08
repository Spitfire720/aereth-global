package live.aereth.fragmentengine.service;

import live.aereth.fragmentengine.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AbilityActivationService {
    private final CharacterService characters;
    private final DisciplineService disciplines;
    private final AbilityService abilities;
    private final AbilityEffectService effects;
    private final AbilityTargetingService targeting;
    private final AbilityResourceService resources;
    private final AbilityScalingService scaling;

    public AbilityActivationService(JavaPlugin plugin, CharacterService characters, DisciplineService disciplines, AbilityService abilities) {
        this.characters = characters;
        this.disciplines = disciplines;
        this.abilities = abilities;
        this.effects = new AbilityEffectService(plugin);
        this.targeting = new AbilityTargetingService();
        this.resources = new AbilityResourceService();
        this.scaling = new AbilityScalingService();
    }

    public ActivationResult activate(OfflinePlayer player, int slot) throws IOException {
        YamlConfiguration character = activeCharacter(player);
        DisciplineService.DisciplineSummary discipline = disciplines.summary(character);

        if (!discipline.selected()) {
            throw new IllegalStateException("No Discipline selected.");
        }

        int maxSlots = maxLoadoutSlots(character);
        if (slot < 1 || slot > maxSlots) {
            throw new IllegalArgumentException("Ability loadout slot " + slot + " is locked. Max slots: " + maxSlots);
        }

        String abilityId = loadoutSlot(character, slot);
        if (abilityId.isBlank()) {
            throw new IllegalArgumentException("Ability loadout slot " + slot + " is empty.");
        }

        if (!abilities.allAbilityIds().contains(abilityId)) {
            throw new IllegalArgumentException("Unknown ability in loadout: " + abilityId);
        }

        AbilityService.AbilitySummary summary = abilities.summary(character);
        if (!summary.unlocked().contains(abilityId)) {
            throw new IllegalArgumentException("Ability is locked: " + abilities.displayName(abilityId));
        }

        AbilityService.AbilityDefinition definition = abilities.definition(abilityId);
        AbilityScalingService.ScalingResult scale = scaling.scale(character, definition, slot);

        long remaining = remainingCooldownMillis(character, abilityId);
        if (remaining > 0L) {
            throw new IllegalStateException("Ability on cooldown: " + secondsRoundedUp(remaining) + "s remaining.");
        }

        Player onlinePlayer = player.getPlayer();
        AbilityResourceService.CostPayment payment = resources.consume(character, onlinePlayer, definition, scale);

        long cooldownMillis = Math.max(0L, (long) Math.ceil(scale.scaledCooldownSeconds() * 1000.0));
        long now = System.currentTimeMillis();
        long expiresAt = now + cooldownMillis;

        AbilityTargetingService.TargetResult target = targeting.resolve(onlinePlayer, definition, slot);
        AbilityEffectService.EffectResult effect = effects.apply(onlinePlayer, definition, slot, target, scale);

        String cooldownPath = "abilities.cooldowns." + abilityId;
        character.set(cooldownPath + ".slot", slot);
        character.set(cooldownPath + ".display", definition.display());
        character.set(cooldownPath + ".activated-at", TimeUtil.nowIso());
        character.set(cooldownPath + ".activated-at-ms", now);
        character.set(cooldownPath + ".expires-at-ms", expiresAt);
        character.set(cooldownPath + ".cooldown-seconds", scale.scaledCooldownSeconds());
        character.set(cooldownPath + ".base-cooldown-seconds", definition.cooldownSeconds());
        character.set(cooldownPath + ".effect-id", effect.effectId());
        character.set(cooldownPath + ".effect-status", effect.status());
        character.set(cooldownPath + ".resource.type", payment.type());
        character.set(cooldownPath + ".resource.amount", payment.amount());
        character.set(cooldownPath + ".resource.base-amount", definition.costAmount());
        character.set(cooldownPath + ".resource.remaining", payment.remaining());
        character.set(cooldownPath + ".resource.max", payment.max());
        character.set(cooldownPath + ".resource.status", payment.status());
        character.set(cooldownPath + ".resource.scaling-status", payment.scalingStatus());
        writeTarget(character, cooldownPath + ".target", target);
        writeScaling(character, cooldownPath + ".scaling", scale);

        character.set("abilities.activation.last.slot", slot);
        character.set("abilities.activation.last.ability-id", abilityId);
        character.set("abilities.activation.last.display", definition.display());
        character.set("abilities.activation.last.cost-type", definition.costType());
        character.set("abilities.activation.last.cost-amount", scale.scaledCostAmount());
        character.set("abilities.activation.last.base-cost-amount", definition.costAmount());
        character.set("abilities.activation.last.cooldown-seconds", scale.scaledCooldownSeconds());
        character.set("abilities.activation.last.base-cooldown-seconds", definition.cooldownSeconds());
        character.set("abilities.activation.last.activated-at", TimeUtil.nowIso());
        character.set("abilities.activation.last.effect-id", effect.effectId());
        character.set("abilities.activation.last.effect-status", effect.status());
        character.set("abilities.activation.last.effect-detail", effect.detail());
        character.set("abilities.activation.last.resource.type", payment.type());
        character.set("abilities.activation.last.resource.amount", payment.amount());
        character.set("abilities.activation.last.resource.remaining", payment.remaining());
        character.set("abilities.activation.last.resource.max", payment.max());
        character.set("abilities.activation.last.resource.status", payment.status());
        character.set("abilities.activation.last.resource.scaling-status", payment.scalingStatus());
        writeTarget(character, "abilities.activation.last.target", target);
        writeScaling(character, "abilities.activation.last.scaling", scale);
        character.set("abilities.activation.last.status", "scaled_resource_paid_targeted_effect_routed");

        saveCharacter(player, character);

        return new ActivationResult(
                slot,
                abilityId,
                definition.display(),
                definition.costType(),
                scale.scaledCostAmount(),
                scale.scaledCooldownSeconds(),
                "scaled_resource_paid_targeted_effect_routed:" + effect.effectId(),
                target.mode(),
                target.status(),
                target.description(),
                payment.type(),
                payment.amount(),
                payment.remaining(),
                payment.max(),
                scale.role(),
                scale.potencyMultiplier(),
                scale.durationMultiplier(),
                scale.radiusMultiplier(),
                scale.identityLine()
        );
    }

    public TargetPreview previewTarget(OfflinePlayer player, int slot) {
        YamlConfiguration character = activeCharacter(player);
        String abilityId = loadoutSlot(character, slot);
        if (abilityId.isBlank() || !abilities.allAbilityIds().contains(abilityId)) {
            return new TargetPreview(slot, abilityId, "none", "missing_ability", "No valid ability in this loadout slot.");
        }

        AbilityService.AbilityDefinition definition = abilities.definition(abilityId);
        AbilityTargetingService.TargetResult target = targeting.resolve(player.getPlayer(), definition, slot);
        return new TargetPreview(slot, abilityId, target.mode(), target.status(), target.description());
    }

    public CooldownSummary cooldowns(OfflinePlayer player) {
        YamlConfiguration character = activeCharacter(player);
        List<ActiveCooldown> active = new ArrayList<>();

        for (int slot = 1; slot <= 4; slot++) {
            String abilityId = loadoutSlot(character, slot);
            if (abilityId.isBlank()) {
                continue;
            }

            long remaining = remainingCooldownMillis(character, abilityId);
            if (remaining <= 0L) {
                continue;
            }

            String display = abilities.allAbilityIds().contains(abilityId) ? abilities.displayName(abilityId) : abilityId;
            active.add(new ActiveCooldown(slot, abilityId, display, secondsRoundedUp(remaining)));
        }

        return new CooldownSummary(active, active.size());
    }

    public String loadoutSlot(YamlConfiguration character, int slot) {
        if (slot < 1 || slot > 4) {
            return "";
        }
        return normalize(character.getString("abilities.loadout.slots.slot" + slot, ""));
    }

    public int maxLoadoutSlots(YamlConfiguration character) {
        boolean selected = character.getBoolean("discipline.selected", false);
        if (!selected) {
            return 0;
        }

        int rank = Math.max(0, character.getInt("discipline.progression.rank", 0));
        return Math.max(1, Math.min(4, rank));
    }

    public long remainingCooldownSeconds(YamlConfiguration character, String abilityId) {
        return secondsRoundedUp(remainingCooldownMillis(character, abilityId));
    }

    public boolean isKnownLoadedAbility(String abilityId) {
        return abilityId != null && abilities.allAbilityIds().contains(normalize(abilityId));
    }

    public boolean isUnlocked(YamlConfiguration character, String abilityId) {
        if (abilityId == null || abilityId.isBlank()) {
            return false;
        }
        return abilities.summary(character).unlocked().contains(normalize(abilityId));
    }

    public String targetMode(AbilityService.AbilityDefinition definition) {
        return targeting.targetMode(definition);
    }

    public AbilityResourceService.ResourceSnapshot resourceSnapshot(YamlConfiguration character, Player player) {
        return resources.snapshot(character, player);
    }

    public AbilityResourceService.CostPreview costPreview(YamlConfiguration character, Player player, AbilityService.AbilityDefinition definition) {
        return resources.preview(character, player, definition);
    }

    public AbilityResourceService.CostPreview costPreviewScaled(YamlConfiguration character, Player player, AbilityService.AbilityDefinition definition, int slot) {
        return resources.preview(character, player, definition, scaling.scale(character, definition, slot));
    }

    public AbilityScalingService.ScalingResult scalingPreview(YamlConfiguration character, AbilityService.AbilityDefinition definition, int slot) {
        return scaling.scale(character, definition, slot);
    }

    private long remainingCooldownMillis(YamlConfiguration character, String abilityId) {
        String id = normalize(abilityId);
        if (id.isBlank()) {
            return 0L;
        }
        long expiresAt = character.getLong("abilities.cooldowns." + id + ".expires-at-ms", 0L);
        return Math.max(0L, expiresAt - System.currentTimeMillis());
    }

    private long secondsRoundedUp(long millis) {
        if (millis <= 0L) {
            return 0L;
        }
        return (long) Math.ceil(millis / 1000.0);
    }

    private YamlConfiguration activeCharacter(OfflinePlayer player) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            throw new IllegalStateException("No active character.");
        }
        return character;
    }

    private void saveCharacter(OfflinePlayer player, YamlConfiguration character) throws IOException {
        int slot = character.getInt("slot", characters.getActiveSlot(player));
        characters.storage().saveCharacter(player.getUniqueId(), slot, character);
    }

    private void writeTarget(YamlConfiguration character, String path, AbilityTargetingService.TargetResult target) {
        character.set(path + ".mode", target.mode());
        character.set(path + ".status", target.status());
        character.set(path + ".description", target.description());
        character.set(path + ".type", target.targetType());
        character.set(path + ".name", target.targetName());
        character.set(path + ".entity-type", target.entityType());
        character.set(path + ".distance", target.distance());
        character.set(path + ".world", target.world());
        character.set(path + ".x", target.x());
        character.set(path + ".y", target.y());
        character.set(path + ".z", target.z());
    }

    private void writeScaling(YamlConfiguration character, String path, AbilityScalingService.ScalingResult scale) {
        character.set(path + ".role", scale.role());
        character.set(path + ".discipline", scale.discipline());
        character.set(path + ".level", scale.level());
        character.set(path + ".rank", scale.rank());
        character.set(path + ".potency-multiplier", scale.potencyMultiplier());
        character.set(path + ".duration-multiplier", scale.durationMultiplier());
        character.set(path + ".radius-multiplier", scale.radiusMultiplier());
        character.set(path + ".velocity-multiplier", scale.velocityMultiplier());
        character.set(path + ".cooldown-multiplier", scale.cooldownMultiplier());
        character.set(path + ".cost-multiplier", scale.costMultiplier());
        character.set(path + ".scaled-cost-amount", scale.scaledCostAmount());
        character.set(path + ".scaled-cooldown-seconds", scale.scaledCooldownSeconds());
        character.set(path + ".identity-line", scale.identityLine());
        character.set(path + ".status", scale.status());
    }

    private String normalize(String raw) {
        return raw == null ? "" : raw.toLowerCase().trim().replace(" ", "_").replace("-", "_");
    }

    public record ActivationResult(
            int slot,
            String abilityId,
            String display,
            String costType,
            double costAmount,
            double cooldownSeconds,
            String status,
            String targetMode,
            String targetStatus,
            String targetDescription,
            String resourceType,
            double resourceCost,
            double resourceRemaining,
            double resourceMax,
            String scalingRole,
            double potencyMultiplier,
            double durationMultiplier,
            double radiusMultiplier,
            String identityLine
    ) {
    }

    public record ActiveCooldown(
            int slot,
            String abilityId,
            String display,
            long remainingSeconds
    ) {
    }

    public record CooldownSummary(
            List<ActiveCooldown> active,
            int count
    ) {
    }

    public record TargetPreview(
            int slot,
            String abilityId,
            String targetMode,
            String targetStatus,
            String targetDescription
    ) {
    }
}
