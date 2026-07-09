package live.aereth.fragmentengine.papi;

import live.aereth.fragmentengine.service.AbilityService;
import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.DisciplineService;
import live.aereth.fragmentengine.service.FragmentService;
import live.aereth.fragmentengine.service.IntentService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public class AerethPlaceholderExpansion extends PlaceholderExpansion {
    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final FragmentService fragments;
    private final IntentService intents;
    private final DisciplineService disciplines;
    private final AbilityService abilities;

    public AerethPlaceholderExpansion(JavaPlugin plugin, CharacterService characters, FragmentService fragments, IntentService intents, DisciplineService disciplines, AbilityService abilities) {
        this.plugin = plugin;
        this.characters = characters;
        this.fragments = fragments;
        this.intents = intents;
        this.disciplines = disciplines;
        this.abilities = abilities;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "aereth";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SpitFire720";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "-";
        }

        String key = params.toLowerCase(Locale.ROOT).trim();
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            return noCharacterValue(key);
        }

        FragmentService.FragmentSummary fragmentSummary = fragments.summary(character);
        IntentService.IntentSummary intentSummary = intents.summary(character);
        DisciplineService.DisciplineSummary disciplineSummary = disciplines.summary(character);
        DisciplineService.DisciplineProgressSummary disciplineProgress = disciplines.progress(character);
        AbilityService.AbilitySummary abilitySummary = abilities.summary(character);

        double combinedPressure = combinedPressure(fragmentSummary, intentSummary);
        double combinedStability = combinedStability(fragmentSummary, intentSummary);
        String identityState = identityState(fragmentSummary, intentSummary);

        return switch (key) {
            case "character_name" -> character.getString("name", "Unnamed");
            case "character_slot" -> String.valueOf(character.getInt("slot", 0));
            case "race" -> character.getString("race.id", "unformed");
            case "race_display" -> character.getString("race.display", character.getString("race.id", "unformed"));
            case "level" -> String.valueOf(character.getInt("progression.level", 1));
            case "phase" -> character.getString("progression.phase", "discovery");
            case "xp" -> String.valueOf(character.getLong("progression.xp", 0L));
            case "total_xp" -> String.valueOf(character.getLong("progression.total-xp", 0L));
            case "xp_required" -> String.valueOf(characters.progression().xpRequiredForLevel(character.getInt("progression.level", 1)));
            case "xp_progress_percent" -> xpPercent(character);
            case "hp" -> round(character.getDouble("derived.current-health", character.getDouble("derived.max-health", 0.0)));
            case "max_hp" -> round(character.getDouble("derived.max-health", 0.0));
            case "attack", "attack_power" -> round(character.getDouble("derived.attack-power", 0.0));
            case "defense" -> round(character.getDouble("derived.defense", 0.0));
            case "crit_chance" -> round(character.getDouble("derived.crit-chance", 0.0));
            case "crit_damage" -> round(character.getDouble("derived.crit-damage", 0.0));
            case "magic_power" -> round(character.getDouble("derived.magic-power", 0.0));
            case "resistance" -> round(character.getDouble("derived.resistance", 0.0));

            case "identity_state" -> identityState;
            case "identity_state_display" -> displayState(identityState);
            case "identity_total_pressure" -> round(combinedPressure);
            case "identity_combined_stability" -> round(combinedStability);
            case "identity_erasure_pressure" -> round(fragmentSummary.erasurePressure());
            case "identity_pressure_label" -> pressureLabel(combinedPressure);
            case "identity_stability_label" -> stabilityLabel(combinedStability);
            case "identity_summary" -> identitySummary(identityState, combinedPressure, combinedStability, fragmentSummary.erasurePressure());
            case "identity_remnant_state" -> character.getString("remnant-state", "UNCOMMITTED");
            case "identity_profession" -> character.getString("profession", "UNFORMED");
            case "identity_fragment_level" -> round(character.getDouble("fragment-level", 1.0));
            case "identity_existence_strain" -> round(character.getDouble("existence-strain", 0.0));

            case "erasure", "erasure_pressure" -> round(fragmentSummary.erasurePressure());
            case "fragment_capacity" -> String.valueOf(fragmentSummary.capacity());
            case "fragment_slots_used", "equipped_fragments" -> String.valueOf(fragmentSummary.equipped().size());
            case "fragment_slots_free" -> String.valueOf(Math.max(0, fragmentSummary.capacity() - fragmentSummary.equipped().size()));
            case "fragment_pressure" -> round(fragmentSummary.totalPressure());
            case "fragment_pressure_label" -> pressureLabel(fragmentSummary.totalPressure());
            case "fragment_stability", "stability" -> round(fragmentSummary.stability());
            case "fragment_stability_label" -> stabilityLabel(fragmentSummary.stability());
            case "fragment_equipped" -> join(fragmentSummary.equipped());
            case "fragment_equipped_display" -> joinFragmentDisplay(fragmentSummary.equipped());
            case "fragment_discovered" -> join(fragmentSummary.discovered());
            case "fragment_discovered_display" -> joinFragmentDisplay(fragmentSummary.discovered());
            case "fragment_discovered_count" -> String.valueOf(fragmentSummary.discovered().size());

            case "intent_primary" -> intentSummary.primary();
            case "intent_primary_display" -> intents.displayName(intentSummary.primary());
            case "intent_slots", "intent_slots_max" -> String.valueOf(intentSummary.maxSlots());
            case "intent_slots_used" -> String.valueOf(intentSummary.usedSlots());
            case "intent_slots_free" -> String.valueOf(Math.max(0, intentSummary.maxSlots() - intentSummary.usedSlots()));
            case "intent_pressure" -> round(intentSummary.pressure());
            case "intent_pressure_label" -> pressureLabel(intentSummary.pressure());
            case "intent_stability_impact" -> round(intentSummary.stabilityImpact());
            case "intent_stability_label" -> stabilityImpactLabel(intentSummary.stabilityImpact());
            case "intent_active" -> join(new ArrayList<>(intentSummary.slots().values()));
            case "intent_active_display" -> joinIntentDisplay(new ArrayList<>(intentSummary.slots().values()));
            case "intent_slot1" -> slotValue(intentSummary, "slot1");
            case "intent_slot2" -> slotValue(intentSummary, "slot2");
            case "intent_slot3" -> slotValue(intentSummary, "slot3");
            case "intent_slot4" -> slotValue(intentSummary, "slot4");
            case "intent_slot1_display" -> slotDisplay(intentSummary, "slot1");
            case "intent_slot2_display" -> slotDisplay(intentSummary, "slot2");
            case "intent_slot3_display" -> slotDisplay(intentSummary, "slot3");
            case "intent_slot4_display" -> slotDisplay(intentSummary, "slot4");

            case "discipline" -> disciplineSummary.id();
            case "discipline_display" -> disciplineSummary.display();
            case "discipline_family" -> disciplineSummary.family();
            case "discipline_unlocked" -> String.valueOf(disciplineSummary.unlocked());
            case "discipline_level_required" -> String.valueOf(disciplineSummary.unlockLevel());
            case "discipline_rank" -> String.valueOf(disciplineProgress.rank());
            case "discipline_rank_name" -> disciplineProgress.rankName();
            case "discipline_xp" -> String.valueOf(disciplineProgress.xp());
            case "discipline_xp_required" -> String.valueOf(disciplineProgress.xpRequired());
            case "discipline_progress_percent" -> round(disciplineProgress.progressPercent());
            case "discipline_passive_stats" -> characters.stats().passiveStatSummary(character);
            case "discipline_passive_derived" -> characters.stats().passiveDerivedSummary(character);
            case "discipline_bonus_vitality" -> round(character.getDouble("stats.discipline-bonus.vitality", 0.0));
            case "discipline_bonus_strength" -> round(character.getDouble("stats.discipline-bonus.strength", 0.0));
            case "discipline_bonus_dexterity" -> round(character.getDouble("stats.discipline-bonus.dexterity", 0.0));
            case "discipline_bonus_intelligence" -> round(character.getDouble("stats.discipline-bonus.intelligence", 0.0));
            case "discipline_bonus_willpower" -> round(character.getDouble("stats.discipline-bonus.willpower", 0.0));
            case "discipline_bonus_endurance" -> round(character.getDouble("stats.discipline-bonus.endurance", 0.0));
            case "abilities_unlocked" -> join(abilitySummary.unlocked());
            case "abilities_locked" -> join(abilitySummary.locked());
            case "abilities_active" -> join(abilitySummary.available());
            case "ability_count" -> String.valueOf(abilitySummary.count());
            default -> null;
        };
    }

    private String noCharacterValue(String key) {
        return switch (key) {
            case "character_name" -> "Unnamed";
            case "race", "race_display", "identity_state", "identity_state_display" -> "unformed";
            case "identity_summary" -> "no_character";
            case "fragment_equipped", "fragment_equipped_display", "fragment_discovered", "fragment_discovered_display", "intent_active", "intent_active_display" -> "none";
            default -> "0";
        };
    }

    private String xpPercent(YamlConfiguration character) {
        int level = character.getInt("progression.level", 1);
        long xp = character.getLong("progression.xp", 0L);
        long required = characters.progression().xpRequiredForLevel(level);
        if (required <= 0) {
            return "100";
        }
        double percent = (xp * 100.0) / required;
        return round(percent);
    }

    private double combinedPressure(FragmentService.FragmentSummary fragmentSummary, IntentService.IntentSummary intentSummary) {
        return fragmentSummary.totalPressure() + intentSummary.pressure();
    }

    private double combinedStability(FragmentService.FragmentSummary fragmentSummary, IntentService.IntentSummary intentSummary) {
        return clamp(fragmentSummary.stability() + intentSummary.stabilityImpact(), 0.0, 100.0);
    }

    private String identityState(FragmentService.FragmentSummary fragmentSummary, IntentService.IntentSummary intentSummary) {
        double pressure = combinedPressure(fragmentSummary, intentSummary);
        double stability = combinedStability(fragmentSummary, intentSummary);
        double erasure = fragmentSummary.erasurePressure();

        if (erasure >= 75.0 || stability <= 20.0) {
            return "critical";
        }
        if (pressure >= 60.0 || erasure >= 50.0 || stability <= 45.0) {
            return "strained";
        }
        if (fragmentSummary.equipped().isEmpty() && intentSummary.usedSlots() == 0) {
            return "unformed";
        }
        return "stable";
    }

    private String displayState(String state) {
        return switch (state) {
            case "critical" -> "Critical";
            case "strained" -> "Strained";
            case "stable" -> "Stable";
            default -> "Unformed";
        };
    }

    private String pressureLabel(double pressure) {
        if (pressure >= 60.0) {
            return "high";
        }
        if (pressure >= 30.0) {
            return "medium";
        }
        return "low";
    }

    private String stabilityLabel(double stability) {
        if (stability <= 20.0) {
            return "critical";
        }
        if (stability <= 45.0) {
            return "strained";
        }
        return "stable";
    }

    private String stabilityImpactLabel(double impact) {
        if (impact <= -20.0) {
            return "severe";
        }
        if (impact < 0.0) {
            return "negative";
        }
        if (impact > 0.0) {
            return "positive";
        }
        return "neutral";
    }

    private String identitySummary(String state, double pressure, double stability, double erasure) {
        return "state=" + state
                + ";pressure=" + round(pressure)
                + ";stability=" + round(stability)
                + ";erasure=" + round(erasure);
    }

    private String slotValue(IntentService.IntentSummary summary, String slot) {
        String value = summary.slots().get(slot);
        return value == null || value.isBlank() ? "none" : value;
    }

    private String slotDisplay(IntentService.IntentSummary summary, String slot) {
        String value = slotValue(summary, slot);
        return value.equals("none") ? "None" : intents.displayName(value);
    }

    private String joinFragmentDisplay(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "none";
        }
        StringJoiner joiner = new StringJoiner(",");
        for (String value : values) {
            joiner.add(fragments.displayName(value));
        }
        return joiner.toString();
    }

    private String joinIntentDisplay(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "none";
        }
        StringJoiner joiner = new StringJoiner(",");
        for (String value : values) {
            joiner.add(intents.displayName(value));
        }
        return joiner.toString();
    }

    private String join(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "none";
        }
        StringJoiner joiner = new StringJoiner(",");
        for (String value : values) {
            joiner.add(value);
        }
        return joiner.toString();
    }

    private String round(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
