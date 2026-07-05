package live.aereth.fragmentengine.service;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AbilityService {
    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final File abilitiesFile;
    private YamlConfiguration definitions;

    public AbilityService(JavaPlugin plugin, CharacterService characters) {
        this.plugin = plugin;
        this.characters = characters;
        this.abilitiesFile = new File(plugin.getDataFolder(), "abilities.yml");

        if (!abilitiesFile.exists()) {
            plugin.saveResource("abilities.yml", false);
        }

        reload();
    }

    public void reload() {
        this.definitions = YamlConfiguration.loadConfiguration(abilitiesFile);
    }

    public List<String> allAbilityIds() {
        ConfigurationSection section = definitions.getConfigurationSection("abilities");
        if (section == null) {
            return List.of();
        }
        return new ArrayList<>(section.getKeys(false));
    }

    public List<AbilityDefinition> allDefinitions() {
        List<AbilityDefinition> result = new ArrayList<>();
        for (String id : allAbilityIds()) {
            result.add(definition(id));
        }
        return result;
    }

    public AbilityDefinition definition(String rawId) {
        String id = normalize(rawId);
        String path = "abilities." + id;

        return new AbilityDefinition(
                id,
                definitions.getString(path + ".display-name", title(id)),
                normalizeDiscipline(definitions.getString(path + ".discipline", "unformed")),
                Math.max(1, definitions.getInt(path + ".unlock-rank", 1)),
                definitions.getString(path + ".cost-type", "none"),
                Math.max(0.0, definitions.getDouble(path + ".cost-amount", 0.0)),
                Math.max(0.0, definitions.getDouble(path + ".cooldown-seconds", 0.0)),
                definitions.getString(path + ".description", "")
        );
    }

    public AbilitySummary summary(YamlConfiguration character) {
        String discipline = normalizeDiscipline(character.getString("discipline.id", "unformed"));
        boolean selected = character.getBoolean("discipline.selected", false);
        int rank = selected ? Math.max(0, character.getInt("discipline.progression.rank", 0)) : 0;

        List<String> unlocked = new ArrayList<>();
        List<String> locked = new ArrayList<>();
        Map<String, Integer> unlockRanks = new LinkedHashMap<>();

        for (AbilityDefinition definition : allDefinitions()) {
            if (!definition.discipline().equals(discipline)) {
                continue;
            }

            unlockRanks.put(definition.id(), definition.unlockRank());

            if (selected && rank >= definition.unlockRank()) {
                unlocked.add(definition.id());
            } else {
                locked.add(definition.id());
            }
        }

        character.set("abilities.unlocked", unlocked);
        character.set("abilities.locked", locked);
        character.set("abilities.available", unlocked);
        character.set("abilities.count", unlocked.size());
        character.set("abilities.by-discipline", discipline);

        return new AbilitySummary(
                discipline,
                rank,
                unlocked,
                locked,
                unlocked,
                unlocked.size(),
                unlockRanks
        );
    }

    public String unlockedSummary(YamlConfiguration character) {
        return readable(summary(character).unlocked());
    }

    public String lockedSummary(YamlConfiguration character) {
        return readable(summary(character).locked());
    }

    public String activeSummary(YamlConfiguration character) {
        return readable(summary(character).available());
    }

    public int unlockedCount(YamlConfiguration character) {
        return summary(character).count();
    }

    public String displayName(String abilityId) {
        return definition(abilityId).display();
    }

    private String readable(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "none";
        }
        return String.join(",", values);
    }

    private String normalize(String raw) {
        return raw == null ? "" : raw.toLowerCase().trim().replace(" ", "_").replace("-", "_");
    }

    private String normalizeDiscipline(String raw) {
        return raw == null ? "unformed" : raw.toLowerCase().trim().replace(" ", "_").replace("-", "_");
    }

    private String title(String id) {
        if (id == null || id.isBlank()) {
            return "Unnamed Ability";
        }

        String clean = id.replace("_", " ");
        String[] words = clean.split(" ");
        StringBuilder builder = new StringBuilder();

        for (String word : words) {
            if (word.isBlank()) {
                continue;
            }

            if (!builder.isEmpty()) {
                builder.append(" ");
            }

            builder.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
        }

        return builder.toString();
    }

    public record AbilityDefinition(
            String id,
            String display,
            String discipline,
            int unlockRank,
            String costType,
            double costAmount,
            double cooldownSeconds,
            String description
    ) {
    }

    public record AbilitySummary(
            String discipline,
            int rank,
            List<String> unlocked,
            List<String> locked,
            List<String> available,
            int count,
            Map<String, Integer> unlockRanks
    ) {
    }
}
