package live.aereth.fragmentengine.command;

import live.aereth.fragmentengine.service.AgentExportService;
import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.FragmentService;
import live.aereth.fragmentengine.service.IntentService;
import live.aereth.fragmentengine.service.DisciplineService;
import live.aereth.fragmentengine.service.AbilityService;
import live.aereth.fragmentengine.service.AbilityActivationService;
import live.aereth.fragmentengine.service.LegacyCommandService;
import live.aereth.fragmentengine.service.ProgressionService;
import live.aereth.fragmentengine.gui.CharacterCardGui;
import live.aereth.fragmentengine.gui.IntentSlotsGui;
import live.aereth.fragmentengine.gui.DisciplineCodexGui;
import live.aereth.fragmentengine.gui.AbilityCodexGui;
import live.aereth.fragmentengine.gui.AbilityLoadoutGui;
import live.aereth.fragmentengine.gui.AbilityActivationGui;
import live.aereth.fragmentengine.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AerethCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final FragmentService fragments;
    private final IntentService intents;
    private final DisciplineService disciplines;
    private final AbilityService abilities;
    private final LegacyCommandService legacy;
    private final AgentExportService agentExport;
    private final CharacterCardGui characterCardGui;
    private final IntentSlotsGui intentSlotsGui;
    private final DisciplineCodexGui disciplineCodexGui;
    private final AbilityCodexGui abilityCodexGui;
    private final AbilityLoadoutGui abilityLoadoutGui;
    private final AbilityActivationService abilityActivation;
    private final AbilityActivationGui abilityActivationGui;

    public AerethCommand(JavaPlugin plugin, CharacterService characters, FragmentService fragments, IntentService intents, DisciplineService disciplines, AbilityService abilities, LegacyCommandService legacy, AgentExportService agentExport) {
        this.plugin = plugin;
        this.characters = characters;
        this.fragments = fragments;
        this.intents = intents;
        this.disciplines = disciplines;
        this.abilities = abilities;
        this.legacy = legacy;
        this.agentExport = agentExport;
        this.characterCardGui = new CharacterCardGui(plugin, characters, fragments, intents, disciplines, abilities);
        this.intentSlotsGui = new IntentSlotsGui(plugin, characters, intents);
        this.disciplineCodexGui = new DisciplineCodexGui(plugin, characters, disciplines);
        this.abilityCodexGui = new AbilityCodexGui(plugin, characters, disciplines, abilities);
        this.abilityLoadoutGui = new AbilityLoadoutGui(plugin, characters, disciplines, abilities);
        this.abilityActivation = new AbilityActivationService(plugin, characters, disciplines, abilities);
        this.abilityActivationGui = new AbilityActivationGui(plugin, characters, disciplines, abilities, abilityActivation);
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
                case "card" -> card(sender);
                case "profile" -> profile(sender, args);
                case "character" -> character(sender, args);
                case "stats" -> stats(sender, args);
                case "fragments" -> fragments(sender, args);
                case "discover" -> discoverFragment(sender, args);
                case "attach" -> attachFragment(sender, args);
                case "detach" -> detachFragment(sender, args);
                case "intent" -> intent(sender, args);
                case "intentgui" -> intentGui(sender);
                case "intentlist" -> intentList(sender);
                case "setintent" -> setIntent(sender, args);
                case "clearintent" -> clearIntent(sender, args);
                case "disciplinegui" -> disciplineGui(sender);
                case "discipline" -> discipline(sender, args);
                case "disciplinelist" -> disciplineList(sender);
                case "setdiscipline" -> setDiscipline(sender, args);
                case "cleardiscipline" -> clearDiscipline(sender, args);
                case "disciplineprogress" -> disciplineProgress(sender, args);
                case "adddisciplinexp" -> addDisciplineXp(sender, args);
                case "setdisciplinerank" -> setDisciplineRank(sender, args);
                case "resetdisciplineprogress" -> resetDisciplineProgress(sender, args);
                case "abilityloadout" -> abilityLoadout(sender);
                case "abilityactivation" -> abilityActivationGui(sender);
                case "abilityactivate" -> abilityActivate(sender, args);
                case "abilitycooldowns" -> abilityCooldowns(sender, args);
                case "abilitygui" -> abilityGui(sender);
                case "abilitylist" -> abilityList(sender);
                case "abilities" -> abilities(sender, args);
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
                case "legacyattach" -> legacyAttach(sender, args);
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
        sender.sendMessage(prefix() + Text.color("&7FragmentEngine Build 3B"));
        sender.sendMessage(Text.color("&b/aereth status"));
        sender.sendMessage(Text.color("&b/aereth card"));
        sender.sendMessage(Text.color("&b/aereth profile <player>"));
        sender.sendMessage(Text.color("&b/aereth character <player>"));
        sender.sendMessage(Text.color("&b/aereth stats <player>"));
        sender.sendMessage(Text.color("&b/aereth fragments <player>"));
        sender.sendMessage(Text.color("&b/aereth discover <player> <fragmentId>"));
        sender.sendMessage(Text.color("&b/aereth attach <player> <fragmentId>"));
        sender.sendMessage(Text.color("&b/aereth detach <player> <fragmentId>"));
        sender.sendMessage(Text.color("&b/aereth intent <player>"));
        sender.sendMessage(Text.color("&b/aereth intentgui"));
        sender.sendMessage(Text.color("&b/aereth intentlist"));
        sender.sendMessage(Text.color("&b/aereth setintent <player> <slot> <intentId>"));
        sender.sendMessage(Text.color("&b/aereth clearintent <player> <slot>"));
        sender.sendMessage(Text.color("&b/aereth disciplinelist"));
        sender.sendMessage(Text.color("&b/aereth disciplinegui"));
        sender.sendMessage(Text.color("&b/aereth discipline <player>"));
        sender.sendMessage(Text.color("&b/aereth setdiscipline <player> <disciplineId>"));
        sender.sendMessage(Text.color("&b/aereth cleardiscipline <player>"));
        sender.sendMessage(Text.color("&b/aereth disciplineprogress <player>"));
        sender.sendMessage(Text.color("&b/aereth adddisciplinexp <player> <amount>"));
        sender.sendMessage(Text.color("&b/aereth setdisciplinerank <player> <rank>"));
        sender.sendMessage(Text.color("&b/aereth resetdisciplineprogress <player>"));
        sender.sendMessage(Text.color("&b/aereth abilityloadout"));
        sender.sendMessage(Text.color("&b/aereth abilityactivation"));
        sender.sendMessage(Text.color("&b/aereth abilityactivate <player> <slot>"));
        sender.sendMessage(Text.color("&b/aereth abilitycooldowns <player>"));
        sender.sendMessage(Text.color("&b/aereth abilitygui"));
        sender.sendMessage(Text.color("&b/aereth abilitylist"));
        sender.sendMessage(Text.color("&b/aereth abilities <player>"));
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
        sender.sendMessage(prefix() + Text.color("&7Fragments: &b" + fragments.allFragmentIds().size()));
        sender.sendMessage(prefix() + Text.color("&7Disciplines: &b" + disciplines.allDisciplineIds().size()));
        sender.sendMessage(prefix() + Text.color("&7PlaceholderAPI: &b" + Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")));
    }


    private void card(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Character Card GUI."));
            return;
        }
        characterCardGui.open(player);
    }

    private void intentGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Intent Slots GUI."));
            return;
        }
        intentSlotsGui.open(player);
    }

    private void disciplineGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Discipline Codex GUI."));
            return;
        }
        disciplineCodexGui.open(player);
    }
    private void abilityLoadout(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Ability Loadout GUI."));
            return;
        }
        abilityLoadoutGui.open(player);
    }
    private void abilityGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Ability Codex GUI."));
            return;
        }
        abilityCodexGui.open(player);
    }
    private void abilityActivationGui(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(prefix() + Text.color("&cOnly players can open the Ability Activation GUI."));
            return;
        }
        abilityActivationGui.open(player);
    }

    private void abilityActivate(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth abilityactivate <player> <slot>");
        int slot = parseInt(args[2], "slot");
        AbilityActivationService.ActivationResult result = abilityActivation.activate(Bukkit.getOfflinePlayer(args[1]), slot);
        sender.sendMessage(prefix() + Text.color("&aAbility activated: &f" + result.display()
                + " &8/ &7Slot: &f" + result.slot()
                + " &8/ &7Cooldown: &f" + result.cooldownSeconds() + "s"));
    }

    private void abilityCooldowns(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth abilitycooldowns <player>");
        AbilityActivationService.CooldownSummary summary = abilityActivation.cooldowns(Bukkit.getOfflinePlayer(args[1]));
        sender.sendMessage(prefix() + Text.color("&7Ability cooldowns: &b" + args[1]));
        if (summary.active().isEmpty()) {
            sender.sendMessage(Text.color("&7Active: &fnone"));
            return;
        }
        for (AbilityActivationService.ActiveCooldown cooldown : summary.active()) {
            sender.sendMessage(Text.color("&7Slot " + cooldown.slot() + ": &f" + cooldown.display()
                    + " &8/ &7Remaining: &f" + cooldown.remainingSeconds() + "s"));
        }
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

        FragmentService.FragmentSummary summary = fragments.summary(character);
        sender.sendMessage(prefix() + Text.color("&7Character: &b" + character.getString("name", "Unnamed")));
        sender.sendMessage(Text.color("&7Race: &f" + character.getString("race.display", character.getString("race.id", "unformed")) + " &8(" + character.getString("race.trait", "-") + "&8)"));
        sender.sendMessage(Text.color("&7Level: &f" + character.getInt("progression.level", 1) + " &8/ &7Phase: &f" + character.getString("progression.phase", "discovery")));
        sender.sendMessage(Text.color("&7XP: &f" + character.getLong("progression.xp", 0L) + " / " + characters.progression().xpRequiredForLevel(character.getInt("progression.level", 1)) + " &8(total " + character.getLong("progression.total-xp", 0L) + ")"));
        sender.sendMessage(Text.color("&7Unspent: &f" + character.getInt("progression.unspent-stat-points", 0) + " stat &8/ &f" + character.getInt("progression.unspent-intent-points", 0) + " intent"));
        sender.sendMessage(Text.color("&7HP: &f" + character.getDouble("derived.current-health", 0.0) + " / " + character.getDouble("derived.max-health", 0.0)));
        sender.sendMessage(Text.color("&7Attack: &f" + character.getDouble("derived.attack-power", 0.0) + " &8/ &7Defense: &f" + character.getDouble("derived.defense", 0.0)));
        sender.sendMessage(Text.color("&7Fragments: &f" + summary.equipped().size() + " / " + summary.capacity() + " &8/ &7Pressure: &f" + summary.totalPressure() + " &8/ &7Stability: &f" + summary.stability()));
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
                double fragmentBonus = character.getDouble("stats.fragment-bonus." + key, 0.0);
                double disciplineBonus = character.getDouble("stats.discipline-bonus." + key, 0.0);
                sender.sendMessage(Text.color("&7" + key + ": &f" + totals.getDouble(key) + " &8(fragment +" + fragmentBonus + " / discipline +" + disciplineBonus + ")"));
            }
        }
        sender.sendMessage(Text.color("&7Derived: &fHP " + character.getDouble("derived.max-health", 0.0)
                + " &8/ &fATK " + character.getDouble("derived.attack-power", 0.0)
                + " &8/ &fDEF " + character.getDouble("derived.defense", 0.0)
                + " &8/ &fEVA " + character.getDouble("derived.evasion", 0.0)));
    }

    private void fragments(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth fragments <player>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        YamlConfiguration character = characters.getActiveCharacter(target);
        if (character == null) {
            sender.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        FragmentService.FragmentSummary summary = fragments.summary(character);
        sender.sendMessage(prefix() + Text.color("&7Fragments: &b" + character.getString("name", "Unnamed")));
        sender.sendMessage(Text.color("&7Capacity: &f" + summary.equipped().size() + " / " + summary.capacity()));
        sender.sendMessage(Text.color("&7Pressure: &f" + summary.totalPressure() + " &8/ &7Stability: &f" + summary.stability() + " &8/ &7Erasure pressure: &f" + summary.erasurePressure()));
        sender.sendMessage(Text.color("&7Equipped: &f" + readableFragments(summary.equipped())));
        sender.sendMessage(Text.color("&7Discovered: &f" + readableFragments(summary.discovered())));
    }

    private void discoverFragment(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth discover <player> <fragmentId>");
        FragmentService.FragmentResult result = fragments.discoverFragment(Bukkit.getOfflinePlayer(args[1]), args[2]);
        sender.sendMessage(prefix() + Text.color("&aFragment discovered: &f" + fragments.displayName(result.fragmentId()) + " &8(" + result.status() + ")"));
    }

    private void attachFragment(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth attach <player> <fragmentId>");
        FragmentService.FragmentResult result = fragments.attachFragment(Bukkit.getOfflinePlayer(args[1]), args[2]);
        sender.sendMessage(prefix() + Text.color("&aFragment attached: &f" + fragments.displayName(result.fragmentId()) + " &8(" + result.status() + ")"));
    }

    private void detachFragment(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth detach <player> <fragmentId>");
        FragmentService.FragmentResult result = fragments.detachFragment(Bukkit.getOfflinePlayer(args[1]), args[2]);
        sender.sendMessage(prefix() + Text.color("&aFragment detached: &f" + fragments.displayName(result.fragmentId()) + " &8(" + result.status() + ")"));
    }

    private void intent(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth intent <player>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        YamlConfiguration character = characters.getActiveCharacter(target);
        if (character == null) {
            sender.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        IntentService.IntentSummary summary = intents.summary(character);
        sender.sendMessage(prefix() + Text.color("&7Intent: &b" + character.getString("name", "Unnamed")));
        sender.sendMessage(Text.color("&7Primary: &f" + intents.displayName(summary.primary())));
        sender.sendMessage(Text.color("&7Slots: &f" + summary.usedSlots() + " / " + summary.maxSlots()));
        sender.sendMessage(Text.color("&7Pressure: &f" + summary.pressure() + " &8/ &7Stability impact: &f" + summary.stabilityImpact()));
        sender.sendMessage(Text.color("&7Active: &f" + readableIntentSlots(summary.slots())));
    }

    private void intentList(CommandSender sender) {
        sender.sendMessage(prefix() + Text.color("&7Known intents"));
        for (String id : intents.allIntentIds()) {
            IntentService.IntentDefinition definition = intents.definition(id);
            sender.sendMessage(Text.color("&b" + definition.id() + " &8- &f" + definition.display()
                    + " &8/ &7Pressure: &f" + definition.pressure()
                    + " &8/ &7Stability: &f" + definition.stabilityImpact()));
        }
    }

    private void setIntent(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 4, "/aereth setintent <player> <slot> <intentId>");
        IntentService.IntentResult result = intents.setIntent(Bukkit.getOfflinePlayer(args[1]), args[2], args[3]);
        sender.sendMessage(prefix() + Text.color("&aIntent set: &f" + result.slot() + " -> " + intents.displayName(result.intentId())
                + " &8/ &7Pressure: &f" + result.summary().pressure()));
    }

    private void clearIntent(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth clearintent <player> <slot>");
        IntentService.IntentResult result = intents.clearIntent(Bukkit.getOfflinePlayer(args[1]), args[2]);
        sender.sendMessage(prefix() + Text.color("&aIntent cleared: &f" + result.slot()
                + " &8/ &7Pressure: &f" + result.summary().pressure()));
    }

    private void discipline(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth discipline <player>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        YamlConfiguration character = characters.getActiveCharacter(target);
        if (character == null) {
            sender.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        DisciplineService.DisciplineSummary summary = disciplines.summary(character);
        sender.sendMessage(prefix() + Text.color("&7Discipline: &b" + character.getString("name", "Unnamed")));
        sender.sendMessage(Text.color("&7Current: &f" + summary.display() + " &8(" + summary.id() + ")"));
        sender.sendMessage(Text.color("&7Family: &f" + summary.family()));
        sender.sendMessage(Text.color("&7Selected: &f" + summary.selected() + " &8/ &7Unlocked: &f" + summary.unlocked()));
        sender.sendMessage(Text.color("&7Required level: &f" + summary.unlockLevel() + " &8/ &7Current level: &f" + character.getInt("progression.level", 1)));
    }

    private void disciplineList(CommandSender sender) {
        sender.sendMessage(prefix() + Text.color("&7Known disciplines"));
        for (String id : disciplines.allDisciplineIds()) {
            DisciplineService.DisciplineDefinition definition = disciplines.definition(id);
            sender.sendMessage(Text.color("&b" + definition.id() + " &8- &f" + definition.display()
                    + " &8/ &7Family: &f" + definition.family()
                    + " &8/ &7Unlock: &f" + definition.unlockLevel()));
        }
    }

    private void setDiscipline(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth setdiscipline <player> <disciplineId>");
        DisciplineService.DisciplineResult result = disciplines.setDiscipline(Bukkit.getOfflinePlayer(args[1]), args[2]);
        sender.sendMessage(prefix() + Text.color("&aDiscipline set: &f" + result.summary().display()
                + " &8/ &7Family: &f" + result.summary().family()));
    }

    private void clearDiscipline(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 2, "/aereth cleardiscipline <player>");
        DisciplineService.DisciplineResult result = disciplines.clearDiscipline(Bukkit.getOfflinePlayer(args[1]));
        sender.sendMessage(prefix() + Text.color("&aDiscipline cleared: &f" + result.summary().display()));
    }

    private void disciplineProgress(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth disciplineprogress <player>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        YamlConfiguration character = characters.getActiveCharacter(target);
        if (character == null) {
            sender.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        DisciplineService.DisciplineSummary discipline = disciplines.summary(character);
        DisciplineService.DisciplineProgressSummary progress = disciplines.progress(character);

        sender.sendMessage(prefix() + Text.color("&7Discipline Progress: &b" + character.getString("name", "Unnamed")));
        sender.sendMessage(Text.color("&7Discipline: &f" + discipline.display() + " &8(" + discipline.id() + ")"));
        sender.sendMessage(Text.color("&7Rank: &f" + progress.rank() + " &8/ &7Name: &f" + progress.rankName()));
        sender.sendMessage(Text.color("&7XP: &f" + progress.xp() + " / " + progress.xpRequired()));
        sender.sendMessage(Text.color("&7Progress: &f" + progress.progressPercent() + "% &8/ &7Max rank: &f" + progress.maxRank()));
    }

    private void addDisciplineXp(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth adddisciplinexp <player> <amount>");
        long amount = parseLong(args[2], "amount");

        DisciplineService.DisciplineProgressResult result = disciplines.addDisciplineXp(Bukkit.getOfflinePlayer(args[1]), amount);

        if (result.ranksGained() > 0) {
            sender.sendMessage(prefix() + Text.color("&dDiscipline rank up. &7Ranks gained: &f" + result.ranksGained()));
        }

        sender.sendMessage(prefix() + Text.color("&aDiscipline XP added. &7Rank: &f" + result.progress().rank()
                + " &8/ &7XP: &f" + result.progress().xp() + " / " + result.progress().xpRequired()));
    }

    private void setDisciplineRank(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 3, "/aereth setdisciplinerank <player> <rank>");
        int rank = parseInt(args[2], "rank");

        DisciplineService.DisciplineProgressResult result = disciplines.setDisciplineRank(Bukkit.getOfflinePlayer(args[1]), rank);
        sender.sendMessage(prefix() + Text.color("&aDiscipline rank updated. &7Rank: &f" + result.progress().rank()
                + " &8/ &7Name: &f" + result.progress().rankName()));
    }

    private void resetDisciplineProgress(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 2, "/aereth resetdisciplineprogress <player>");

        DisciplineService.DisciplineProgressResult result = disciplines.resetDisciplineProgress(Bukkit.getOfflinePlayer(args[1]));
        sender.sendMessage(prefix() + Text.color("&aDiscipline progress reset. &7Rank: &f" + result.progress().rank()
                + " &8/ &7XP: &f" + result.progress().xp()));
    }

    private void abilityList(CommandSender sender) {
        sender.sendMessage(prefix() + Text.color("&7Known Discipline abilities"));

        for (AbilityService.AbilityDefinition definition : this.abilities.allDefinitions()) {
            sender.sendMessage(Text.color("&b" + definition.id()
                    + " &8- &f" + definition.display()
                    + " &8/ &7Discipline: &f" + definition.discipline()
                    + " &8/ &7Rank: &f" + definition.unlockRank()
                    + " &8/ &7Cost: &f" + definition.costAmount() + " " + definition.costType()
                    + " &8/ &7Cooldown: &f" + definition.cooldownSeconds() + "s"));
        }
    }

    private void abilities(CommandSender sender, String[] args) {
        requireArgs(args, 2, "/aereth abilities <player>");
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        YamlConfiguration character = characters.getActiveCharacter(target);

        if (character == null) {
            sender.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        AbilityService.AbilitySummary summary = this.abilities.summary(character);

        sender.sendMessage(prefix() + Text.color("&7Abilities: &b" + character.getString("name", "Unnamed")));
        sender.sendMessage(Text.color("&7Discipline: &f" + summary.discipline() + " &8/ &7Rank: &f" + summary.rank()));
        sender.sendMessage(Text.color("&7Unlocked: &f" + String.join(", ", summary.unlocked())));
        sender.sendMessage(Text.color("&7Locked: &f" + String.join(", ", summary.locked())));
        sender.sendMessage(Text.color("&7Count: &f" + summary.count()));
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
        requireArgs(args, 3, "/aereth legacyattach <player> <fragmentId>");
        legacy.attach(Bukkit.getOfflinePlayer(args[1]), args[2]);
        sender.sendMessage(prefix() + Text.color("&aLegacy fragment attached."));
    }

    private void legacyErasure(CommandSender sender, String[] args) throws IOException {
        requireArgs(args, 4, "/aereth erasure <player> <amount> <source>");
        legacy.erasure(Bukkit.getOfflinePlayer(args[1]), parseDouble(args[2], "amount"), args[3]);
        sender.sendMessage(prefix() + Text.color("&aErasure updated."));
    }

    private String readableFragments(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return "none";
        }
        List<String> names = new ArrayList<>();
        for (String id : ids) {
            names.add(fragments.displayName(id));
        }
        return String.join(", ", names);
    }

    private String readableIntentSlots(Map<String, String> slots) {
        if (slots == null || slots.isEmpty()) {
            return "none";
        }
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, String> entry : slots.entrySet()) {
            names.add(entry.getKey() + "=" + intents.displayName(entry.getValue()));
        }
        return String.join(", ", names);
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
            return partial(args[0], List.of("status", "card", "intentgui", "disciplinegui", "profile", "character", "stats", "fragments", "discover", "attach", "detach", "intent", "intentlist", "setintent", "clearintent", "discipline", "disciplinelist", "setdiscipline", "cleardiscipline", "disciplineprogress", "adddisciplinexp", "setdisciplinerank", "resetdisciplineprogress", "abilitylist", "abilities", "createcharacter", "addxp", "setlevel", "setrace", "save", "reload", "diagnostics", "agent", "activity", "echo", "legacyattach", "erasure"));
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("abilityactivate")) {
            return partial(args[2], List.of("1", "2", "3", "4"));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("agent")) {
            return partial(args[1], List.of("export"));
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("discover") || args[0].equalsIgnoreCase("attach") || args[0].equalsIgnoreCase("detach"))) {
            return partial(args[2], fragments.allFragmentIds());
        }
        if (args.length == 3 && (args[0].equalsIgnoreCase("setintent") || args[0].equalsIgnoreCase("clearintent"))) {
            return partial(args[2], List.of("slot1", "slot2", "slot3", "slot4", "slot5"));
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("setintent")) {
            return partial(args[3], intents.allIntentIds());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("setdiscipline")) {
            return partial(args[2], disciplines.allDisciplineIds());
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