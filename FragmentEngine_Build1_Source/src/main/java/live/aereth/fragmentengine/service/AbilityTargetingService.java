package live.aereth.fragmentengine.service;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Locale;
import java.util.function.Predicate;

public class AbilityTargetingService {
    private static final double AIMED_ENTITY_RANGE = 18.0;
    private static final double AIMED_ENTITY_RAY_SIZE = 0.85;
    private static final double TARGET_BLOCK_RANGE = 18.0;
    private static final double FORWARD_LINE_RANGE = 9.0;
    private static final double AREA_RANGE = 6.0;

    public TargetResult resolve(Player player, AbilityService.AbilityDefinition definition, int slot) {
        String mode = targetMode(definition);

        if (player == null || !player.isOnline()) {
            return new TargetResult(
                    mode,
                    "offline_record_only",
                    "No online player target context.",
                    "none",
                    "none",
                    "none",
                    0.0,
                    "offline",
                    0.0,
                    0.0,
                    0.0
            );
        }

        return switch (mode) {
            case "aimed_entity" -> aimedEntity(player, definition, slot);
            case "aimed_location" -> aimedLocation(player, definition, slot);
            case "area" -> areaAroundPlayer(player, definition, slot);
            case "forward_line" -> forwardLine(player, definition, slot);
            default -> self(player, definition, slot);
        };
    }

    public String targetMode(AbilityService.AbilityDefinition definition) {
        String id = safe(definition.id());
        String cost = safe(definition.costType());

        if (containsAny(id, "guardian", "stance", "wall", "oath", "mend", "quickstep", "time_skip", "clear_sight")) {
            return "self";
        }

        if (containsAny(id, "deadeye", "shadow", "cut", "mark", "hook")) {
            return "aimed_entity";
        }

        if (containsAny(id, "root", "bind", "chain", "silence", "field_frame", "field")) {
            return "aimed_location";
        }

        if (containsAny(id, "static", "bloom", "anomaly", "wrong", "paradox", "grave", "echo_call")) {
            return "area";
        }

        if (containsAny(id, "linebreaker", "thread", "arc", "spark", "sigil") || cost.equals("mana")) {
            return "forward_line";
        }

        return "self";
    }

    private TargetResult self(Player player, AbilityService.AbilityDefinition definition, int slot) {
        Location location = player.getLocation();
        return atLocation(
                "self",
                "resolved_self",
                "Self: " + player.getName(),
                "player",
                player.getName(),
                player.getType().name().toLowerCase(Locale.ROOT),
                0.0,
                location
        );
    }

    private TargetResult aimedEntity(Player player, AbilityService.AbilityDefinition definition, int slot) {
        Entity target = rayTraceEntity(player, AIMED_ENTITY_RANGE);
        if (target != null) {
            Location location = target.getLocation();
            return atLocation(
                    "aimed_entity",
                    "resolved_entity",
                    describeEntity(target),
                    "entity",
                    entityName(target),
                    target.getType().name().toLowerCase(Locale.ROOT),
                    player.getLocation().distance(location),
                    location
            );
        }

        TargetResult fallback = aimedLocation(player, definition, slot);
        return new TargetResult(
                "aimed_entity",
                "fallback_location",
                "No entity hit. Fallback: " + fallback.description(),
                "location",
                "none",
                "fallback",
                fallback.distance(),
                fallback.world(),
                fallback.x(),
                fallback.y(),
                fallback.z()
        );
    }

    private TargetResult aimedLocation(Player player, AbilityService.AbilityDefinition definition, int slot) {
        Block block = player.getTargetBlockExact((int) TARGET_BLOCK_RANGE);
        Location location;
        String status;
        String detail;

        if (block != null) {
            location = block.getLocation().add(0.5, 1.0, 0.5);
            status = "resolved_block";
            detail = "Block: " + block.getType().name().toLowerCase(Locale.ROOT);
        } else {
            location = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(FORWARD_LINE_RANGE));
            status = "fallback_forward_point";
            detail = "Forward point";
        }

        return atLocation(
                "aimed_location",
                status,
                detail,
                "location",
                "none",
                "location",
                player.getLocation().distance(location),
                location
        );
    }

    private TargetResult forwardLine(Player player, AbilityService.AbilityDefinition definition, int slot) {
        Location location = player.getEyeLocation().add(player.getEyeLocation().getDirection().normalize().multiply(FORWARD_LINE_RANGE));
        return atLocation(
                "forward_line",
                "resolved_forward_line",
                "Line endpoint " + shortCoords(location),
                "location",
                "none",
                "line",
                FORWARD_LINE_RANGE,
                location
        );
    }

    private TargetResult areaAroundPlayer(Player player, AbilityService.AbilityDefinition definition, int slot) {
        Location location = player.getLocation();
        long nearby = player.getWorld().getNearbyEntities(location, AREA_RANGE, AREA_RANGE, AREA_RANGE).stream()
                .filter(entity -> !entity.getUniqueId().equals(player.getUniqueId()))
                .filter(entity -> entity instanceof LivingEntity)
                .count();

        return atLocation(
                "area",
                "resolved_area",
                "Area around player: " + nearby + " nearby living entities",
                "area",
                "none",
                "sphere",
                AREA_RANGE,
                location
        );
    }

    private Entity rayTraceEntity(Player player, double range) {
        try {
            Location eye = player.getEyeLocation();
            Vector direction = eye.getDirection().normalize();
            Predicate<Entity> filter = entity -> !entity.getUniqueId().equals(player.getUniqueId()) && entity instanceof LivingEntity;
            RayTraceResult result = player.getWorld().rayTraceEntities(eye, direction, range, AIMED_ENTITY_RAY_SIZE, filter);
            return result == null ? null : result.getHitEntity();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private TargetResult atLocation(String mode, String status, String description, String targetType, String targetName,
                                    String entityType, double distance, Location location) {
        return new TargetResult(
                mode,
                status,
                description,
                targetType,
                targetName,
                entityType,
                round(distance),
                location.getWorld() == null ? "unknown" : location.getWorld().getName(),
                round(location.getX()),
                round(location.getY()),
                round(location.getZ())
        );
    }

    private String describeEntity(Entity entity) {
        return entityName(entity) + " (" + entity.getType().name().toLowerCase(Locale.ROOT) + ")";
    }

    private String entityName(Entity entity) {
        if (entity.getCustomName() != null && !entity.getCustomName().isBlank()) {
            return entity.getCustomName();
        }
        return entity.getName();
    }

    private String shortCoords(Location location) {
        return "x=" + round(location.getX()) + ", y=" + round(location.getY()) + ", z=" + round(location.getZ());
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim().replace(" ", "_").replace("-", "_");
    }

    private boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    public record TargetResult(
            String mode,
            String status,
            String description,
            String targetType,
            String targetName,
            String entityType,
            double distance,
            String world,
            double x,
            double y,
            double z
    ) {
    }
}
