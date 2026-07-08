package live.aereth.fragmentengine.service;

import live.aereth.fragmentengine.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AbilityActivationService {
    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final DisciplineService disciplines;
    private final AbilityService abilities;

    public AbilityActivationService(JavaPlugin plugin, CharacterService characters, DisciplineService disciplines, AbilityService abilities) {
        this.plugin = plugin;
        this.characters = characters;
        this.disciplines = disciplines;
        this.abilities = abilities;
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
        long remaining = remainingCooldownMillis(character, abilityId);
        if (remaining > 0L) {
            throw new IllegalStateException("Ability on cooldown: " + secondsRoundedUp(remaining) + "s remaining.");
        }

        long cooldownMillis = Math.max(0L, (long) Math.ceil(definition.cooldownSeconds() * 1000.0));
        long now = System.currentTimeMillis();
        long expiresAt = now + cooldownMillis;

        String cooldownPath = "abilities.cooldowns." + abilityId;
        character.set(cooldownPath + ".slot", slot);
        character.set(cooldownPath + ".display", definition.display());
        character.set(cooldownPath + ".activated-at", TimeUtil.nowIso());
        character.set(cooldownPath + ".activated-at-ms", now);
        character.set(cooldownPath + ".expires-at-ms", expiresAt);
        character.set(cooldownPath + ".cooldown-seconds", definition.cooldownSeconds());

        character.set("abilities.activation.last.slot", slot);
        character.set("abilities.activation.last.ability-id", abilityId);
        character.set("abilities.activation.last.display", definition.display());
        character.set("abilities.activation.last.cost-type", definition.costType());
        character.set("abilities.activation.last.cost-amount", definition.costAmount());
        character.set("abilities.activation.last.cooldown-seconds", definition.cooldownSeconds());
        character.set("abilities.activation.last.activated-at", TimeUtil.nowIso());
        character.set("abilities.activation.last.status", "resolved_foundation_only");

        saveCharacter(player, character);

        return new ActivationResult(
                slot,
                abilityId,
                definition.display(),
                definition.costType(),
                definition.costAmount(),
                definition.cooldownSeconds(),
                "activated"
        );
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
            String status
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
}
