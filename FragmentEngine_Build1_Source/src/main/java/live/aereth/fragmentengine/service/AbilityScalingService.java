package live.aereth.fragmentengine.service;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Locale;

public class AbilityScalingService {
    public ScalingResult scale(YamlConfiguration character, AbilityService.AbilityDefinition definition, int slot) {
        int level = Math.max(1, character.getInt("progression.level", 1));
        int rank = Math.max(0, character.getInt("discipline.progression.rank", 0));
        String discipline = normalize(character.getString("discipline.id", definition.discipline()));
        String role = roleFor(discipline, definition);

        double levelBonus = Math.min(0.30, Math.max(0.0, (level - 1) * 0.005));
        double rankBonus = Math.min(0.36, Math.max(0.0, rank * 0.045));
        double unlockBonus = Math.max(0.0, (rank - definition.unlockRank()) * 0.035);

        double potency = 1.0 + levelBonus + rankBonus + unlockBonus;
        double duration = 1.0 + levelBonus * 0.45 + rankBonus * 0.70;
        double radius = 1.0 + levelBonus * 0.35 + rankBonus * 0.35;
        double velocity = 1.0 + levelBonus * 0.35 + rankBonus * 0.45;
        double cooldown = 1.0 - Math.min(0.18, rank * 0.025) - Math.min(0.10, levelBonus * 0.35);
        double cost = 1.0 + Math.min(0.18, levelBonus * 0.40 + rankBonus * 0.18);

        switch (role) {
            case "defender" -> {
                duration += 0.18;
                radius += 0.08;
                cooldown -= 0.03;
                cost -= 0.04;
            }
            case "striker" -> {
                potency += 0.16;
                velocity += 0.08;
                cost += 0.05;
            }
            case "precision" -> {
                potency += 0.12;
                cooldown -= 0.06;
                radius -= 0.04;
            }
            case "control" -> {
                duration += 0.20;
                radius += 0.16;
                cost += 0.03;
            }
            case "arcane" -> {
                potency += 0.10;
                radius += 0.12;
                cost += 0.06;
            }
            case "temporal" -> {
                cooldown -= 0.11;
                duration += 0.12;
                velocity += 0.05;
            }
            case "support" -> {
                duration += 0.24;
                cost -= 0.07;
                potency += 0.04;
            }
            case "occult" -> {
                potency += 0.09;
                duration += 0.14;
                cost -= 0.03;
            }
            case "technical" -> {
                radius += 0.20;
                cooldown -= 0.05;
                potency += 0.05;
            }
            case "aberrant" -> {
                potency += 0.13;
                radius += 0.12;
                cooldown += 0.04;
                cost -= 0.05;
            }
            default -> {
                // Balanced fallback. Glamorous, no. Safe, yes.
            }
        }

        potency = clamp(potency, 1.0, 2.40);
        duration = clamp(duration, 1.0, 2.10);
        radius = clamp(radius, 0.75, 1.85);
        velocity = clamp(velocity, 1.0, 1.80);
        cooldown = clamp(cooldown, 0.62, 1.20);
        cost = clamp(cost, 0.70, 1.35);

        double scaledCost = round(definition.costAmount() <= 0.0 ? 0.0 : definition.costAmount() * cost);
        double scaledCooldown = round(Math.max(0.0, definition.cooldownSeconds() * cooldown));

        String identity = identityLine(role, discipline);

        return new ScalingResult(
                definition.id(),
                discipline,
                role,
                slot,
                level,
                rank,
                definition.unlockRank(),
                round(potency),
                round(duration),
                round(radius),
                round(velocity),
                round(cooldown),
                round(cost),
                scaledCost,
                scaledCooldown,
                identity,
                "scaled_by_level_rank_and_discipline_role"
        );
    }

