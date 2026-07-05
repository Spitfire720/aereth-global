package live.aereth.fragmentengine.service;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

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

        clearKnownDisciplineBonuses(character);

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
        double fragmentBonus = character.getDouble("stats.fragment-bonus." + stat, 0.0);
        double disciplineBonus = disciplineStatBonus(character, stat);
        double total = Math.max(0.0, base + raceMod + bonus + fragmentBonus + disciplineBonus);

        character.set("stats.base." + stat, round(base));
        character.set("stats.race." + stat, round(raceMod));
        character.set("stats.bonus." + stat, round(bonus));
        character.set("stats.fragment-bonus." + stat, round(fragmentBonus));
        character.set("stats.discipline-bonus." + stat, round(disciplineBonus));
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

        derived.put("max-health", derivedRound(character, "max-health", 100 + vitality * 12 + endurance * 6 + races.derivedModifier(race, "max-health")));
        derived.put("attack-power", derivedRound(character, "attack-power", 10 + strength * 2 + races.derivedModifier(race, "attack-power")));
        derived.put("defense", derivedRound(character, "defense", endurance * 1.5 + races.derivedModifier(race, "defense")));
        derived.put("crit-chance", derivedRound4(character, "crit-chance", 0.05 + dexterity * 0.002 + races.derivedModifier(race, "crit-chance")));
        derived.put("crit-damage", derivedRound4(character, "crit-damage", 1.5 + dexterity * 0.01));
        derived.put("magic-power", derivedRound(character, "magic-power", intelligence * 2 + races.derivedModifier(race, "magic-power")));
        derived.put("resistance", derivedRound(character, "resistance", willpower * 1.25 + races.derivedModifier(race, "resistance")));
        derived.put("evasion", derivedRound4(character, "evasion", 0.02 + dexterity * 0.0015));
        derived.put("erasure-resistance", derivedRound(character, "erasure-resistance", willpower * 0.75 + races.derivedModifier(race, "resistance")));
        derived.put("movement-speed", derivedRound4(character, "movement-speed", 1.0 + races.derivedModifier(race, "movement-speed")));

        double stabilityBase = 100 - erasurePressure + willpower * 0.5;
        double stabilityBonus = disciplineDerivedBonus(character, "stability");
        character.set("derived.discipline-bonus.stability", round(stabilityBonus));
        derived.put("stability", round(clamp(stabilityBase + stabilityBonus, 0, 100)));

        return derived;
    }

    public void applyStatsAndDerived(YamlConfiguration character) {
        int level = character.getInt("progression.level", 1);
        double erasure = character.getDouble("fragments.erasure-pressure", character.getDouble("erasure", 0.0));

        Map<String, Double> stats = defaultStatsForLevel(character, level);
        Map<String, Double> derived = derivedStats(character, stats, erasure);

        for (Map.Entry<String, Double> entry : derived.entrySet()) {
            character.set("derived." + entry.getKey(), entry.getValue());
        }

        character.set("discipline.passives.stat-summary", passiveStatSummary(character));
        character.set("discipline.passives.derived-summary", passiveDerivedSummary(character));

        double maxHealth = derived.getOrDefault("max-health", 0.0);
        if (!character.contains("derived.current-health")) {
            character.set("derived.current-health", maxHealth);
        } else {
            double current = character.getDouble("derived.current-health", maxHealth);
            character.set("derived.current-health", round(clamp(current, 0.0, maxHealth)));
        }
    }

    public Map<String, Double> disciplineStatBonuses(YamlConfiguration character) {
        Map<String, Double> bonuses = new LinkedHashMap<>();
        String id = disciplineId(character);
        int rank = disciplineRank(character);

        if (rank <= 0 || id.equals("unformed") || id.equals("none")) {
            return bonuses;
        }

        switch (id) {
            case "vanguard" -> {
                add(bonuses, "vitality", 1.0 * rank);
                add(bonuses, "endurance", 0.5 * rank);
            }
            case "reaver" -> {
                add(bonuses, "strength", 1.0 * rank);
                add(bonuses, "endurance", 0.25 * rank);
            }
            case "skirmisher" -> {
                add(bonuses, "dexterity", 1.0 * rank);
                add(bonuses, "endurance", 0.25 * rank);
            }
            case "assassin" -> {
                add(bonuses, "dexterity", 1.0 * rank);
                add(bonuses, "strength", 0.25 * rank);
            }
            case "marksman" -> {
                add(bonuses, "dexterity", 0.8 * rank);
                add(bonuses, "strength", 0.4 * rank);
            }
            case "controller" -> {
                add(bonuses, "willpower", 0.8 * rank);
                add(bonuses, "intelligence", 0.4 * rank);
            }
            case "spellweaver" -> add(bonuses, "intelligence", 1.0 * rank);
            case "arcanist" -> {
                add(bonuses, "intelligence", 0.8 * rank);
                add(bonuses, "willpower", 0.4 * rank);
            }
            case "chronomancer" -> {
                add(bonuses, "dexterity", 0.5 * rank);
                add(bonuses, "intelligence", 0.5 * rank);
            }
            case "fateweaver" -> {
                add(bonuses, "willpower", 1.0 * rank);
                add(bonuses, "intelligence", 0.25 * rank);
            }
            case "sentinel" -> {
                add(bonuses, "endurance", 1.0 * rank);
                add(bonuses, "willpower", 0.25 * rank);
            }
            case "warden" -> {
                add(bonuses, "endurance", 0.8 * rank);
                add(bonuses, "vitality", 0.4 * rank);
            }
            case "vitalist" -> {
                add(bonuses, "vitality", 0.8 * rank);
                add(bonuses, "willpower", 0.6 * rank);
            }
            case "necrosage" -> {
                add(bonuses, "intelligence", 0.6 * rank);
                add(bonuses, "willpower", 0.6 * rank);
            }
            case "summoner" -> {
                add(bonuses, "intelligence", 0.7 * rank);
                add(bonuses, "willpower", 0.5 * rank);
            }
            case "binder" -> {
                add(bonuses, "willpower", 0.8 * rank);
                add(bonuses, "endurance", 0.3 * rank);
            }
            case "warforger" -> {
                add(bonuses, "strength", 0.6 * rank);
                add(bonuses, "endurance", 0.6 * rank);
            }
            case "architect" -> {
                add(bonuses, "intelligence", 0.6 * rank);
                add(bonuses, "endurance", 0.4 * rank);
            }
            case "oracle" -> {
                add(bonuses, "willpower", 0.7 * rank);
                add(bonuses, "intelligence", 0.5 * rank);
            }
            case "paradox" -> {
                add(bonuses, "intelligence", 0.5 * rank);
                add(bonuses, "dexterity", 0.5 * rank);
            }
            case "suppressor" -> {
                add(bonuses, "willpower", 0.8 * rank);
                add(bonuses, "endurance", 0.4 * rank);
            }
            case "anomaly" -> {
                add(bonuses, "dexterity", 0.4 * rank);
                add(bonuses, "intelligence", 0.4 * rank);
                add(bonuses, "willpower", 0.4 * rank);
            }
            default -> {
            }
        }

        return bonuses;
    }

    public Map<String, Double> disciplineDerivedBonuses(YamlConfiguration character) {
        Map<String, Double> bonuses = new LinkedHashMap<>();
        String id = disciplineId(character);
        int rank = disciplineRank(character);

        if (rank <= 0 || id.equals("unformed") || id.equals("none")) {
            return bonuses;
        }

        switch (id) {
            case "vanguard" -> {
                add(bonuses, "max-health", 10.0 * rank);
                add(bonuses, "defense", 1.0 * rank);
            }
            case "reaver" -> {
                add(bonuses, "attack-power", 3.0 * rank);
                add(bonuses, "crit-damage", 0.02 * rank);
            }
            case "skirmisher" -> {
                add(bonuses, "evasion", 0.0015 * rank);
                add(bonuses, "movement-speed", 0.004 * rank);
            }
            case "assassin" -> {
                add(bonuses, "crit-chance", 0.003 * rank);
                add(bonuses, "crit-damage", 0.03 * rank);
            }
            case "marksman" -> {
                add(bonuses, "crit-chance", 0.0025 * rank);
                add(bonuses, "attack-power", 1.5 * rank);
            }
            case "controller" -> {
                add(bonuses, "resistance", 1.0 * rank);
                add(bonuses, "stability", 1.0 * rank);
            }
            case "spellweaver" -> add(bonuses, "magic-power", 3.0 * rank);
            case "arcanist" -> {
                add(bonuses, "magic-power", 2.0 * rank);
                add(bonuses, "resistance", 0.5 * rank);
            }
            case "chronomancer" -> {
                add(bonuses, "movement-speed", 0.003 * rank);
                add(bonuses, "evasion", 0.001 * rank);
            }
            case "fateweaver" -> {
                add(bonuses, "stability", 1.5 * rank);
                add(bonuses, "crit-chance", 0.0015 * rank);
            }
            case "sentinel" -> {
                add(bonuses, "defense", 2.0 * rank);
                add(bonuses, "resistance", 0.75 * rank);
            }
            case "warden" -> {
                add(bonuses, "max-health", 8.0 * rank);
                add(bonuses, "defense", 1.25 * rank);
            }
            case "vitalist" -> {
                add(bonuses, "max-health", 12.0 * rank);
                add(bonuses, "erasure-resistance", 1.0 * rank);
            }
            case "necrosage" -> {
                add(bonuses, "magic-power", 1.5 * rank);
                add(bonuses, "erasure-resistance", 1.5 * rank);
            }
            case "summoner" -> {
                add(bonuses, "magic-power", 1.75 * rank);
                add(bonuses, "resistance", 0.5 * rank);
            }
            case "binder" -> {
                add(bonuses, "stability", 1.25 * rank);
                add(bonuses, "resistance", 0.75 * rank);
            }
            case "warforger" -> {
                add(bonuses, "attack-power", 1.5 * rank);
                add(bonuses, "defense", 1.5 * rank);
            }
            case "architect" -> {
                add(bonuses, "defense", 1.0 * rank);
                add(bonuses, "resistance", 1.0 * rank);
            }
            case "oracle" -> {
                add(bonuses, "stability", 1.5 * rank);
                add(bonuses, "erasure-resistance", 1.0 * rank);
            }
            case "paradox" -> {
                add(bonuses, "magic-power", 1.0 * rank);
                add(bonuses, "evasion", 0.0015 * rank);
            }
            case "suppressor" -> {
                add(bonuses, "resistance", 1.25 * rank);
                add(bonuses, "erasure-resistance", 1.25 * rank);
            }
            case "anomaly" -> {
                add(bonuses, "movement-speed", 0.002 * rank);
                add(bonuses, "crit-chance", 0.001 * rank);
                add(bonuses, "magic-power", 0.75 * rank);
            }
            default -> {
            }
        }

        return bonuses;
    }

    public String passiveStatSummary(YamlConfiguration character) {
        return readableMap(disciplineStatBonuses(character));
    }

    public String passiveDerivedSummary(YamlConfiguration character) {
        return readableMap(disciplineDerivedBonuses(character));
    }

    private double disciplineStatBonus(YamlConfiguration character, String stat) {
        return disciplineStatBonuses(character).getOrDefault(stat, 0.0);
    }

    private double disciplineDerivedBonus(YamlConfiguration character, String key) {
        return disciplineDerivedBonuses(character).getOrDefault(key, 0.0);
    }

    private double derivedRound(YamlConfiguration character, String key, double base) {
        double bonus = disciplineDerivedBonus(character, key);
        character.set("derived.discipline-bonus." + key, round(bonus));
        return round(base + bonus);
    }

    private double derivedRound4(YamlConfiguration character, String key, double base) {
        double bonus = disciplineDerivedBonus(character, key);
        character.set("derived.discipline-bonus." + key, round4(bonus));
        return round4(base + bonus);
    }

    private String disciplineId(YamlConfiguration character) {
        return character.getString("discipline.id", "unformed").toLowerCase().trim();
    }

    private int disciplineRank(YamlConfiguration character) {
        if (!character.getBoolean("discipline.selected", false)) {
            return 0;
        }

        return Math.max(0, character.getInt("discipline.progression.rank", 0));
    }

    private void clearKnownDisciplineBonuses(YamlConfiguration character) {
        for (String stat : new String[]{"vitality", "strength", "dexterity", "intelligence", "willpower", "endurance"}) {
            character.set("stats.discipline-bonus." + stat, 0.0);
        }

        for (String key : new String[]{"max-health", "attack-power", "defense", "crit-chance", "crit-damage", "magic-power", "resistance", "evasion", "erasure-resistance", "movement-speed", "stability"}) {
            character.set("derived.discipline-bonus." + key, 0.0);
        }
    }

    private void add(Map<String, Double> map, String key, double value) {
        if (value == 0.0) {
            return;
        }

        map.put(key, roundFlexible(value));
    }

    private String readableMap(Map<String, Double> values) {
        if (values == null || values.isEmpty()) {
            return "none";
        }

        StringJoiner joiner = new StringJoiner(",");
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            joiner.add(entry.getKey() + "=" + roundFlexible(entry.getValue()));
        }

        return joiner.toString();
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double roundFlexible(double value) {
        if (Math.abs(value) < 1.0) {
            return round4(value);
        }

        return round(value);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double round4(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }
}
