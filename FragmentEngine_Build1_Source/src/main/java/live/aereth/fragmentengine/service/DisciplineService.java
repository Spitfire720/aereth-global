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

    public DisciplineProgressSummary progress(YamlConfiguration character) {
        DisciplineSummary discipline = summary(character);
        boolean selected = discipline.selected();

        int rank = character.getInt("discipline.progression.rank", selected ? 1 : 0);
        rank = clamp(rank, 0, maxRank());

        long xp = Math.max(0L, character.getLong("discipline.progression.xp", 0L));
        long required = rank >= maxRank() || rank <= 0 ? 0L : xpRequiredForRank(rank);
        double percent = required <= 0L ? (rank >= maxRank() ? 100.0 : 0.0) : Math.min(100.0, (xp * 100.0) / required);

        return new DisciplineProgressSummary(
                rank,
                rankName(rank),
                xp,
                required,
                roundTwo(percent),
                maxRank(),
                rank >= maxRank()
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

        writeProgressionFields(character, 1, 0L);

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

        writeProgressionFields(character, 0, 0L);

        saveCharacter(player, character);

        return new DisciplineResult("unformed", "cleared", summary(character));
    }

    public DisciplineProgressResult addDisciplineXp(OfflinePlayer player, long amount) throws IOException {
        if (amount <= 0L) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }

        YamlConfiguration character = activeCharacter(player);
        DisciplineSummary discipline = summary(character);

        if (!discipline.selected()) {
            throw new IllegalStateException("No Discipline selected.");
        }

        DisciplineProgressSummary before = progress(character);
        int rank = before.rank() <= 0 ? 1 : before.rank();
        long xp = before.xp() + amount;
        int ranksGained = 0;

        while (rank < maxRank()) {
            long required = xpRequiredForRank(rank);
            if (xp < required) {
                break;
            }
            xp -= required;
            rank++;
            ranksGained++;
        }

        if (rank >= maxRank()) {
            rank = maxRank();
            xp = 0L;
        }

        writeProgressionFields(character, rank, xp);
        saveCharacter(player, character);

        return new DisciplineProgressResult(amount, ranksGained, progress(character));
    }

    public DisciplineProgressResult setDisciplineRank(OfflinePlayer player, int rank) throws IOException {
        if (rank < 0 || rank > maxRank()) {
            throw new IllegalArgumentException("Rank must be between 0 and " + maxRank() + ".");
        }

        YamlConfiguration character = activeCharacter(player);
        DisciplineSummary discipline = summary(character);

        if (!discipline.selected() && rank > 0) {
            throw new IllegalStateException("No Discipline selected.");
        }

        writeProgressionFields(character, rank, 0L);
        saveCharacter(player, character);

        return new DisciplineProgressResult(0L, 0, progress(character));
    }

    public DisciplineProgressResult resetDisciplineProgress(OfflinePlayer player) throws IOException {
        YamlConfiguration character = activeCharacter(player);
        DisciplineSummary discipline = summary(character);

        int rank = discipline.selected() ? 1 : 0;
        writeProgressionFields(character, rank, 0L);
        saveCharacter(player, character);

        return new DisciplineProgressResult(0L, 0, progress(character));
    }

    public long xpRequiredForRank(int rank) {
        return switch (rank) {
            case 1 -> 1000L;
            case 2 -> 2500L;
            case 3 -> 5000L;
            case 4 -> 9000L;
            default -> rank <= 0 || rank >= maxRank() ? 0L : (long) Math.floor(1000.0 * Math.pow(rank, 1.5));
        };
    }

    public int maxRank() {
        return definitions.getInt("settings.max-rank", 5);
    }

    public String rankName(int rank) {
        return switch (rank) {
            case 1 -> "Initiate";
            case 2 -> "Adept";
            case 3 -> "Specialist";
            case 4 -> "Master";
            case 5 -> "Ascendant";
            default -> "Untrained";
        };
    }

    private void writeProgressionFields(YamlConfiguration character, int rank, long xp) {
        rank = clamp(rank, 0, maxRank());
        xp = Math.max(0L, xp);

        long required = rank <= 0 || rank >= maxRank() ? 0L : xpRequiredForRank(rank);
        double percent = required <= 0L ? (rank >= maxRank() ? 100.0 : 0.0) : Math.min(100.0, (xp * 100.0) / required);

        character.set("discipline.progression.rank", rank);
        character.set("discipline.progression.rank-name", rankName(rank));
        character.set("discipline.progression.xp", xp);
        character.set("discipline.progression.xp-required", required);
        character.set("discipline.progression.progress-percent", roundTwo(percent));
        character.set("discipline.progression.max-rank", maxRank());
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
        characters.recalculate(character);
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

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private double roundTwo(double value) {
        return Math.round(value * 100.0) / 100.0;
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

    public record DisciplineProgressSummary(
            int rank,
            String rankName,
            long xp,
            long xpRequired,
            double progressPercent,
            int maxRank,
            boolean atCap
    ) {
    }

    public record DisciplineResult(
            String disciplineId,
            String status,
            DisciplineSummary summary
    ) {
    }

    public record DisciplineProgressResult(
            long amountAdded,
            int ranksGained,
            DisciplineProgressSummary progress
    ) {
    }
}