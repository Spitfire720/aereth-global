package live.aereth.fragmentengine.service;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatsService {
    public Map<String, Double> defaultStatsForLevel(YamlConfiguration character, int level) {
        Map<String, Double> stats = new LinkedHashMap<>();

        double vitality = getOrDefault(character, "stats.vitality", 5.0);
        double strength = getOrDefault(character, "stats.strength", 5.0);
        double dexterity = getOrDefault(character, "stats.dexterity", 5.0);
        double intelligence = getOrDefault(character, "stats.intelligence", 5.0);
        double willpower = getOrDefault(character, "stats.willpower", 5.0);
        double endurance = getOrDefault(character, "stats.endurance", 5.0);

        if (!character.contains("stats")) {
            int extra = Math.max(0, level - 1);
            vitality = 5.0 + 0.8 * extra;
            strength = 5.0 + 0.75 * extra;
            dexterity = 5.0 + 0.55 * extra;
            intelligence = 5.0 + 0.55 * extra;
            willpower = 5.0 + 0.6 * extra;
            endurance = 5.0 + 0.7 * extra;
        }

        stats.put("vitality", round(vitality));
        stats.put("strength", round(strength));
        stats.put("dexterity", round(dexterity));
        stats.put("intelligence", round(intelligence));
        stats.put("willpower", round(willpower));
        stats.put("endurance", round(endurance));
        return stats;
    }

    public Map<String, Double> derivedStats(Map<String, Double> stats, double erasurePressure) {
        double vitality = stats.getOrDefault("vitality", 0.0);
        double strength = stats.getOrDefault("strength", 0.0);
        double dexterity = stats.getOrDefault("dexterity", 0.0);
        double intelligence = stats.getOrDefault("intelligence", 0.0);
        double willpower = stats.getOrDefault("willpower", 0.0);
        double endurance = stats.getOrDefault("endurance", 0.0);

        Map<String, Double> derived = new LinkedHashMap<>();
        derived.put("max-health", round(100 + vitality * 12 + endurance * 6));
        derived.put("attack-power", round(10 + strength * 2));
        derived.put("defense", round(endurance * 1.5));
        derived.put("crit-chance", round4(0.05 + dexterity * 0.002));
        derived.put("crit-damage", round4(1.5 + dexterity * 0.01));
        derived.put("magic-power", round(intelligence * 2));
        derived.put("resistance", round(willpower * 1.25));
        derived.put("movement-speed", 1.0);
        derived.put("stability", round(clamp(100 - erasurePressure + willpower * 0.5, 0, 100)));
        return derived;
    }

    public void applyStatsAndDerived(YamlConfiguration character) {
        int level = character.getInt("progression.level", 1);
        double erasure = character.getDouble("erasure", character.getDouble("fragments.erasure-pressure", 0.0));

        Map<String, Double> stats = defaultStatsForLevel(character, level);
        for (Map.Entry<String, Double> entry : stats.entrySet()) {
            character.set("stats." + entry.getKey(), entry.getValue());
        }

        Map<String, Double> derived = derivedStats(stats, erasure);
        for (Map.Entry<String, Double> entry : derived.entrySet()) {
            character.set("derived." + entry.getKey(), entry.getValue());
        }

        if (!character.contains("derived.current-health")) {
            character.set("derived.current-health", derived.get("max-health"));
        }
    }

    private double getOrDefault(ConfigurationSection section, String path, double fallback) {
        return section.contains(path) ? section.getDouble(path) : fallback;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double round4(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
