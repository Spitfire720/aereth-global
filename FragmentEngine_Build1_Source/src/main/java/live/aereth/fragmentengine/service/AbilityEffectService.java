package live.aereth.fragmentengine.service;

import live.aereth.fragmentengine.util.Text;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Predicate;

public class AbilityEffectService {
    private static final double TARGET_RANGE = 18.0;
    private static final double TARGET_RAY_SIZE = 0.85;
    private static final double AREA_RADIUS = 5.0;

    private final JavaPlugin plugin;
    private final AbilityCombatService combat;

    public AbilityEffectService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.combat = new AbilityCombatService(plugin);
    }

    public EffectResult apply(Player player, AbilityService.AbilityDefinition definition, int slot, AbilityTargetingService.TargetResult target) {
        return apply(player, definition, slot, target, AbilityScalingService.neutral(definition, slot));
    }

    public EffectResult apply(Player player, AbilityService.AbilityDefinition definition, int slot,
                              AbilityTargetingService.TargetResult target, AbilityScalingService.ScalingResult scaling) {
        String effectId = routeId(definition);

        if (player == null || !player.isOnline()) {
            return new EffectResult(effectId, "recorded_offline", "Player was offline. Target: " + target.description() + ". Scaling: " + scaling.role());
        }

        return switch (effectId) {
            case "defensive_pulse" -> defensivePulse(player, definition, slot, target, scaling);
            case "blood_pressure" -> bloodPressure(player, definition, slot, target, scaling);
            case "arcane_spark" -> arcaneSpark(player, definition, slot, target, scaling);
            case "focus_thread" -> focusThread(player, definition, slot, target, scaling);
            case "unstable_bloom" -> unstableBloom(player, definition, slot, target, scaling);
            case "stamina_surge" -> staminaSurge(player, definition, slot, target, scaling);
            default -> resonancePing(player, definition, slot, target, scaling);
        };
    }

    private EffectResult defensivePulse(Player player, AbilityService.AbilityDefinition definition, int slot,
                                        AbilityTargetingService.TargetResult target, AbilityScalingService.ScalingResult scaling) {
        applyPotion(player, duration(120, scaling), amplifier(0, scaling), "DAMAGE_RESISTANCE", "RESISTANCE");
        applyPotion(player, duration(120, scaling), 0, "ABSORPTION");
        safeSound(player, "BLOCK_ANVIL_PLACE", 0.45f, 1.25f);
        safeParticle(player, targetLocation(player, target), "END_ROD", particleCount(28, scaling), 0.75 * scaling.radiusMultiplier(), 0.85, 0.75 * scaling.radiusMultiplier(), 0.01);
        message(player, "&b" + definition.display() + " &7anchors a PvE-safe defensive pulse. &8[" + scaleTag(scaling) + "]");
        return new EffectResult("defensive_pulse", "applied_combat_safe", "Applied scaled Resistance/Absorption. No hostile damage route. " + scaleDetail(scaling));
    }

    private EffectResult bloodPressure(Player player, AbilityService.AbilityDefinition definition, int slot,
                                       AbilityTargetingService.TargetResult target, AbilityScalingService.ScalingResult scaling) {
        LivingEntity aimed = aimedLivingTarget(player);
        boolean marked = applyPotion(aimed, duration(120, scaling), 0, "GLOWING");
        AbilityCombatService.CombatImpact impact = combat.damageOne(player, aimed, damage(4.0, scaling), "blood_pressure", scaling);
        if (aimed == null) {
            applyPotion(player, duration(80, scaling), amplifier(0, scaling), "INCREASE_DAMAGE", "STRENGTH");
        }
        safeSound(player, "ENTITY_PLAYER_ATTACK_CRIT", 0.6f, 0.85f);
        safeParticle(player, targetLocation(player, target), "CRIT", particleCount(36, scaling), 0.55, 0.75, 0.55, 0.08);
        message(player, "&c" + definition.display() + " &7pressures &f" + targetLabel(target) + "&7. &8[PvE hits " + impact.targetsAffected() + ", " + scaleTag(scaling) + "]");
        return new EffectResult("blood_pressure", "combat_integrated", "Marked=" + marked + ". " + impact.detail() + " BlockedPlayers=" + impact.blockedPlayers() + ". " + scaleDetail(scaling));
    }

    private EffectResult arcaneSpark(Player player, AbilityService.AbilityDefinition definition, int slot,
                                     AbilityTargetingService.TargetResult target, AbilityScalingService.ScalingResult scaling) {
        Location location = targetLocation(player, target);
        double radius = radius(3.5, scaling);
        List<LivingEntity> targets = combat.nearbyPvETargets(location, player, radius);
        for (LivingEntity entity : targets) {
            applyPotion(entity, duration(80, scaling), 0, "SLOW", "SLOWNESS");
            applyPotion(entity, duration(80, scaling), 0, "GLOWING");
        }
        AbilityCombatService.CombatImpact impact = combat.damageTargets(player, targets, damage(2.5, scaling), "arcane_spark", scaling);
        safeSound(player, "BLOCK_AMETHYST_BLOCK_CHIME", 0.7f, 1.35f);
        safeParticle(player, location, "ENCHANT", particleCount(48, scaling), 0.85 * scaling.radiusMultiplier(), 0.9, 0.85 * scaling.radiusMultiplier(), 0.035);
        message(player, "&d" + definition.display() + " &7detonates through &f" + targetLabel(target) + "&7. &8[PvE hits " + impact.targetsAffected() + ", r" + round(radius) + "]");
        return new EffectResult("arcane_spark", "combat_integrated", "Arcane slow/glow. " + impact.detail() + " Radius=" + round(radius) + ". " + scaleDetail(scaling));
    }

    private EffectResult focusThread(Player player, AbilityService.AbilityDefinition definition, int slot,
                                     AbilityTargetingService.TargetResult target, AbilityScalingService.ScalingResult scaling) {
        LivingEntity aimed = aimedLivingTarget(player);
        boolean focused = applyPotion(aimed, duration(140, scaling), 0, "GLOWING");
        if (aimed != null && !(aimed instanceof Player)) {
            applyPotion(aimed, duration(60, scaling), 0, "SLOW", "SLOWNESS");
        }
        AbilityCombatService.CombatImpact impact = combat.damageOne(player, aimed, damage(1.5, scaling), "focus_thread", scaling);
        applyPotion(player, duration(120, scaling), amplifier(0, scaling), "SPEED");
        safeSound(player, "ENTITY_EXPERIENCE_ORB_PICKUP", 0.55f, 1.45f);
        safeParticle(player, targetLocation(player, target), "END_ROD", particleCount(24, scaling), 0.35, 0.55, 0.35, 0.0);
        message(player, "&e" + definition.display() + " &7threads combat focus into &f" + targetLabel(target) + "&7. &8[PvE hits " + impact.targetsAffected() + ", " + scaleTag(scaling) + "]");
        return new EffectResult("focus_thread", "combat_integrated", "Caster gained scaled Speed. Aimed entity focused=" + focused + ". " + impact.detail() + " " + scaleDetail(scaling));
    }

    private EffectResult unstableBloom(Player player, AbilityService.AbilityDefinition definition, int slot,
                                       AbilityTargetingService.TargetResult target, AbilityScalingService.ScalingResult scaling) {
        Location location = targetLocation(player, target);
        double radius = radius(AREA_RADIUS, scaling);
        List<LivingEntity> targets = combat.nearbyPvETargets(location, player, radius);
        for (LivingEntity entity : targets) {
            applyPotion(entity, duration(80, scaling), 0, "CONFUSION", "NAUSEA");
            applyPotion(entity, duration(80, scaling), 0, "SLOW", "SLOWNESS");
        }
        AbilityCombatService.CombatImpact impact = combat.damageTargets(player, targets, damage(1.75, scaling), "unstable_bloom", scaling);
        safeSound(player, "ENTITY_ENDERMAN_TELEPORT", 0.5f, 1.55f);
        safeParticle(player, location, "PORTAL", particleCount(64, scaling), 0.85 * scaling.radiusMultiplier(), 1.0, 0.85 * scaling.radiusMultiplier(), 0.1);
        message(player, "&5" + definition.display() + " &7blooms into controlled PvE disruption. &8[PvE hits " + impact.targetsAffected() + ", r" + round(radius) + "]");
        return new EffectResult("unstable_bloom", "combat_integrated", "Instability slow/confusion. " + impact.detail() + " Radius=" + round(radius) + ". " + scaleDetail(scaling));
    }

    private EffectResult staminaSurge(Player player, AbilityService.AbilityDefinition definition, int slot,
                                      AbilityTargetingService.TargetResult target, AbilityScalingService.ScalingResult scaling) {
        applyPotion(player, duration(100, scaling), amplifier(1, scaling), "SPEED");
        applyPotion(player, duration(80, scaling), 0, "JUMP", "JUMP_BOOST");
        try {
            Vector forward = player.getLocation().getDirection().normalize().multiply(0.65 * scaling.velocityMultiplier());
            forward.setY(Math.max(0.18, forward.getY()));
            player.setVelocity(forward);
        } catch (RuntimeException ignored) {
            // Velocity is nice, not sacred scripture.
        }
        safeSound(player, "ENTITY_HORSE_BREATHE", 0.45f, 1.2f);
        safeParticle(player, player.getLocation().add(0.0, 0.25, 0.0), "CLOUD", particleCount(28, scaling), 0.45, 0.25, 0.45, 0.02);
        message(player, "&a" + definition.display() + " &7surges with combat-ready movement. &8[" + scaleTag(scaling) + "]");
        return new EffectResult("stamina_surge", "applied_combat_safe", "Applied scaled Speed, Jump Boost, and movement impulse. No hostile damage route. " + scaleDetail(scaling));
    }

    private EffectResult resonancePing(Player player, AbilityService.AbilityDefinition definition, int slot,
                                       AbilityTargetingService.TargetResult target, AbilityScalingService.ScalingResult scaling) {
        applyPotion(player, duration(80, scaling), 0, "REGENERATION", "REGEN");
        safeSound(player, "BLOCK_NOTE_BLOCK_PLING", 0.45f, 1.2f);
        safeParticle(player, targetLocation(player, target), "END_ROD", particleCount(18, scaling), 0.45, 0.55, 0.45, 0.0);
        message(player, "&b" + definition.display() + " &7resonates into a stabilizing combat pulse. &8[" + scaleTag(scaling) + "]");
        return new EffectResult("resonance_ping", "applied_combat_safe", "Applied scaled minor Regeneration. No hostile damage route. " + scaleDetail(scaling));
    }

    private LivingEntity aimedLivingTarget(Player player) {
        if (player == null || player.getWorld() == null) {
            return null;
        }

        try {
            Location eye = player.getEyeLocation();
            Vector direction = eye.getDirection().normalize();
            Predicate<Entity> filter = entity -> !entity.getUniqueId().equals(player.getUniqueId()) && entity instanceof LivingEntity;
            RayTraceResult result = player.getWorld().rayTraceEntities(eye, direction, TARGET_RANGE, TARGET_RAY_SIZE, filter);
            if (result == null || !(result.getHitEntity() instanceof LivingEntity living)) {
                return null;
            }
            return living;
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private boolean applyPotion(LivingEntity entity, int durationTicks, int amplifier, String... names) {
        if (entity == null || entity.isDead()) {
            return false;
        }

        PotionEffectType type = potionType(names);
        if (type == null) {
            return false;
        }

        try {
            entity.addPotionEffect(new PotionEffect(type, durationTicks, amplifier, false, true, true));
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    private PotionEffectType potionType(String... names) {
        for (String name : names) {
            try {
                PotionEffectType type = PotionEffectType.getByName(name);
                if (type != null) {
                    return type;
                }
            } catch (RuntimeException ignored) {
                // Try the next ancient potion spelling. Bukkit is folklore with imports.
            }
        }
        return null;
    }

    private Location targetLocation(Player player, AbilityTargetingService.TargetResult target) {
        if (target == null || target.world() == null || player.getWorld() == null || !player.getWorld().getName().equals(target.world())) {
            return player.getLocation().add(0.0, 1.0, 0.0);
        }
        return new Location(player.getWorld(), target.x(), target.y(), target.z()).add(0.0, 0.75, 0.0);
    }

    private String targetLabel(AbilityTargetingService.TargetResult target) {
        if (target == null) {
            return "unknown target";
        }
        return target.description() + " &8[" + target.mode() + "]";
    }

    private String routeId(AbilityService.AbilityDefinition definition) {
        String id = safe(definition.id());
        String cost = safe(definition.costType());

        if (containsAny(id, "guard", "stance", "wall", "oath", "iron", "root", "bind")) {
            return "defensive_pulse";
        }

        if (containsAny(id, "blood", "grave", "mark", "ravaging", "shadow", "deadeye") || cost.equals("health") || cost.equals("hp")) {
            return "blood_pressure";
        }

        if (containsAny(id, "arc", "sigil", "null", "spark", "whisper", "echo_call") || cost.equals("mana") || cost.equals("arcane")) {
            return "arcane_spark";
        }

        if (containsAny(id, "thread", "sight", "gravity", "chain", "field", "step", "time_skip") || cost.equals("focus")) {
            return "focus_thread";
        }

        if (containsAny(id, "static", "anomaly", "wrong", "paradox") || cost.equals("instability")) {
            return "unstable_bloom";
        }

        if (cost.equals("stamina") || cost.equals("energy")) {
            return "stamina_surge";
        }

        return "resonance_ping";
    }

    private int duration(int baseTicks, AbilityScalingService.ScalingResult scaling) {
        return Math.max(20, (int) Math.round(baseTicks * scaling.durationMultiplier()));
    }

    private int amplifier(int baseAmplifier, AbilityScalingService.ScalingResult scaling) {
        return Math.max(baseAmplifier, baseAmplifier + (scaling.potencyMultiplier() >= 1.65 ? 1 : 0));
    }

    private double damage(double baseDamage, AbilityScalingService.ScalingResult scaling) {
        return round(baseDamage * scaling.potencyMultiplier());
    }

    private double radius(double baseRadius, AbilityScalingService.ScalingResult scaling) {
        return round(baseRadius * scaling.radiusMultiplier());
    }

    private int particleCount(int baseCount, AbilityScalingService.ScalingResult scaling) {
        return Math.max(baseCount, (int) Math.round(baseCount * Math.min(1.75, scaling.potencyMultiplier())));
    }

    private String scaleTag(AbilityScalingService.ScalingResult scaling) {
        return scaling.role() + " x" + scaling.potencyMultiplier();
    }

    private String scaleDetail(AbilityScalingService.ScalingResult scaling) {
        return "Scaling role=" + scaling.role()
                + ", potency=" + scaling.potencyMultiplier()
                + ", duration=" + scaling.durationMultiplier()
                + ", radius=" + scaling.radiusMultiplier()
                + ".";
    }

    private boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private void message(Player player, String message) {
        player.sendMessage(prefix() + Text.color(message));
    }

    private void safeSound(Player player, String soundName, float volume, float pitch) {
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException ignored) {
            plugin.getLogger().fine("Sound route not available on this server version: " + soundName);
        }
    }

    private void safeParticle(Player player, Location location, String particleName, int count, double offsetX, double offsetY, double offsetZ, double extra) {
        try {
            Particle particle = Particle.valueOf(particleName);
            player.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
        } catch (IllegalArgumentException ignored) {
            plugin.getLogger().fine("Particle route not available on this server version: " + particleName);
        }
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase().trim().replace(" ", "_").replace("-", "_");
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }

    public record EffectResult(
            String effectId,
            String status,
            String detail
    ) {
    }
}
