package live.aereth.fragmentengine.service;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;

public class StatsService {
    private final FileConfiguration config;
    private final RaceService races;

    public StatsService(FileConfiguration config, RaceService races) {
        this.config = config;
        this.races = races;
    }

    public Map<String, Double> defaultStatsForLevel(YamlConfiguration character, int level) {
        Map<String, Double> totals = new LinkedHashMap<>();
        String race = character.getString("race.id", "remnant");

        putStat(character, totals, race, level, "vitality");
        putStat(character, totals, race, level, "strength");
        putStat(character, totals, race, level, "dexterity");
        putStat(character, totals, race, level, "intelligence");
        putStat(character, totals, race, level, "willpower");
        putStat(character, totals, race, level, "endurance");

        return totals;
    }

    private void putStat(YamlConfiguration character, Map<String, Double> totals, String race, int level, String stat) {
        double starting = config.getDouble("stats.starting." + stat, 5.0);
        double growth = config.getDouble("stats.per-level-growth." + stat, 0.5);
        double base = starting + Math.max(0, level - 1) * growth;
        double raceMod = races.statModifier(race, stat);
        double bonus = character.getDouble("stats.bonus." + stat, 0.0);
        double total = Math.max(0.0, base + raceMod + bonus);

        character.set("stats.base." + stat, round(base));
        character.set("stats.race." + stat, round(raceMod));
        character.set("stats.bonus." + stat, round(bonus));
        character.set("stats.total." + stat, round(total));
        character.set("stats." + stat, round(total));
        totals.put(stat, round(total));
    }

    public Map<String, Double> derivedStats(YamlConfiguration character, Map<String, Double> stats, double erasurePressure) {
        String race = character.getString("race.id", "remnant");
        double vitality = stats.getOrDefault("vitality", 0.0);
        double strength = stats.getOrDefault("strength", 0.0);
        double dexterity = stats.getOrDefault("dexterity", 0.0);
        double intelligence = stats.getOrDefault("intelligence", 0.0);
        double willpower = stats.getOrDefault("willpower", 0.0);
        double endurance = stats.getOrDefault("endurance", 0.0);

        Map<String, Double> derived = new LinkedHashMap<>();
        derived.put("max-health", round(100 + vitality * 12 + endurance * 6 + races.derivedModifier(race, "max-health")));
        derived.put("attack-power", round(10 + strength * 2 + races.derivedModifier(race, "attack-power")));
        derived.put("defense", round(endurance * 1.5 + races.derivedModifier(race, "defense")));
        derived.put("crit-chance", round4(0.05 + dexterity * 0.002 + races.derivedModifier(race, "crit-chance")));
        derived.put("crit-damage", round4(1.5 + dexterity * 0.01));
        derived.put("magic-power", round(intelligence * 2 + races.derivedModifier(race, "magic-power")));
        derived.put("resistance", round(willpower * 1.25 + races.derivedModifier(race, "resistance")));
        derived.put("evasion", round4(0.02 + dexterity * 0.0015));
        derived.put("erasure-resistance", round(willpower * 0.75 + races.derivedModifier(race, "resistance")));
        derived.put("movement-speed", round4(1.0 + races.derivedModifier(race, "movement-speed")));
        derived.put("stability", round(clamp(100 - erasurePressure + willpower * 0.5, 0, 100)));
        return derived;
    }

    public void applyStatsAndDerived(YamlConfiguration character) {
        int level = character.getInt("progression.level", 1);
        double erasure = character.getDouble("erasure", character.getDouble("fragments.erasure-pressure", 0.0));

        Map<String, Double> stats = defaultStatsForLevel(character, level);
        Map<String, Double> derived = derivedStats(character, stats, erasure);
        for (Map.Entry<String, Double> entry : derived.entrySet()) {
            character.set("derived." + entry.getKey(), entry.getValue());
        }

        double maxHealth = derived.getOrDefault("max-health", 0.0);
        if (!character.contains("derived.current-health")) {
            character.set("derived.current-health", maxHealth);
        } else {
            double current = character.getDouble("derived.current-health", maxHealth);
            character.set("derived.current-health", round(clamp(current, 0.0, maxHealth)));
        }
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