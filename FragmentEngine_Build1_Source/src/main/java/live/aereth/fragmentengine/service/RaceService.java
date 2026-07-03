package live.aereth.fragmentengine.service;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Locale;

public class RaceService {
    private final JavaPlugin plugin;
    private YamlConfiguration races;

    public RaceService(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "races.yml");
        if (!file.exists()) {
            plugin.saveResource("races.yml", false);
        }
        races = YamlConfiguration.loadConfiguration(file);
    }

    public String normalizeRace(String race) {
        if (race == null || race.isBlank()) {
            return "remnant";
        }
        return race.toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    public boolean exists(String race) {
        return races.contains("races." + normalizeRace(race));
    }

    public boolean selectable(String race) {
        return races.getBoolean("races." + normalizeRace(race) + ".selectable", true);
    }

    public String displayName(String race) {
        String clean = normalizeRace(race);
        return races.getString("races." + clean + ".display", fallbackDisplay(clean));
    }

    public String traitId(String race) {
        String clean = normalizeRace(race);
        return races.getString("races." + clean + ".trait", "Memory Fractured");
    }

    public String lore(String race) {
        String clean = normalizeRace(race);
        return races.getString("races." + clean + ".lore", "No stable memory has formed yet.");
    }

    public double statModifier(String race, String stat) {
        return races.getDouble("races." + normalizeRace(race) + ".modifiers.stats." + stat, 0.0);
    }

    public double derivedModifier(String race, String stat) {
        return races.getDouble("races." + normalizeRace(race) + ".modifiers.derived." + stat, 0.0);
    }

    public double startingErasureModifier(String race) {
        return races.getDouble("races." + normalizeRace(race) + ".modifiers.starting.erasure", 0.0);
    }

    public double startingStabilityModifier(String race) {
        return races.getDouble("races." + normalizeRace(race) + ".modifiers.starting.stability", 0.0);
    }

    private String fallbackDisplay(String race) {
        if (race == null || race.isBlank()) {
            return "Remnant";
        }
        return race.substring(0, 1).toUpperCase(Locale.ROOT) + race.substring(1).replace("_", " ");
    }
}