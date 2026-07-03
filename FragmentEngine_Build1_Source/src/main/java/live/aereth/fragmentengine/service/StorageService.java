package live.aereth.fragmentengine.service;

import live.aereth.fragmentengine.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class StorageService {
    private final JavaPlugin plugin;
    private final File accountsFolder;
    private final File charactersFolder;
    private final File agentFolder;

    public StorageService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.accountsFolder = new File(plugin.getDataFolder(), plugin.getConfig().getString("storage.accounts-folder", "accounts"));
        this.charactersFolder = new File(plugin.getDataFolder(), plugin.getConfig().getString("storage.characters-folder", "characters"));
        this.agentFolder = new File(plugin.getDataFolder(), plugin.getConfig().getString("storage.agent-folder", "agent"));
    }

    public void ensureFolders() {
        plugin.getDataFolder().mkdirs();
        accountsFolder.mkdirs();
        charactersFolder.mkdirs();
        agentFolder.mkdirs();
    }

    public File accountFile(UUID uuid) {
        return new File(accountsFolder, uuid + ".yml");
    }

    public File characterFile(UUID uuid, int slot) {
        return new File(charactersFolder, uuid + "-slot" + slot + ".yml");
    }

    public File characterFile(String profileId) {
        return new File(charactersFolder, profileId + ".yml");
    }

    public File getAccountsFolder() {
        return accountsFolder;
    }

    public File getCharactersFolder() {
        return charactersFolder;
    }

    public File getAgentFolder() {
        return agentFolder;
    }

    public YamlConfiguration loadAccount(OfflinePlayer player) {
        File file = accountFile(player.getUniqueId());
        YamlConfiguration account = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            account.set("schema-version", 1);
            account.set("uuid", player.getUniqueId().toString());
            account.set("username", player.getName() == null ? "unknown" : player.getName());
            account.set("active-slot", 0);
            account.set("maximum-slots", plugin.getConfig().getInt("settings.maximum-character-slots", 4));
            account.set("created-at", TimeUtil.nowIso());
            account.set("flags.has-completed-character-creation", false);
            account.set("flags.requires-character-selection", true);

            int freeSlots = plugin.getConfig().getInt("settings.default-free-character-slots", 2);
            int maxSlots = plugin.getConfig().getInt("settings.maximum-character-slots", 4);
            for (int i = 1; i <= maxSlots; i++) {
                account.set("slots." + i + ".unlocked", i <= freeSlots);
                account.set("slots." + i + ".occupied", false);
                account.set("slots." + i + ".race-id", null);
                account.set("slots." + i + ".race-trait", null);
            }
        }

        account.set("last-seen-at", TimeUtil.nowIso());
        return account;
    }

    public YamlConfiguration loadCharacter(UUID uuid, int slot) {
        return YamlConfiguration.loadConfiguration(characterFile(uuid, slot));
    }

    public void saveAccount(UUID uuid, YamlConfiguration account) throws IOException {
        account.save(accountFile(uuid));
    }

    public void saveCharacter(UUID uuid, int slot, YamlConfiguration character) throws IOException {
        character.save(characterFile(uuid, slot));
    }

    public void saveCharacter(String profileId, YamlConfiguration character) throws IOException {
        character.save(characterFile(profileId));
    }
}
