package live.aereth.fragmentengine.service;

import live.aereth.fragmentengine.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DisciplineService {
    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final YamlConfiguration definitions;

    public DisciplineService(JavaPlugin plugin, CharacterService characters) {
        this.plugin = plugin;
        this.characters = characters;
        this.definitions = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "disciplines.yml"));
    }

    public List<String> allDisciplineIds() {
        ConfigurationSection section = definitions.getConfigurationSection("disciplines");
        if (section == null) {
            return List.of("vanguard", "reaver", "skirmisher", "assassin", "marksman", "controller",
                    "spellweaver", "arcanist", "chronomancer", "fateweaver",
                    "sentinel", "warden", "vitalist", "necrosage",
                    "summoner", "binder", "warforger", "architect",
                    "oracle", "paradox", "suppressor", "anomaly");
        }
        return new ArrayList<>(section.getKeys(false));
    }

    public boolean isKnownDiscipline(String raw) {
        String id = normalizeDiscipline(raw);
        return allDisciplineIds().contains(id);
    }

    public String displayName(String raw) {
        String id = normalizeDiscipline(raw);
        if (id.isBlank() || id.equals("unformed") || id.equals("none")) {
            return "Unformed";
        }
        return definitions.getString("disciplines." + id + ".display", title(id));
    }

    public DisciplineDefinition definition(String raw) {
        String id = normalizeDiscipline(raw);
        if (id.isBlank() || id.equals("unformed") || id.equals("none")) {
            return new DisciplineDefinition(
                    "unformed",
                    "Unformed",
                    "none",
                    defaultUnlockLevel(),
                    "No committed Discipline selected."
            );
        }

        return new DisciplineDefinition(
                id,
                displayName(id),
                definitions.getString("disciplines." + id + ".family", "unknown"),
                definitions.getInt("disciplines." + id + ".unlock-level", defaultUnlockLevel()),
                definitions.getString("disciplines." + id + ".description", "")
        );
    }

    public DisciplineSummary summary(YamlConfiguration character) {
        int level = character.getInt("progression.level", 1);
        String id = normalizeDiscipline(character.getString("discipline.id", character.getString("profession", "unformed")));

        if (id.isBlank() || id.equals("none") || id.equals("unformed")) {
            int required = defaultUnlockLevel();
            return new DisciplineSummary("unformed", "Unformed", "none", required, level >= required, false);
        }

        DisciplineDefinition definition = definition(id);
        return new DisciplineSummary(
                definition.id(),
                definition.display(),
                definition.family(),
                definition.unlockLevel(),
                level >= definition.unlockLevel(),
                true
        );
    }

    public DisciplineResult setDiscipline(OfflinePlayer player, String rawDiscipline) throws IOException {
        YamlConfiguration character = activeCharacter(player);
        String id = normalizeDiscipline(rawDiscipline);

        if (!isKnownDiscipline(id)) {
            throw new IllegalArgumentException("Unknown discipline: " + rawDiscipline);
        }

        DisciplineDefinition definition = definition(id);
        int level = character.getInt("progression.level", 1);

        if (level < definition.unlockLevel()) {
            throw new IllegalArgumentException("Discipline locked. Required level: " + definition.unlockLevel() + ", current level: " + level);
        }

        character.set("discipline.id", definition.id());
        character.set("discipline.display", definition.display());
        character.set("discipline.family", definition.family());
        character.set("discipline.level-required", definition.unlockLevel());
        character.set("discipline.selected", true);
        character.set("discipline.selected-at", TimeUtil.nowIso());
        character.set("profession", definition.id().toUpperCase());
        character.set("remnant-state", "COMMITTED");

        saveCharacter(player, character);

        return new DisciplineResult(definition.id(), "set", summary(character));
    }

    public DisciplineResult clearDiscipline(OfflinePlayer player) throws IOException {
        YamlConfiguration character = activeCharacter(player);

        character.set("discipline.id", "unformed");
        character.set("discipline.display", "Unformed");
        character.set("discipline.family", "none");
        character.set("discipline.level-required", defaultUnlockLevel());
        character.set("discipline.selected", false);
        character.set("discipline.selected-at", null);
        character.set("profession", "UNFORMED");
        character.set("remnant-state", "UNCOMMITTED");

        saveCharacter(player, character);

        return new DisciplineResult("unformed", "cleared", summary(character));
    }

    private YamlConfiguration activeCharacter(OfflinePlayer player) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            throw new IllegalStateException("No active character.");
        }
        return character;
    }

    private void saveCharacter(OfflinePlayer player, YamlConfiguration character) throws IOException {
        int slot = character.getInt("slot", characters.getActiveSlot(player));
        characters.storage().saveCharacter(player.getUniqueId(), slot, character);
    }

    private int defaultUnlockLevel() {
        return definitions.getInt("settings.unlock-level", 16);
    }

    private String normalizeDiscipline(String raw) {
        return raw == null ? "" : raw.toLowerCase().trim().replace(" ", "_").replace("-", "_");
    }

    private String title(String id) {
        if (id == null || id.isBlank()) {
            return "Unformed";
        }
        String clean = id.replace("_", " ");
        return clean.substring(0, 1).toUpperCase() + clean.substring(1);
    }

    public record DisciplineDefinition(
            String id,
            String display,
            String family,
            int unlockLevel,
            String description
    ) {
    }

    public record DisciplineSummary(
            String id,
            String display,
            String family,
            int unlockLevel,
            boolean unlocked,
            boolean selected
    ) {
    }

    public record DisciplineResult(
            String disciplineId,
            String status,
            DisciplineSummary summary
    ) {
    }
}