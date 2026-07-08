package live.aereth.fragmentengine.gui;

import live.aereth.fragmentengine.service.AbilityService;
import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.DisciplineService;
import live.aereth.fragmentengine.service.FragmentService;
import live.aereth.fragmentengine.service.IntentService;
import live.aereth.fragmentengine.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

public class CharacterCardGui {
    public static final String TITLE = "&b✦ Character Card ✦";

    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final FragmentService fragments;
    private final IntentService intents;
    private final DisciplineService disciplines;
    private final AbilityService abilities;

    public CharacterCardGui(JavaPlugin plugin, CharacterService characters, FragmentService fragments,
                            IntentService intents, DisciplineService disciplines, AbilityService abilities) {
        this.plugin = plugin;
        this.characters = characters;
        this.fragments = fragments;
        this.intents = intents;
        this.disciplines = disciplines;
        this.abilities = abilities;
    }

    public void open(Player player) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            player.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        FragmentService.FragmentSummary fragmentSummary = fragments.summary(character);
        IntentService.IntentSummary intentSummary = intents.summary(character);
        DisciplineService.DisciplineSummary disciplineSummary = disciplines.summary(character);
        DisciplineService.DisciplineProgressSummary disciplineProgress = disciplines.progress(character);
        AbilityService.AbilitySummary abilitySummary = abilities.summary(character);

        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.playerHead(player, "&b" + character.getString("name", player.getName()), GuiItem.lore(
                "&7Owner: &f" + player.getName(),
                "&7Slot: &f" + character.getInt("slot", 0),
                "&7Profile: &8" + character.getString("profile-id", "unknown")
        )));

        inventory.setItem(10, GuiItem.item(Material.ENCHANTED_BOOK, "&bRace", GuiItem.lore(
                "&7Race: &f" + character.getString("race.display", character.getString("race.id", "unformed")),
                "&7Trait: &f" + character.getString("race.trait", "none"),
                "&7Condition: &fRemnant"
        )));

        inventory.setItem(12, GuiItem.item(Material.EXPERIENCE_BOTTLE, "&bProgression", GuiItem.lore(
                "&7Level: &c" + character.getInt("progression.level", 1),
                "&7Phase: &f" + character.getString("progression.phase", "discovery"),
                "&7XP: &f" + character.getLong("progression.xp", 0L)
                        + " &8/ &f" + characters.progression().xpRequiredForLevel(character.getInt("progression.level", 1)),
                "&7Total XP: &f" + character.getLong("progression.total-xp", 0L),
                "&7Unspent: &f" + character.getInt("progression.unspent-stat-points", 0)
                        + " stat &8/ &f" + character.getInt("progression.unspent-intent-points", 0) + " intent"
        )));

        inventory.setItem(14, GuiItem.item(Material.APPLE, "&bVitals", GuiItem.lore(
                "&7HP: &f" + round(character.getDouble("derived.current-health", character.getDouble("derived.max-health", 0.0)))
                        + " &8/ &f" + round(character.getDouble("derived.max-health", 0.0)),
                "&7Stability: &f" + round(fragmentSummary.stability()),
                "&7Erasure Pressure: &f" + round(fragmentSummary.erasurePressure())
        )));

        inventory.setItem(16, GuiItem.item(Material.IRON_SWORD, "&bCombat", GuiItem.lore(
                "&7Attack: &f" + round(character.getDouble("derived.attack-power", 0.0)),
                "&7Defense: &f" + round(character.getDouble("derived.defense", 0.0)),
                "&7Magic: &f" + round(character.getDouble("derived.magic-power", 0.0)),
                "&7Resistance: &f" + round(character.getDouble("derived.resistance", 0.0)),
                "&7Evasion: &f" + round(character.getDouble("derived.evasion", 0.0))
        )));

        inventory.setItem(20, GuiItem.item(Material.AMETHYST_SHARD, "&dFragments", GuiItem.lore(
                "&7Equipped: &f" + fragmentSummary.equipped().size() + " &8/ &f" + fragmentSummary.capacity(),
                "&7Discovered: &f" + fragmentSummary.discovered().size(),
                "&7Pressure: &f" + round(fragmentSummary.totalPressure()),
                "&7Stability: &f" + round(fragmentSummary.stability()),
                "&8Fragments alter access, pressure, and consequence."
        )));

        inventory.setItem(22, GuiItem.item(Material.ECHO_SHARD, "&bIntent Slots", GuiItem.lore(
                "&7Primary: &f" + intents.displayName(intentSummary.primary()),
                "&7Slots: &f" + intentSummary.usedSlots() + " &8/ &f" + intentSummary.maxSlots(),
                "&7Pressure: &f" + round(intentSummary.pressure()),
                "&7Stability Impact: &f" + round(intentSummary.stabilityImpact()),
                "",
                "&eClick to open Intent Slots."
        )));

        Material disciplineMaterial = disciplineSummary.selected() ? Material.NETHER_STAR : Material.GRAY_DYE;
        inventory.setItem(24, GuiItem.item(disciplineMaterial, "&bDiscipline", GuiItem.lore(
                "&7Current: &f" + disciplineSummary.display(),
                "&7Family: &f" + disciplineSummary.family(),
                "&7Selected: &f" + yesNo(disciplineSummary.selected()),
                "&7Unlocked: &f" + yesNo(disciplineSummary.unlocked()),
                "&7Required Level: &f" + disciplineSummary.unlockLevel(),
                "&7Rank: &f" + disciplineProgress.rank() + " &8/ &f" + disciplineProgress.rankName(),
                "&7XP: &f" + disciplineProgress.xp() + " &8/ &f" + disciplineProgress.xpRequired(),
                "",
                "&eClick to open Discipline Codex."
        )));

        inventory.setItem(28, GuiItem.item(Material.BLAZE_POWDER, "&bAbilities", GuiItem.lore(
                "&7Discipline: &f" + abilitySummary.discipline(),
                "&7Rank: &f" + abilitySummary.rank(),
                "&7Unlocked: &f" + abilitySummary.unlocked().size(),
                "&7Locked: &f" + abilitySummary.locked().size(),
                "&7Available: &f" + abilitySummary.count()
        )));

        inventory.setItem(30, GuiItem.item(Material.HEART_OF_THE_SEA, "&bState", GuiItem.lore(
                "&7Remnant State: &f" + character.getString("remnant-state", "UNCOMMITTED"),
                "&7Profession: &f" + character.getString("profession", "UNFORMED"),
                "&7Fragment Level: &f" + round(character.getDouble("fragment-level", 1.0)),
                "&7Existence Strain: &f" + round(character.getDouble("existence-strain", 0.0))
        )));

        inventory.setItem(32, GuiItem.item(Material.RECOVERY_COMPASS, "&bOutcome Bias", GuiItem.lore(
                "&7Current: &fUnresolved",
                "&8Outcome Effects will read Fragment + Intent state.",
                "&8This is intentionally display-only for now."
        )));

        inventory.setItem(34, GuiItem.item(Material.BARRIER, "&8Mutations", GuiItem.lore(
                "&7Status: &8Locked",
                "&7Requires late-game Discipline progression.",
                "&8Visible now, usable later. Very MMO."
        )));

        inventory.setItem(45, GuiItem.item(Material.ARROW, "&7Back", GuiItem.lore("&8No parent menu yet.")));
        inventory.setItem(49, GuiItem.item(Material.PAPER, "&bRefresh", GuiItem.lore("&eClick to reload this card.")));
        inventory.setItem(53, GuiItem.item(Material.RED_STAINED_GLASS_PANE, "&cClose", GuiItem.lore("&7Close this menu.")));

        player.openInventory(inventory);
    }

    private void fillBorder(Inventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8) {
                inventory.setItem(slot, GuiItem.filler());
            }
        }
    }

    private String yesNo(boolean value) {
        return value ? "&aYes" : "&8No";
    }

    private String round(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}
