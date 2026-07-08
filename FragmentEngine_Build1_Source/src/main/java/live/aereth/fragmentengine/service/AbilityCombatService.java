package live.aereth.fragmentengine.service;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AbilityCombatService {
    private static final double MAX_SINGLE_DAMAGE = 12.0;
    private static final double MAX_AREA_DAMAGE = 8.0;

    private final JavaPlugin plugin;
    private final NamespacedKey lastRouteKey;
    private final NamespacedKey lastCasterKey;
    private final NamespacedKey lastDamageKey;

    public AbilityCombatService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.lastRouteKey = new NamespacedKey(plugin, "ability_last_route");
        this.lastCasterKey = new NamespacedKey(plugin, "ability_last_caster");
        this.lastDamageKey = new NamespacedKey(plugin, "ability_last_damage");
    }

    public CombatImpact damageOne(Player caster, LivingEntity target, double amount, String route,
                                  AbilityScalingService.ScalingResult scaling) {
        String cleanRoute = safe(route, "ability");
        double safeAmount = clampDamage(amount, MAX_SINGLE_DAMAGE);

        if (target == null || target.isDead()) {
            return new CombatImpact(cleanRoute, "single", 0, 0.0, 0, "no_target", "No valid target.");
        }

        if (!canAffect(caster, target)) {
            return new CombatImpact(cleanRoute, "single", 0, 0.0, target instanceof Player ? 1 : 0,
                    "blocked", "Target blocked by PvP/safety rules.");
        }

        boolean applied = applyDamage(caster, target, safeAmount);
        if (!applied) {
            return new CombatImpact(cleanRoute, "single", 0, 0.0, 0, "failed", "Damage call failed safely.");
        }

        tag(target, caster, cleanRoute, safeAmount);
        return new CombatImpact(cleanRoute, "single", 1, safeAmount, 0, "applied", detail(cleanRoute, 1, safeAmount, scaling));
    }

    public CombatImpact damageTargets(Player caster, List<LivingEntity> targets, double amount, String route,
                                      AbilityScalingService.ScalingResult scaling) {
        String cleanRoute = safe(route, "ability");
        double safeAmount = clampDamage(amount, MAX_AREA_DAMAGE);
        if (targets == null || targets.isEmpty()) {
            return new CombatImpact(cleanRoute, "targets", 0, 0.0, 0, "no_targets", "No valid targets.");
        }

        int affected = 0;
        int blockedPlayers = 0;
        double totalDamage = 0.0;

        for (LivingEntity target : targets) {
            if (target == null || target.isDead()) {
                continue;
            }
            if (!canAffect(caster, target)) {
                if (target instanceof Player) {
                    blockedPlayers++;
                }
                continue;
            }
            if (applyDamage(caster, target, safeAmount)) {
                tag(target, caster, cleanRoute, safeAmount);
                affected++;
                totalDamage += safeAmount;
            }
        }

        String status = affected > 0 ? "applied" : blockedPlayers > 0 ? "blocked" : "no_targets";
        String message = affected > 0
                ? detail(cleanRoute, affected, totalDamage, scaling)
                : "No PvE targets accepted damage.";
        return new CombatImpact(cleanRoute, "targets", affected, round(totalDamage), blockedPlayers, status, message);
    }

    public List<LivingEntity> nearbyPvETargets(Location center, Player caster, double radius) {
        List<LivingEntity> targets = new ArrayList<>();
        if (center == null || center.getWorld() == null) {
            return targets;
        }

        double cleanRadius = Math.max(0.5, Math.min(12.0, radius));
        for (Entity entity : center.getWorld().getNearbyEntities(center, cleanRadius, cleanRadius, cleanRadius)) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            if (caster != null && entity.getUniqueId().equals(caster.getUniqueId())) {
                continue;
            }
            if (!canAffect(caster, living)) {
                continue;
            }
            targets.add(living);
        }
        return targets;
    }

    public boolean canAffect(Player caster, LivingEntity target) {
        if (target == null || target.isDead()) {
            return false;
        }
        if (caster != null && target.getUniqueId().equals(caster.getUniqueId())) {
            return false;
        }
        return !(target instanceof Player);
    }

    private boolean applyDamage(Player caster, LivingEntity target, double amount) {
        try {
            target.damage(amount, caster);
            return true;
        } catch (RuntimeException ex) {
            plugin.getLogger().fine("Ability combat damage failed safely: " + ex.getMessage());
            return false;
        }
    }

    private void tag(LivingEntity target, Player caster, String route, double amount) {
        try {
            PersistentDataContainer data = target.getPersistentDataContainer();
            data.set(lastRouteKey, PersistentDataType.STRING, route);
            data.set(lastDamageKey, PersistentDataType.STRING, String.format(Locale.US, "%.2f", amount));
            if (caster != null) {
                data.set(lastCasterKey, PersistentDataType.STRING, caster.getUniqueId().toString());
            }
        } catch (RuntimeException ignored) {
            // Tags are useful, not worth crashing combat over. Imagine that, restraint.
        }
    }

    private double clampDamage(double amount, double max) {
        return round(Math.max(0.0, Math.min(max, amount)));
    }

    private String detail(String route, int affected, double damage, AbilityScalingService.ScalingResult scaling) {
        String role = scaling == null ? "neutral" : scaling.role();
        double potency = scaling == null ? 1.0 : scaling.potencyMultiplier();
        return route + " affected " + affected + " PvE target" + (affected == 1 ? "" : "s")
                + " for " + round(damage) + " total damage. Role=" + role + ", potency=" + potency + ".";
    }

    private String safe(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.toLowerCase(Locale.ROOT).trim().replace(" ", "_").replace("-", "_");
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record CombatImpact(
            String route,
            String mode,
            int targetsAffected,
            double damageApplied,
            int blockedPlayers,
            String status,
            String detail
    ) {
    }
}
