package live.aereth.fragmentengine.papi;

import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.ProgressionService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class AerethPlaceholderExpansion extends PlaceholderExpansion {
    private final JavaPlugin plugin;
    private final CharacterService characters;

    public AerethPlaceholderExpansion(JavaPlugin plugin, CharacterService characters) {
        this.plugin = plugin;
        this.characters = characters;
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

        return switch (key) {
            case "character_name" -> character.getString("name", "Unnamed");
            case "character_slot" -> String.valueOf(character.getInt("slot", 0));
            case "race" -> character.getString("race.id", "unformed");
            case "level" -> String.valueOf(character.getInt("progression.level", 1));
            case "phase" -> character.getString("progression.phase", "discovery");
            case "xp" -> String.valueOf(character.getLong("progression.xp", 0L));
            case "xp_required" -> String.valueOf(characters.progression().xpRequiredForLevel(character.getInt("progression.level", 1)));
            case "xp_progress_percent" -> xpPercent(character);
            case "hp" -> String.valueOf(character.getDouble("derived.current-health", character.getDouble("derived.max-health", 0.0)));
            case "max_hp" -> String.valueOf(character.getDouble("derived.max-health", 0.0));
            case "attack_power" -> String.valueOf(character.getDouble("derived.attack-power", 0.0));
            case "defense" -> String.valueOf(character.getDouble("derived.defense", 0.0));
            case "crit_chance" -> String.valueOf(character.getDouble("derived.crit-chance", 0.0));
            case "crit_damage" -> String.valueOf(character.getDouble("derived.crit-damage", 0.0));
            case "magic_power" -> String.valueOf(character.getDouble("derived.magic-power", 0.0));
            case "resistance" -> String.valueOf(character.getDouble("derived.resistance", 0.0));
            case "erasure_pressure" -> String.valueOf(character.getDouble("fragments.erasure-pressure", character.getDouble("erasure", 0.0)));
            case "fragment_capacity" -> String.valueOf(character.getInt("fragments.capacity", 3));
            case "equipped_fragments" -> String.valueOf(character.getStringList("fragments.equipped").size());
            case "intent_slots" -> String.valueOf(character.getInt("intent.unlocked-slots", 1));
            case "intent_pressure" -> String.valueOf(character.getDouble("intent.pressure", 0.0));
            case "stability" -> String.valueOf(character.getDouble("derived.stability", character.getDouble("fragment-stability", 100.0)));
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
}
