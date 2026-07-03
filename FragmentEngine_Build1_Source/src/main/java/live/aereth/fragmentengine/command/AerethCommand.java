package live.aereth.fragmentengine.command;

import live.aereth.fragmentengine.service.AgentExportService;
import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.LegacyCommandService;
import live.aereth.fragmentengine.service.ProgressionService;
import live.aereth.fragmentengine.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AerethCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final LegacyCommandService legacy;
    private final AgentExportService agentExport;

    public AerethCommand(JavaPlugin plugin, CharacterService characters, LegacyCommandService legacy, AgentExportService agentExport) {
        this.plugin = plugin;
        this.characters = characters;
        this.legacy = legacy;
        this.agentExport = agentExport;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            help(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        try {
            switch (sub) {
                case "status" -> status(sender);
                case "profile" -> profile(sender, args);
                case "character" -> character(sender, args);
                case "stats" -> stats(sender, args);
                case "createcharacter" -> createCharacter(sender, args);
                case "addxp" -> addXp(sender, args);
                case "setlevel" -> setLevel(sender, args);
                case "setrace" -> setRace(sender, args);
                case "save" -> save(sender);
                case "reload" -> reload(sender);
                case "diagnostics" -> diagnostics(sender);
                case "agent" -> agent(sender, args);
                case "activity" -> legacyActivity(sender, args);
                case "echo" -> legacyEcho(sender, args);
                case "attach" -> legacyAttach(sender, args);
                case "erasure" -> legacyErasure(sender, args);
                default -> help(sender);
            }
        } catch (Exception ex) {
            sender.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            plugin.getLogger().warning("Command failed: " + String.join(" ", args) + " -> " + ex.getMessage());
        }

        return true;
    }

    private void help(CommandSender sender) {
        sender.sendMessage(prefix() + Text.color("&7FragmentEngine Build 2A"));
        sender.sendMessage(Text.color("&b/aereth status"));
        sender.sendMessage(Text.color("&b/aereth profile <player>"));
        sender.sendMessage(Text.color("&b/aereth character <player>"));
        sender.sendMessage(Text.color("&b/aereth stats <player>"));
        sender.sendMessage(Text.color("&b/aereth createcharacter <player> <slot> <race> [name...]"));
        sender.sendMessage(Text.color("&b/aereth addxp <player> <amount>"));
        sender.sendMessage(Text.color("&b/aereth setlevel <player> <level>"));
        sender.sendMessage(Text.color("&b/aereth setrace <player> <race>"));
        sender.sendMessage(Text.color("&b/aereth save"));
        sender.sendMessage(Text.color("&b/aereth agent export"));
    }

    private void status(CommandSender sender) {
        sender.sendMessage(prefix() + Text.color("&7Version: &b" + plugin.getDescription().getVersion()));
        sender.sendMessage(prefix() + Text.color("&7Accounts: &b" + count(characters.storage().getAccountsFolder())));
        sender.sendMessage(prefix() + Text.color("&7Characters: &b" + count(characters.storage().getCharactersFolder())));
        sender.sendMessage(prefix() + Text.color("&7PlaceholderAPI: &b" + Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")));
    }

    private void profile(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth profile <player>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        YamlConfiguration account = characters.getAccount(target);
        sender.sendMessage(prefix() + Text.color("&7Profile: &b" + target.getName()));
        sender.sendMessage(Text.color("&7UUID: &f" + target.getUniqueId()));
        sender.sendMessage(Text.color("&7Active slot: &f" + account.getInt("active-slot", 0)));
        sender.sendMessage(Text.color("&7Maximum slots: &f" + account.getInt("maximum-slots", 4)));

        for (int i = 1; i <= account.getInt("maximum-slots", 4); i++) {
            boolean unlocked = account.getBoolean("slots." + i + ".unlocked", false);
            boolean occupied = account.getBoolean("slots." + i + ".occupied", false);
            String race = account.getString("slots." + i + ".race-id", "-");
            sender.sendMessage(Text.color("&7Slot " + i + ": &f" + (unlocked ? "unlocked" : "locked") + " &8/ &f" + (occupied ? race : "empty")));
        }
    }

    private void character(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth character <player>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        YamlConfiguration character = characters.getActiveCharacter(target);
        if (character == null) {
            sender.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        sender.sendMessage(prefix() + Text.color("&7Character: &b" + character.getString("name", "Unnamed")));
        sender.sendMessage(Text.color("&7Race: &f" + character.getString("race.display", character.getString("race.id", "unformed")) + " &8(" + character.getString("race.trait", "-") + "&8)"));
        sender.sendMessage(Text.color("&7Level: &f" + character.getInt("progression.level", 1) + " &8/ &7Phase: &f" + character.getString("progression.phase", "discovery")));
        sender.sendMessage(Text.color("&7XP: &f" + character.getLong("progression.xp", 0L) + " / " + characters.progression().xpRequiredForLevel(character.getInt("progression.level", 1)) + " &8(total " + character.getLong("progression.total-xp", 0L) + ")"));
        sender.sendMessage(Text.color("&7Unspent: &f" + character.getInt("progression.unspent-stat-points", 0) + " stat &8/ &f" + character.getInt("progression.unspent-intent-points", 0) + " intent"));
        sender.sendMessage(Text.color("&7HP: &f" + character.getDouble("derived.current-health", 0.0) + " / " + character.getDouble("derived.max-health", 0.0)));
        sender.sendMessage(Text.color("&7Attack: &f" + character.getDouble("derived.attack-power", 0.0) + " &8/ &7Defense: &f" + character.getDouble("derived.defense", 0.0)));
        sender.sendMessage(Text.color("&7Erasure: &f" + character.getDouble("erasure", 0.0) + " &8/ &7Stability: &f" + character.getDouble("derived.stability", 0.0)));
    }

    private void stats(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth stats <player>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        YamlConfiguration character = characters.getActiveCharacter(target);
        if (character == null) {
            sender.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        sender.sendMessage(prefix() + Text.color("&7Stats: &b" + character.getString("name", "Unnamed")));
        ConfigurationSection totals = character.getConfigurationSection("stats.total");
        if (totals != null) {
            for (String key : totals.getKeys(false)) {
                sender.sendMessage(Text.color("&7" + key + ": &f" + totals.getDouble(key)));
            }
        }
        sender.sendMessage(Text.color("&7Derived: &fHP " + character.getDouble("derived.max-health", 0.0)
                + " &8/ &fATK " + character.getDouble("derived.attack-power", 0.0)
                + " &8/ &fDEF " + character.getDouble("derived.defense", 0.0)
                + " &8/ &fEVA " + character.getDouble("derived.evasion", 0.0)));
    }

    private void createCharacter(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 4, "/aereth createcharacter <player> <slot> <race> [name...]");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        int slot = parseInt(args[2], "slot");
        String race = args[3];
        String name = args.length >= 5 ? String.join(" ", Arrays.copyOfRange(args, 4, args.length)) : null;
        YamlConfiguration character = characters.createCharacter(target, slot, race, name);
        sender.sendMessage(prefix() + Text.color("&aCharacter created: &f" + character.getString("name", "Unnamed") + " &7(" + character.getString("race.display", "Remnant") + ")"));
    }

    private void addXp(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth addxp <player> <amount>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        long amount = parseLong(args[2], "amount");
        ProgressionService.LevelResult result = characters.addXp(target, amount);
        if (result.leveledUp()) {
            sender.sendMessage(prefix() + Text.color("&dLevel up. &7" + result.oldLevel() + " -> &f" + result.level() + " &8(+" + result.levelsGained() + ")"));
        }
        sender.sendMessage(prefix() + Text.color("&aXP added. &7Level: &f" + result.level() + " &7XP: &f" + result.xp()));
    }

    private void setLevel(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth setlevel <player> <level>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        int level = parseInt(args[2], "level");
        characters.setLevel(target, level);
        sender.sendMessage(prefix() + Text.color("&aLevel updated."));
    }

    private void setRace(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth setrace <player> <race>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        characters.setRace(target, args[2]);
        sender.sendMessage(prefix() + Text.color("&aRace updated."));
    }

    private void save(CommandSender sender) throws IOException {
        agentExport.exportAll();
        sender.sendMessage(prefix() + Text.color(plugin.getConfig().getString("messages.data-saved", "&aSaved.")));
    }

    private void reload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(prefix() + Text.color("&aFragmentEngine config reloaded."));
    }

    private void diagnostics(CommandSender sender) throws IOException {
        agentExport.exportAll();
        sender.sendMessage(prefix() + Text.color(plugin.getConfig().getString("messages.diagnostics-exported", "&aDiagnostics exported.")));
    }

    private void agent(CommandSender sender, String[] args) throws IOException {
        if (args.length >= 2 && args[1].equalsIgnoreCase("export")) {
            agentExport.exportAll();
            sender.sendMessage(prefix() + Text.color(plugin.getConfig().getString("messages.diagnostics-exported", "&aDiagnostics exported.")));
            return;
        }
        sender.sendMessage(prefix() + Text.color("&cUsage: /aereth agent export"));
    }

    private void legacyActivity(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 5, "/aereth activity <player> <activityType> <amount> <source>");
        legacy.activity(Bukkit.getOfflinePlayer(args[1]), args[2], parseDouble(args[3], "amount"), args[4]);
        sender.sendMessage(prefix() + Text.color("&aActivity recorded."));
    }

    private void legacyEcho(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 5, "/aereth echo <player> <echoId> <scope> <source>");
        legacy.echo(Bukkit.getOfflinePlayer(args[1]), args[2], args[3], args[4]);
        sender.sendMessage(prefix() + Text.color("&aEcho recorded."));
    }

    private void legacyAttach(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth attach <player> <fragmentId>");
        legacy.attach(Bukkit.getOfflinePlayer(args[1]), args[2]);
        sender.sendMessage(prefix() + Text.color("&aFragment attached."));
    }

    private void legacyErasure(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 4, "/aereth erasure <player> <amount> <source>");
        legacy.erasure(Bukkit.getOfflinePlayer(args[1]), parseDouble(args[2], "amount"), args[3]);
        sender.sendMessage(prefix() + Text.color("&aErasure updated."));
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }

    private void requireArgs(String[] args, int min, String usage) {
        if (args.length < min) {
            throw new IllegalArgumentException("Usage: " + usage);
        }
    }

    private int parseInt(String raw, String name) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid " + name + ": " + raw);
        }
    }

    private long parseLong(String raw, String name) {
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid " + name + ": " + raw);
        }
    }

    private double parseDouble(String raw, String name) {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid " + name + ": " + raw);
        }
    }

    private int count(java.io.File folder) {
        java.io.File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        return files == null ? 0 : files.length;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return partial(args[0], List.of("status", "profile", "character", "stats", "createcharacter", "addxp", "setlevel", "setrace", "save", "reload", "diagnostics", "agent", "activity", "echo", "attach", "erasure"));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("agent")) {
            return partial(args[1], List.of("export"));
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("createcharacter")) {
            return partial(args[3], List.of("remnant", "sylvae", "delver", "vireborn"));
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("setrace")) {
            return partial(args[2], List.of("remnant", "sylvae", "delver", "vireborn"));
        }
        return new ArrayList<>();
    }

    private List<String> partial(String input, List<String> options) {
        String lower = input.toLowerCase();
        return options.stream().filter(option -> option.startsWith(lower)).toList();
    }
}