    public static ScalingResult neutral(AbilityService.AbilityDefinition definition, int slot) {
        return new ScalingResult(
                definition.id(),
                definition.discipline(),
                "neutral",
                slot,
                1,
                definition.unlockRank(),
                definition.unlockRank(),
                1.0,
                1.0,
                1.0,
                1.0,
                1.0,
                1.0,
                roundStatic(definition.costAmount()),
                roundStatic(definition.cooldownSeconds()),
                "Neutral effect routing.",
                "neutral_scaling"
        );
    }

    private String roleFor(String discipline, AbilityService.AbilityDefinition definition) {
        String id = normalize(definition.id());
        String cost = normalize(definition.costType());
        String pool = discipline + "_" + id;

        if (containsAny(pool, "vanguard", "sentinel", "warden", "bulwark", "iron", "oath", "guardian", "wall", "root")) {
            return "defender";
        }
        if (containsAny(pool, "reaver", "assassin", "skirmisher", "ravager", "blood", "shadow", "cut") || cost.equals("health") || cost.equals("hp")) {
            return "striker";
        }
        if (containsAny(pool, "marksman", "deadeye", "siege", "shot", "sight")) {
            return "precision";
        }
        if (containsAny(pool, "controller", "suppressor", "binder", "chain", "silence", "field", "gravity")) {
            return "control";
        }
        if (containsAny(pool, "spell", "arcanist", "void", "arc", "sigil", "spark") || cost.equals("mana") || cost.equals("arcane")) {
            return "arcane";
        }
        if (containsAny(pool, "chrono", "fate", "time", "paradox", "thread")) {
            return "temporal";
        }
        if (containsAny(pool, "vitalist", "sanctifier", "lifeshaper", "mend", "renew", "sanctuary")) {
            return "support";
        }
        if (containsAny(pool, "necro", "grave", "summon", "echo", "flesh", "bone")) {
            return "occult";
        }
        if (containsAny(pool, "warforger", "architect", "oracle", "frame", "forge", "construct")) {
            return "technical";
        }
        if (containsAny(pool, "anomaly", "wrong", "static", "unstable") || cost.equals("instability")) {
            return "aberrant";
        }
        return "balanced";
    }

    private String identityLine(String role, String discipline) {
        String display = title(discipline);
        return switch (role) {
            case "defender" -> display + " scales toward longer protection and steadier uptime.";
            case "striker" -> display + " scales toward harder pressure and burst commitment.";
            case "precision" -> display + " scales toward sharper output and shorter recovery.";
            case "control" -> display + " scales toward longer disruption and wider control.";
            case "arcane" -> display + " scales toward stronger resonance and broader arcane reach.";
            case "temporal" -> display + " scales toward cooldown compression and sustained timing windows.";
            case "support" -> display + " scales toward efficient sustain and longer boons.";
            case "occult" -> display + " scales through pressure, decay, and lingering effects.";
            case "technical" -> display + " scales through engineered area and clean routing.";
            case "aberrant" -> display + " scales through unstable force and strange reach.";
            default -> display + " scales evenly by level and rank.";
        };
    }

    private String title(String raw) {
        String clean = normalize(raw).replace("_", " ");
        String[] words = clean.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(" ");
            }
            builder.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1));
        }
        return builder.isEmpty() ? "Unformed" : builder.toString();
    }

    private boolean containsAny(String value, String... needles) {
        for (String needle : needles) {
            if (value.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private String normalize(String raw) {
        return raw == null ? "" : raw.toLowerCase(Locale.ROOT).trim().replace(" ", "_").replace("-", "_");
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double round(double value) {
        return roundStatic(value);
    }

    private static double roundStatic(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public record ScalingResult(
            String abilityId,
            String discipline,
            String role,
            int slot,
            int level,
            int rank,
            int unlockRank,
            double potencyMultiplier,
            double durationMultiplier,
            double radiusMultiplier,
            double velocityMultiplier,
            double cooldownMultiplier,
            double costMultiplier,
            double scaledCostAmount,
            double scaledCooldownSeconds,
            String identityLine,
            String status
    ) {
    }
}
