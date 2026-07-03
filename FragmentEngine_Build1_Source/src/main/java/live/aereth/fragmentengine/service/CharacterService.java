package live.aereth.fragmentengine.service;

import live.aereth.fragmentengine.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.UUID;

public class CharacterService {
    private final JavaPlugin plugin;
    private final StorageService storage;
    private final ProgressionService progression;
    private final StatsService stats;
    private final RaceService races;

    public CharacterService(JavaPlugin plugin, StorageService storage, ProgressionService progression, StatsService stats, RaceService races) {
        this.plugin = plugin;
        this.storage = storage;
        this.progression = progression;
        this.stats = stats;
        this.races = races;
    }

    public YamlConfiguration getAccount(OfflinePlayer player) {
        return storage.loadAccount(player);
    }

    public int getActiveSlot(OfflinePlayer player) {
        YamlConfiguration account = storage.loadAccount(player);
        return account.getInt("active-slot", account.getInt("active-character-slot", 0));
    }

    public YamlConfiguration getActiveCharacter(OfflinePlayer player) {
        int slot = getActiveSlot(player);
        if (slot <= 0) {
            return null;
        }
        YamlConfiguration character = storage.loadCharacter(player.getUniqueId(), slot);
        if (isEmptyCharacterFile(character)) {
            return null;
        }
        ensureBuild1Fields(character, player, slot);
        return character;
    }

    public YamlConfiguration getCharacter(OfflinePlayer player, int slot) {
        YamlConfiguration character = storage.loadCharacter(player.getUniqueId(), slot);
        if (isEmptyCharacterFile(character)) {
            return null;
        }
        ensureBuild1Fields(character, player, slot);
        return character;
    }

    public YamlConfiguration createCharacter(OfflinePlayer player, int slot, String race, String name) throws IOException {
        UUID uuid = player.getUniqueId();
        YamlConfiguration account = storage.loadAccount(player);

        int maxSlots = plugin.getConfig().getInt("settings.maximum-character-slots", 4);
        if (slot < 1 || slot > maxSlots) {
            throw new IllegalArgumentException("Slot must be between 1 and " + maxSlots);
        }

        boolean unlocked = account.getBoolean("slots." + slot + ".unlocked", slot <= plugin.getConfig().getInt("settings.default-free-character-slots", 2));
        if (!unlocked) {
            throw new IllegalArgumentException("Slot " + slot + " is locked.");
        }

        String cleanRace = normalizeRace(race);
        String profileId = uuid + "-slot" + slot;
        YamlConfiguration character = storage.loadCharacter(uuid, slot);

        if (!character.contains("created-at")) {
            character.set("created-at", TimeUtil.nowIso());
        }

        character.set("schema-version", 2);
        character.set("profile-id", profileId);
        character.set("owner-uuid", uuid.toString());
        character.set("owner-name", player.getName() == null ? "unknown" : player.getName());
        character.set("slot", slot);
        character.set("name", (name == null || name.isBlank()) ? "Unnamed " + displayRace(cleanRace) : name);
        character.set("status", "active");
        character.set("creation-complete", true);
        character.set("last-played-at", TimeUtil.nowIso());
        character.set("race.id", cleanRace);
        character.set("race.display", races.displayName(cleanRace));
        character.set("race.trait", races.traitId(cleanRace));
        character.set("race.lore", races.lore(cleanRace));

        ensureBuild1Fields(character, player, slot);

        account.set("active-slot", slot);
        account.set("active-character-slot", slot);
        account.set("slots." + slot + ".unlocked", true);
        account.set("slots." + slot + ".occupied", true);
        account.set("slots." + slot + ".race-id", cleanRace);
        account.set("slots." + slot + ".race-trait", races.traitId(cleanRace));
        account.set("flags.has-completed-character-creation", true);
        account.set("flags.requires-character-selection", false);

        storage.saveCharacter(uuid, slot, character);
        storage.saveAccount(uuid, account);

        return character;
    }

