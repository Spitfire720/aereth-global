package live.aereth.fragmentengine.papi;

import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.FragmentService;
import live.aereth.fragmentengine.service.IntentService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

public class AerethPlaceholderExpansion extends PlaceholderExpansion {
    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final FragmentService fragments;
    private final IntentService intents;

    public AerethPlaceholderExpansion(JavaPlugin plugin, CharacterService characters, FragmentService fragments, IntentService intents) {
        this.plugin = plugin;
        this.characters = characters;
        this.fragments = fragments;
        this.intents = intents;
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

        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            return switch (params.toLowerCase()) {
                case "race" -> "unformed";
                case "character_name" -> "Unnamed";
                default -> "0";
            };
        }

        String key = params.toLowerCase();
        FragmentService.FragmentSummary fragmentSummary = fragments.summary(character);
        IntentService.IntentSummary intentSummary = intents.summary(character);

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
            case "hp" -> String.valueOf(character.getDouble("derived.current-health", character.getDouble("derived.max-health", 0.0)));
            case "max_hp" -> String.valueOf(character.getDouble("derived.max-health", 0.0));
            case "attack", "attack_power" -> String.valueOf(character.getDouble("derived.attack-power", 0.0));
            case "defense" -> String.valueOf(character.getDouble("derived.defense", 0.0));
            case "crit_chance" -> String.valueOf(character.getDouble("derived.crit-chance", 0.0));
            case "crit_damage" -> String.valueOf(character.getDouble("derived.crit-damage", 0.0));
            case "magic_power" -> String.valueOf(character.getDouble("derived.magic-power", 0.0));
            case "resistance" -> String.valueOf(character.getDouble("derived.resistance", 0.0));
            case "erasure", "erasure_pressure" -> String.valueOf(fragmentSummary.erasurePressure());
            case "fragment_capacity" -> String.valueOf(fragmentSummary.capacity());
            case "fragment_slots_used", "equipped_fragments" -> String.valueOf(fragmentSummary.equipped().size());
            case "fragment_slots_free" -> String.valueOf(Math.max(0, fragmentSummary.capacity() - fragmentSummary.equipped().size()));
            case "fragment_pressure" -> String.valueOf(fragmentSummary.totalPressure());
            case "fragment_stability", "stability" -> String.valueOf(fragmentSummary.stability());
            case "fragment_equipped" -> join(fragmentSummary.equipped());
            case "fragment_discovered" -> join(fragmentSummary.discovered());
            case "intent_primary" -> intentSummary.primary();
            case "intent_slots", "intent_slots_max" -> String.valueOf(intentSummary.maxSlots());
            case "intent_slots_used" -> String.valueOf(intentSummary.usedSlots());
            case "intent_pressure" -> String.valueOf(intentSummary.pressure());
            case "intent_stability_impact" -> String.valueOf(intentSummary.stabilityImpact());
            case "intent_active" -> join(new java.util.ArrayList<>(intentSummary.slots().values()));
            default -> null;
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
        return String.format("%.2f", percent);
    }

    private String join(java.util.List<String> values) {
        if (values == null || values.isEmpty()) {
            return "none";
        }
        StringJoiner joiner = new StringJoiner(",");
        for (String value : values) {
            joiner.add(value);
        }
        return joiner.toString();
    }
}