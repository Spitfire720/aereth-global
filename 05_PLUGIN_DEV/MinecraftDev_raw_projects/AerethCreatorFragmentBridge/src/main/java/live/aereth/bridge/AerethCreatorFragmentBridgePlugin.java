package live.aereth.bridge;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

public final class AerethCreatorFragmentBridgePlugin extends JavaPlugin implements CommandExecutor {
    private File pluginsFolder;
    private File creatorProfilesFile;
    private File fragmentAccountsFolder;
    private File fragmentCharactersFolder;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        refreshPaths();

        if (getCommand("aerethbridge") != null) {
            getCommand("aerethbridge").setExecutor(this);
        }

        boolean autoSync = getConfig().getBoolean("auto-sync.enabled", true);
        if (autoSync) {
            long intervalTicks = Math.max(20L, getConfig().getLong("auto-sync.interval-seconds", 10L) * 20L);
            Bukkit.getScheduler().runTaskTimer(this, this::syncOnlinePlayersSafely, 60L, intervalTicks);
        }

        getLogger().info("AerethCreatorFragmentBridge enabled. CharacterCreator -> FragmentEngine mirror armed.");
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        refreshPaths();
    }

    private void refreshPaths() {
        pluginsFolder = getDataFolder().getParentFile();

        String creatorFolder = getConfig().getString("paths.character-creator-folder", "AerethCharacterCreatorCore");
        String fragmentFolder = getConfig().getString("paths.fragment-engine-folder", "FragmentEngine");
        String creatorProfile = getConfig().getString("paths.character-creator-profile-file", "player_profiles.yml");
        String accounts = getConfig().getString("paths.fragment-accounts-folder", "accounts");
        String characters = getConfig().getString("paths.fragment-characters-folder", "characters");

        creatorProfilesFile = new File(new File(pluginsFolder, creatorFolder), creatorProfile);
        File fragmentDataFolder = new File(pluginsFolder, fragmentFolder);
        fragmentAccountsFolder = new File(fragmentDataFolder, accounts);
        fragmentCharactersFolder = new File(fragmentDataFolder, characters);

        fragmentAccountsFolder.mkdirs();
        fragmentCharactersFolder.mkdirs();
    }

    private void syncOnlinePlayersSafely() {
        try {
            for (Player player : Bukkit.getOnlinePlayers()) {
                syncPlayer(player.getUniqueId(), player.getName(), false);
            }
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "Auto-sync failed: " + ex.getMessage(), ex);
        }
    }

    private int syncAllProfilesSafely() {
        try {
            return syncAllProfiles();
        } catch (Exception ex) {
            getLogger().log(Level.WARNING, "Sync-all failed: " + ex.getMessage(), ex);
            return 0;
        }
    }

    private int syncAllProfiles() throws IOException {
        if (!creatorProfilesFile.exists()) {
            return 0;
        }

        YamlConfiguration profiles = YamlConfiguration.loadConfiguration(creatorProfilesFile);
        ConfigurationSection players = profiles.getConfigurationSection("players");
        if (players == null) {
            return 0;
        }

        int synced = 0;
        for (String rawUuid : players.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(rawUuid);
                OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
                String name = offline.getName() == null ? "unknown" : offline.getName();
                if (syncPlayer(uuid, name, true)) {
                    synced++;
                }
            } catch (IllegalArgumentException ignored) {
                getLogger().warning("Skipping invalid UUID in CharacterCreator profile file: " + rawUuid);
            }
        }
        return synced;
    }

    private boolean syncPlayer(UUID uuid, String playerName, boolean forceExisting) throws IOException {
        if (!creatorProfilesFile.exists()) {
            return false;
        }

        YamlConfiguration profiles = YamlConfiguration.loadConfiguration(creatorProfilesFile);
        String base = "players." + uuid;
        if (!profiles.contains(base)) {
            return false;
        }

        int maxSlots = getConfig().getInt("sync.maximum-slots", 4);
        int freeSlots = getConfig().getInt("sync.default-free-slots", 2);
        int activeSlot = profiles.getInt(base + ".active_character", 0);
        if (activeSlot <= 0) {
            activeSlot = firstExistingSlot(profiles, base, maxSlots);
        }
        if (activeSlot <= 0) {
            return false;
        }

        YamlConfiguration account = loadAccount(uuid);
        initializeAccount(account, uuid, playerName, maxSlots, freeSlots);
        account.set("active-slot", activeSlot);
        account.set("active-character-slot", activeSlot);
        account.set("flags.has-completed-character-creation", true);
        account.set("flags.requires-character-selection", false);
        account.set("last-seen-at", nowIso());

        boolean changed = false;
        boolean overwriteExisting = getConfig().getBoolean("sync.overwrite-existing-fragment-characters", false);

        for (int slot = 1; slot <= maxSlots; slot++) {
            String slotBase = base + ".slots." + slot;
            boolean exists = profiles.getBoolean(slotBase + ".exists", false);

            account.set("slots." + slot + ".unlocked", slot <= freeSlots || exists);

            if (!exists) {
                if (!account.contains("slots." + slot + ".occupied")) {
                    account.set("slots." + slot + ".occupied", false);
                }
                continue;
            }

            String race = normalizeRace(profiles.getString(slotBase + ".race", "remnant"));
            String raceDisplay = profiles.getString(slotBase + ".race_display", displayRace(race));
            String name = profiles.getString(slotBase + ".name", "Character " + slot);
            String region = profiles.getString(slotBase + ".region", "Unknown");
            int level = Math.max(1, profiles.getInt(slotBase + ".level", 1));
            String createdAt = profiles.getString(slotBase + ".created_at", nowIso());

            account.set("slots." + slot + ".occupied", true);
            account.set("slots." + slot + ".race-id", race);
            account.set("slots." + slot + ".race-trait", defaultTraitForRace(race));

            File characterFile = characterFile(uuid, slot);
            if (characterFile.exists() && !forceExisting && !overwriteExisting && characterFile.length() > 0) {
                YamlConfiguration existing = YamlConfiguration.loadConfiguration(characterFile);
                if (existing.contains("profile-id") && existing.contains("race.id")) {
                    continue;
                }
            }

            YamlConfiguration character = YamlConfiguration.loadConfiguration(characterFile);
            writeCharacter(character, uuid, playerName, slot, race, raceDisplay, name, region, level, createdAt);
            character.save(characterFile);
            changed = true;
        }

        account.save(accountFile(uuid));
        return changed || true;
    }

    private int firstExistingSlot(YamlConfiguration profiles, String base, int maxSlots) {
        for (int slot = 1; slot <= maxSlots; slot++) {
            if (profiles.getBoolean(base + ".slots." + slot + ".exists", false)) {
                return slot;
            }
        }
        return 0;
    }

    private YamlConfiguration loadAccount(UUID uuid) {
        return YamlConfiguration.loadConfiguration(accountFile(uuid));
    }

    private File accountFile(UUID uuid) {
        return new File(fragmentAccountsFolder, uuid + ".yml");
    }

    private File characterFile(UUID uuid, int slot) {
        return new File(fragmentCharactersFolder, uuid + "-slot" + slot + ".yml");
    }

    private void initializeAccount(YamlConfiguration account, UUID uuid, String playerName, int maxSlots, int freeSlots) {
        account.set("schema-version", account.getInt("schema-version", 1));
        account.set("uuid", account.getString("uuid", uuid.toString()));
        account.set("username", playerName == null || playerName.isBlank() ? account.getString("username", "unknown") : playerName);
        account.set("maximum-slots", account.getInt("maximum-slots", maxSlots));
        if (!account.contains("created-at")) {
            account.set("created-at", nowIso());
        }

        for (int slot = 1; slot <= maxSlots; slot++) {
            if (!account.contains("slots." + slot + ".unlocked")) {
                account.set("slots." + slot + ".unlocked", slot <= freeSlots);
            }
            if (!account.contains("slots." + slot + ".occupied")) {
                account.set("slots." + slot + ".occupied", false);
            }
        }
    }

    private void writeCharacter(
            YamlConfiguration character,
            UUID uuid,
            String playerName,
            int slot,
            String race,
            String raceDisplay,
            String name,
            String region,
            int level,
            String createdAt
    ) {
        String profileId = uuid + "-slot" + slot;
        long totalXp = cumulativeXpToReachLevel(level);

        character.set("schema-version", 1);
        character.set("profile-id", profileId);
        character.set("owner-uuid", uuid.toString());
        character.set("owner-name", playerName == null || playerName.isBlank() ? "unknown" : playerName);
        character.set("slot", slot);
        character.set("name", name == null || name.isBlank() ? "Unnamed " + raceDisplay : name);
        character.set("status", "active");
        character.set("creation-complete", true);
        character.set("created-at", character.getString("created-at", createdAt == null ? nowIso() : createdAt));
        character.set("last-played-at", nowIso());

        character.set("race.id", race);
        character.set("race.display-name", raceDisplay);
        character.set("race.trait", defaultTraitForRace(race));
        character.set("origin.region", region);

        character.set("progression.level", level);
        character.set("progression.xp", character.getLong("progression.xp", 0L));
        character.set("progression.total-xp", Math.max(totalXp, character.getLong("progression.total-xp", totalXp)));
        character.set("progression.phase", phaseForLevel(level));
        character.set("progression.unspent-stat-points", character.getInt("progression.unspent-stat-points", 0));
        character.set("progression.unspent-intent-points", character.getInt("progression.unspent-intent-points", 0));

        character.set("fragment-level", character.getDouble("fragment-level", 1.0));
        character.set("fragment-stability", character.getDouble("fragment-stability", 100.0));
        character.set("existence-strain", character.getDouble("existence-strain", 0.0));
        character.set("erasure", character.getDouble("erasure", 0.0));
        character.set("remnant-state", character.getString("remnant-state", "UNCOMMITTED"));
        character.set("profession", character.getString("profession", "UNFORMED"));

        character.set("fragments.capacity", character.getInt("fragments.capacity", 3));
        if (!character.contains("fragments.discovered")) {
            character.createSection("fragments.discovered");
        }
        if (!character.contains("fragments.equipped")) {
            character.createSection("fragments.equipped");
        }
        character.set("fragments.corruption", character.getDouble("fragments.corruption", 0.0));
        character.set("fragments.erasure-pressure", character.getDouble("fragments.erasure-pressure", character.getDouble("erasure", 0.0)));

        character.set("intent.unlocked-slots", character.getInt("intent.unlocked-slots", 1));
        if (!character.contains("intent.active")) {
            character.createSection("intent.active");
        }
        character.set("intent.pressure", character.getDouble("intent.pressure", 0.0));

        applyStatsAndDerived(character, level);
    }

    private void applyStatsAndDerived(YamlConfiguration character, int level) {
        int extra = Math.max(0, level - 1);
        double vitality = character.getDouble("stats.vitality", 5.0 + 0.8 * extra);
        double strength = character.getDouble("stats.strength", 5.0 + 0.75 * extra);
        double dexterity = character.getDouble("stats.dexterity", 5.0 + 0.55 * extra);
        double intelligence = character.getDouble("stats.intelligence", 5.0 + 0.55 * extra);
        double willpower = character.getDouble("stats.willpower", 5.0 + 0.6 * extra);
        double endurance = character.getDouble("stats.endurance", 5.0 + 0.7 * extra);

        character.set("stats.vitality", round(vitality));
        character.set("stats.strength", round(strength));
        character.set("stats.dexterity", round(dexterity));
        character.set("stats.intelligence", round(intelligence));
        character.set("stats.willpower", round(willpower));
        character.set("stats.endurance", round(endurance));

        double erasure = character.getDouble("erasure", character.getDouble("fragments.erasure-pressure", 0.0));
        double maxHealth = round(100 + vitality * 12 + endurance * 6);
        character.set("derived.max-health", maxHealth);
        character.set("derived.attack-power", round(10 + strength * 2));
        character.set("derived.defense", round(endurance * 1.5));
        character.set("derived.crit-chance", round4(0.05 + dexterity * 0.002));
        character.set("derived.crit-damage", round4(1.5 + dexterity * 0.01));
        character.set("derived.magic-power", round(intelligence * 2));
        character.set("derived.resistance", round(willpower * 1.25));
        character.set("derived.movement-speed", 1.0);
        character.set("derived.stability", round(clamp(100 - erasure + willpower * 0.5, 0, 100)));

        if (!character.contains("derived.current-health")) {
            character.set("derived.current-health", maxHealth);
        }
    }

    private long cumulativeXpToReachLevel(int level) {
        long total = 0L;
        for (int current = 1; current < Math.max(1, level); current++) {
            total += (long) Math.floor(900.0 * Math.pow(current, 1.9));
        }
        return total;
    }

    private String phaseForLevel(int level) {
        if (level <= 15) return "Discovery";
        if (level <= 35) return "Commitment";
        if (level <= 50) return "Optimization";
        return "Mastery";
    }

    private String normalizeRace(String race) {
        if (race == null || race.isBlank()) {
            return "remnant";
        }
        return race.toLowerCase(Locale.ROOT).replace(" ", "_");
    }

    private String displayRace(String race) {
        if (race == null || race.isBlank()) {
            return "Remnant";
        }
        String clean = normalizeRace(race);
        return clean.substring(0, 1).toUpperCase(Locale.ROOT) + clean.substring(1).replace("_", " ");
    }

    private String defaultTraitForRace(String race) {
        return switch (normalizeRace(race)) {
            case "sylvae" -> "Living Anchor";
            case "delver" -> "Stonebound Endurance";
            case "vireborn" -> "Pressure Adapted";
            case "nullborn", "echoform", "lumenfae", "threadbound" -> "Locked";
            default -> "Memory Fractured";
        };
    }

    private String nowIso() {
        return java.time.OffsetDateTime.now().toString();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double round4(double value) {
        return Math.round(value * 10000.0) / 10000.0;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("aereth.bridge.admin")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("status")) {
            sender.sendMessage("AerethCreatorFragmentBridge");
            sender.sendMessage("CharacterCreator profile: " + creatorProfilesFile.getPath() + " exists=" + creatorProfilesFile.exists());
            sender.sendMessage("FragmentEngine accounts: " + fragmentAccountsFolder.getPath());
            sender.sendMessage("FragmentEngine characters: " + fragmentCharactersFolder.getPath());
            return true;
        }

        if (args[0].equalsIgnoreCase("syncall")) {
            int synced = syncAllProfilesSafely();
            sender.sendMessage("Synced " + synced + " CharacterCreator profile(s) into FragmentEngine.");
            return true;
        }

        if (args[0].equalsIgnoreCase("sync")) {
            if (args.length >= 2) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                try {
                    boolean result = syncPlayer(target.getUniqueId(), target.getName() == null ? args[1] : target.getName(), true);
                    sender.sendMessage("Sync result for " + args[1] + ": " + result);
                } catch (IOException ex) {
                    sender.sendMessage("Sync failed: " + ex.getMessage());
                    getLogger().log(Level.WARNING, "Manual sync failed", ex);
                }
                return true;
            }

            if (sender instanceof Player player) {
                try {
                    boolean result = syncPlayer(player.getUniqueId(), player.getName(), true);
                    sender.sendMessage("Sync result: " + result);
                } catch (IOException ex) {
                    sender.sendMessage("Sync failed: " + ex.getMessage());
                    getLogger().log(Level.WARNING, "Manual sync failed", ex);
                }
                return true;
            }

            sender.sendMessage("Usage from console: /aerethbridge sync <player>");
            return true;
        }

        sender.sendMessage("Usage: /aerethbridge <status|sync|syncall>");
        return true;
    }
}