    public void ensureBuild1Fields(YamlConfiguration character, OfflinePlayer player, int slot) {
        int level = character.getInt("progression.level", 1);
        long xp = character.getLong("progression.xp", 0L);
        long totalXp = character.getLong("progression.total-xp", progression.cumulativeXpToReachLevel(level) + xp);
        String raceId = normalizeRace(getRace(character));

        character.set("schema-version", Math.max(character.getInt("schema-version", 1), 2));
        character.set("profile-id", character.getString("profile-id", player.getUniqueId() + "-slot" + slot));
        character.set("owner-uuid", character.getString("owner-uuid", player.getUniqueId().toString()));
        character.set("owner-name", character.getString("owner-name", player.getName() == null ? "unknown" : player.getName()));
        character.set("slot", character.getInt("slot", slot));
        character.set("name", character.getString("name", "Unnamed " + displayRace(raceId)));
        character.set("status", character.getString("status", "active"));
        character.set("last-played-at", TimeUtil.nowIso());

        character.set("race.id", raceId);
        character.set("race.display", races.displayName(raceId));
        character.set("race.trait", races.traitId(raceId));
        character.set("race.lore", races.lore(raceId));

        character.set("progression.level", level);
        character.set("progression.xp", xp);
        character.set("progression.total-xp", totalXp);
        character.set("progression.phase", progression.phaseForLevel(level));
        character.set("progression.unspent-stat-points", character.getInt("progression.unspent-stat-points", 0));
        character.set("progression.unspent-intent-points", character.getInt("progression.unspent-intent-points", 0));

        if (!character.contains("fragment-level")) {
            character.set("fragment-level", 1.0);
        }
        if (!character.contains("fragment-stability")) {
            character.set("fragment-stability", 100.0 + races.startingStabilityModifier(raceId));
        }
        if (!character.contains("existence-strain")) {
            character.set("existence-strain", 0.0);
        }
        if (!character.contains("erasure")) {
            character.set("erasure", Math.max(0.0, races.startingErasureModifier(raceId)));
        }
        if (!character.contains("remnant-state")) {
            character.set("remnant-state", "UNCOMMITTED");
        }
        if (!character.contains("profession")) {
            character.set("profession", "UNFORMED");
        }

        character.set("fragments.capacity", character.getInt("fragments.capacity", plugin.getConfig().getInt("fragments.starting-capacity", 3)));
        if (!character.contains("fragments.discovered")) {
            character.createSection("fragments.discovered");
        }
        if (!character.contains("fragments.equipped")) {
            character.createSection("fragments.equipped");
        }
        character.set("fragments.corruption", character.getDouble("fragments.corruption", 0.0));
        character.set("fragments.erasure-pressure", character.getDouble("fragments.erasure-pressure", character.getDouble("erasure", 0.0)));

        character.set("intent.unlocked-slots", character.getInt("intent.unlocked-slots", plugin.getConfig().getInt("intent.starting-slots", 1)));
        if (!character.contains("intent.active")) {
            character.createSection("intent.active");
        }
        character.set("intent.pressure", character.getDouble("intent.pressure", 0.0));

        stats.applyStatsAndDerived(character);
    }

    public ProgressionService.LevelResult addXp(OfflinePlayer player, long amount) throws IOException {
        int slot = getActiveSlot(player);
        if (slot <= 0) {
            throw new IllegalStateException("No active character.");
        }

        YamlConfiguration character = storage.loadCharacter(player.getUniqueId(), slot);
        ensureBuild1Fields(character, player, slot);

        ProgressionService.LevelResult result = progression.addXp(
                character.getInt("progression.level", 1),
                character.getLong("progression.xp", 0L),
                character.getLong("progression.total-xp", 0L),
                amount
        );

        character.set("progression.level", result.level());
        character.set("progression.xp", result.xp());
        character.set("progression.total-xp", result.totalXp());
        character.set("progression.phase", result.phase());
        if (result.leveledUp()) {
            int statGain = result.levelsGained() * progression.statPointsPerLevel();
            int intentGain = result.levelsGained() * progression.intentPointsPerLevel();
            character.set("progression.unspent-stat-points", character.getInt("progression.unspent-stat-points", 0) + statGain);
            character.set("progression.unspent-intent-points", character.getInt("progression.unspent-intent-points", 0) + intentGain);
        }
        stats.applyStatsAndDerived(character);
        storage.saveCharacter(player.getUniqueId(), slot, character);

        return result;
    }

    public void setLevel(OfflinePlayer player, int level) throws IOException {
        int slot = getActiveSlot(player);
        if (slot <= 0) {
            throw new IllegalStateException("No active character.");
        }
        int clean = Math.max(1, Math.min(level, progression.getLevelCap()));
        YamlConfiguration character = storage.loadCharacter(player.getUniqueId(), slot);
        ensureBuild1Fields(character, player, slot);
        character.set("progression.level", clean);
        character.set("progression.xp", 0L);
        character.set("progression.total-xp", progression.cumulativeXpToReachLevel(clean));
        character.set("progression.phase", progression.phaseForLevel(clean));
        stats.applyStatsAndDerived(character);
        storage.saveCharacter(player.getUniqueId(), slot, character);
    }

    public void setRace(OfflinePlayer player, String race) throws IOException {
        int slot = getActiveSlot(player);
        if (slot <= 0) {
            throw new IllegalStateException("No active character.");
        }
        String cleanRace = normalizeRace(race);
        YamlConfiguration character = storage.loadCharacter(player.getUniqueId(), slot);
        ensureBuild1Fields(character, player, slot);
        character.set("race.id", cleanRace);
        character.set("race.display", races.displayName(cleanRace));
        character.set("race.trait", races.traitId(cleanRace));
        character.set("race.lore", races.lore(cleanRace));
        stats.applyStatsAndDerived(character);
        storage.saveCharacter(player.getUniqueId(), slot, character);

        YamlConfiguration account = storage.loadAccount(player);
        account.set("slots." + slot + ".race-id", cleanRace);
        account.set("slots." + slot + ".race-trait", races.traitId(cleanRace));
        storage.saveAccount(player.getUniqueId(), account);
    }

    public String getRace(YamlConfiguration character) {
        return character.getString("race.id", character.getString("race-id", "remnant"));
    }

    public String normalizeRace(String race) {
        return races.normalizeRace(race);
    }

    public String displayRace(String race) {
        return races.displayName(race);
    }

    public String defaultTraitForRace(String race) {
        return races.traitId(race);
    }

    private boolean isEmptyCharacterFile(YamlConfiguration character) {
        return character == null || character.getKeys(false).isEmpty();
    }

    public StorageService storage() {
        return storage;
    }

    public ProgressionService progression() {
        return progression;
    }

    public StatsService stats() {
        return stats;
    }

    public RaceService races() {
        return races;
    }
}