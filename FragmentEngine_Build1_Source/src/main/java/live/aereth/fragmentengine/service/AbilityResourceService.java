package live.aereth.fragmentengine.service;

import live.aereth.fragmentengine.util.TimeUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public class AbilityResourceService {
    private static final String BASE_PATH = "abilities.resources";
    private static final double HEALTH_RESERVE = 2.0;

    public ResourceSnapshot snapshot(YamlConfiguration character, Player player) {
        refresh(character, player);
        return new ResourceSnapshot(
                pool(character, "stamina"),
                pool(character, "mana"),
                pool(character, "focus"),
                pool(character, "instability"),
                healthPool(player)
        );
    }

    public CostPreview preview(YamlConfiguration character, Player player, AbilityService.AbilityDefinition definition) {
        return previewAmount(character, player, definition, Math.max(0.0, definition.costAmount()), "base_cost");
    }

    public CostPreview preview(YamlConfiguration character, Player player, AbilityService.AbilityDefinition definition,
                               AbilityScalingService.ScalingResult scaling) {
        double amount = scaling == null ? Math.max(0.0, definition.costAmount()) : Math.max(0.0, scaling.scaledCostAmount());
        String status = scaling == null ? "base_cost" : "scaled_cost:" + scaling.role();
        return previewAmount(character, player, definition, amount, status);
    }

    public CostPayment consume(YamlConfiguration character, Player player, AbilityService.AbilityDefinition definition) {
        return consumePreview(character, player, definition, preview(character, player, definition));
    }

    public CostPayment consume(YamlConfiguration character, Player player, AbilityService.AbilityDefinition definition,
                               AbilityScalingService.ScalingResult scaling) {
        return consumePreview(character, player, definition, preview(character, player, definition, scaling));
    }

    public void refresh(YamlConfiguration character, Player player) {
        long now = System.currentTimeMillis();
        for (String type : List.of("stamina", "mana", "focus", "instability")) {
            refreshPool(character, type, now);
        }

        if (player != null && player.isOnline()) {
            character.set(BASE_PATH + ".health.current", round(player.getHealth()));
            character.set(BASE_PATH + ".health.max", round(maxHealth(player)));
            character.set(BASE_PATH + ".health.status", "online_player_health");
            character.set(BASE_PATH + ".health.last-updated-ms", now);
        }
    }

    private CostPreview previewAmount(YamlConfiguration character, Player player, AbilityService.AbilityDefinition definition,
                                      double amount, String scalingStatus) {
        String type = normalize(definition.costType());
        amount = round(Math.max(0.0, amount));

        if (type.isBlank() || type.equals("none") || amount <= 0.0) {
            return new CostPreview("none", 0.0, 0.0, 0.0, true, "No resource cost.", "None", scalingStatus);
        }

        if (type.equals("hp")) {
            type = "health";
        }

        refresh(character, player);

        if (type.equals("health")) {
            if (player == null || !player.isOnline()) {
                return new CostPreview(type, amount, 0.0, 0.0, false, "Health-cost abilities require an online player.", display(type, amount, 0.0, 0.0), scalingStatus);
            }

            double current = round(player.getHealth());
            double max = round(maxHealth(player));
            boolean affordable = current - amount >= HEALTH_RESERVE;
            String detail = affordable
                    ? "Health cost is safe."
                    : "Not enough health. Minimum reserve is " + format(HEALTH_RESERVE) + ".";
            return new CostPreview(type, amount, current, max, affordable, detail, display(type, amount, current, max), scalingStatus);
        }

        Pool pool = pool(character, type);
        boolean affordable = pool.current() >= amount;
        String detail = affordable
                ? "Enough " + type + "."
                : "Need " + format(amount - pool.current()) + " more " + type + ".";
        return new CostPreview(type, amount, pool.current(), pool.max(), affordable, detail, display(type, amount, pool.current(), pool.max()), scalingStatus);
    }

    private CostPayment consumePreview(YamlConfiguration character, Player player, AbilityService.AbilityDefinition definition, CostPreview preview) {
        if (!preview.affordable()) {
            throw new IllegalStateException("Not enough " + preview.type() + " for " + definition.display() + ": " + preview.detail());
        }

        if (preview.type().equals("none")) {
            writeLastCost(character, preview.type(), 0.0, 0.0, 0.0, "no_cost", preview.scalingStatus());
            return new CostPayment(preview.type(), 0.0, 0.0, 0.0, "no_cost", preview.scalingStatus());
        }

        if (preview.type().equals("health")) {
            double before = player.getHealth();
            double after = Math.max(HEALTH_RESERVE, before - preview.amount());
            player.setHealth(Math.min(maxHealth(player), after));
            CostPayment payment = new CostPayment("health", preview.amount(), round(player.getHealth()), preview.max(), "paid_health", preview.scalingStatus());
            writeLastCost(character, payment.type(), payment.amount(), payment.remaining(), payment.max(), payment.status(), payment.scalingStatus());
            return payment;
        }

        String path = BASE_PATH + "." + preview.type();
        double remaining = Math.max(0.0, preview.current() - preview.amount());
        character.set(path + ".current", round(remaining));
        character.set(path + ".last-spent", preview.amount());
        character.set(path + ".last-spent-at", TimeUtil.nowIso());
        character.set(path + ".last-updated-ms", System.currentTimeMillis());
        character.set(path + ".status", "spent");
        character.set(path + ".scaling-status", preview.scalingStatus());

        CostPayment payment = new CostPayment(preview.type(), preview.amount(), round(remaining), preview.max(), "paid_resource", preview.scalingStatus());
        writeLastCost(character, payment.type(), payment.amount(), payment.remaining(), payment.max(), payment.status(), payment.scalingStatus());
        return payment;
    }

    private void refreshPool(YamlConfiguration character, String type, long now) {
        String path = BASE_PATH + "." + type;
        double max = maxFor(character, type);
        double regen = regenFor(character, type);
        long lastUpdated = character.getLong(path + ".last-updated-ms", 0L);
        double current = character.contains(path + ".current") ? character.getDouble(path + ".current", max) : max;

        if (lastUpdated <= 0L) {
            current = max;
            lastUpdated = now;
        } else if (now > lastUpdated) {
            double seconds = Math.min(30.0, (now - lastUpdated) / 1000.0);
            current = Math.min(max, current + regen * seconds);
        }

        character.set(path + ".current", round(current));
        character.set(path + ".max", round(max));
        character.set(path + ".regen-per-second", round(regen));
        character.set(path + ".last-updated-ms", now);
        character.set(path + ".status", "ready");
    }

    private Pool pool(YamlConfiguration character, String rawType) {
        String type = normalize(rawType);
        if (type.equals("hp")) {
            type = "health";
        }

        String path = BASE_PATH + "." + type;
        double max = round(character.getDouble(path + ".max", maxFor(character, type)));
        double current = round(character.getDouble(path + ".current", max));
        double regen = round(character.getDouble(path + ".regen-per-second", regenFor(character, type)));
        return new Pool(type, current, max, regen);
    }

    private Pool healthPool(Player player) {
        if (player == null || !player.isOnline()) {
            return new Pool("health", 0.0, 0.0, 0.0);
        }
        return new Pool("health", round(player.getHealth()), round(maxHealth(player)), 0.0);
    }

    private double maxFor(YamlConfiguration character, String type) {
        int level = Math.max(1, character.getInt("progression.level", 1));
        int rank = Math.max(0, character.getInt("discipline.progression.rank", 0));

        return switch (normalize(type)) {
            case "mana" -> 90.0 + level * 2.0 + rank * 5.0;
            case "focus" -> 85.0 + level * 2.0 + rank * 5.0;
            case "instability" -> 75.0 + level * 1.5 + rank * 4.0;
            case "health", "hp" -> 20.0;
            default -> 100.0 + level * 2.0 + rank * 4.0;
        };
    }

    private double regenFor(YamlConfiguration character, String type) {
        int rank = Math.max(0, character.getInt("discipline.progression.rank", 0));
        return switch (normalize(type)) {
            case "mana" -> 4.0 + rank * 0.25;
            case "focus" -> 5.0 + rank * 0.30;
            case "instability" -> 3.0 + rank * 0.20;
            default -> 7.0 + rank * 0.35;
        };
    }

    private double maxHealth(Player player) {
        if (player == null) {
            return 20.0;
        }

        try {
            AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) {
                return attribute.getValue();
            }
        } catch (RuntimeException ignored) {
            // Fallback below. Bukkit compatibility is a small museum of footnotes.
        }

        return 20.0;
    }

    private void writeLastCost(YamlConfiguration character, String type, double amount, double remaining, double max, String status, String scalingStatus) {
        character.set("abilities.activation.last.resource.type", type);
        character.set("abilities.activation.last.resource.amount", round(amount));
        character.set("abilities.activation.last.resource.remaining", round(remaining));
        character.set("abilities.activation.last.resource.max", round(max));
        character.set("abilities.activation.last.resource.status", status);
        character.set("abilities.activation.last.resource.scaling-status", scalingStatus);
    }

    private String display(String type, double amount, double current, double max) {
        return format(amount) + " " + type + " &8(" + format(current) + "/" + format(max) + ")";
    }

    private String normalize(String raw) {
        return raw == null ? "" : raw.toLowerCase(Locale.ROOT).trim().replace(" ", "_").replace("-", "_");
    }

    private String format(double value) {
        return String.format(Locale.US, "%.1f", round(value));
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public record Pool(String type, double current, double max, double regenPerSecond) {
        public String line() {
            return String.format(Locale.US, "%.1f/%.1f", current, max);
        }
    }

    public record ResourceSnapshot(Pool stamina, Pool mana, Pool focus, Pool instability, Pool health) {
    }

    public record CostPreview(
            String type,
            double amount,
            double current,
            double max,
            boolean affordable,
            String detail,
            String display,
            String scalingStatus
    ) {
    }

    public record CostPayment(
            String type,
            double amount,
            double remaining,
            double max,
            String status,
            String scalingStatus
    ) {
    }
}
