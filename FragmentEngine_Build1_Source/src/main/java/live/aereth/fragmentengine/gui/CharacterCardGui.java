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
    public static final String TITLE = "&bâœ¦ Character Card âœ¦";

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

        double fragmentPressure = fragmentSummary.totalPressure();
        double intentPressure = intentSummary.pressure();
        double totalPressure = fragmentPressure + intentPressure;
        double stability = clamp(fragmentSummary.stability() + intentSummary.stabilityImpact(), 0.0, 100.0);
        double erasurePressure = fragmentSummary.erasurePressure();
        String identityState = diagnosticState(fragmentSummary, intentSummary, totalPressure, stability, erasurePressure);

        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.playerHead(player, "&b" + character.getString("name", player.getName()), GuiItem.lore(
                "&7Owner: &f" + player.getName(),
                "&7Slot: &f" + character.getInt("slot", 0),
                "&7Profile: &8" + character.getString("profile-id", "unknown"),
                "&7Identity State: &f" + identityState
        )));

        inventory.setItem(10, GuiItem.item(Material.ENCHANTED_BOOK, "&bRace / Condition", GuiItem.lore(
                "&7Race: &f" + character.getString("race.display", character.getString("race.id", "unformed")),
                "&7Trait: &f" + character.getString("race.trait", "none"),
                "&7Condition: &fRemnant",
                "&8Race informs origin. It does not own the RPG state."
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

        inventory.setItem(14, GuiItem.item(Material.HEART_OF_THE_SEA, "&bStability / Erasure", GuiItem.lore(
                "&7Combined Stability: &f" + round(stability),
                "&7Fragment Stability: &f" + round(fragmentSummary.stability()),
                "&7Intent Impact: &f" + round(intentSummary.stabilityImpact()),
                "&7Erasure Pressure: &c" + round(erasurePressure),
                "&8Diagnostic only. No punishment system yet."
        )));

        inventory.setItem(16, GuiItem.item(Material.IRON_SWORD, "&bCombat Snapshot", GuiItem.lore(
                "&7Attack: &f" + round(character.getDouble("derived.attack-power", 0.0)),
                "&7Defense: &f" + round(character.getDouble("derived.defense", 0.0)),
                "&7Magic: &f" + round(character.getDouble("derived.magic-power", 0.0)),
                "&7Resistance: &f" + round(character.getDouble("derived.resistance", 0.0)),
                "&7Evasion: &f" + round(character.getDouble("derived.evasion", 0.0))
        )));

        inventory.setItem(20, GuiItem.item(Material.AMETHYST_SHARD, "&dFragment Layer", GuiItem.lore(
                "&7Equipped: &f" + fragmentSummary.equipped().size() + " &8/ &f" + fragmentSummary.capacity(),
                "&7Discovered: &f" + fragmentSummary.discovered().size(),
                "&7Pressure: &c" + round(fragmentPressure),
                "&7Stability: &f" + round(fragmentSummary.stability()),
                "&8Fragments alter access, consequence, and pressure.",
                "&8They are not MMOItems gear."
        )));

        inventory.setItem(22, GuiItem.item(Material.ECHO_SHARD, "&bIntent Layer", GuiItem.lore(
                "&7Primary: &f" + intents.displayName(intentSummary.primary()),
                "&7Slots: &f" + intentSummary.usedSlots() + " &8/ &f" + intentSummary.maxSlots(),
                "&7Pressure: &c" + round(intentPressure),
                "&7Stability Impact: &f" + round(intentSummary.stabilityImpact()),
                "",
                "&eClick to open Intent Slots."
        )));

        Material disciplineMaterial = disciplineSummary.selected() ? Material.NETHER_STAR : Material.GRAY_DYE;
        inventory.setItem(24, GuiItem.item(disciplineMaterial, "&bDiscipline Layer", GuiItem.lore(
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

        inventory.setItem(28, GuiItem.item(Material.BLAZE_POWDER, "&bAbility Codex", GuiItem.lore(
                "&7Discipline: &f" + abilitySummary.discipline(),
                "&7Rank: &f" + abilitySummary.rank(),
                "&7Unlocked: &f" + abilitySummary.unlocked().size(),
                "&7Locked: &f" + abilitySummary.locked().size(),
                "&7Available: &f" + abilitySummary.count(),
                "",
                "&eClick to open Ability Codex."
        )));

        inventory.setItem(30, GuiItem.item(Material.WRITABLE_BOOK, "&bAbility Loadout", GuiItem.lore(
                "&7Purpose: &fEquipped ability slots",
                "&7Framework: &fSlot validation + persistence",
                "&7Design Status: &8No final ability catalogue yet",
                "",
                "&eClick to open Ability Loadout."
        )));

        inventory.setItem(32, GuiItem.item(Material.RECOVERY_COMPASS, "&bActivation Pipeline", GuiItem.lore(
                "&7Purpose: &fCooldown/resource/target test flow",
                "&7Effects: &8Temporary placeholder routes",
                "&7PvP Damage: &cBlocked",
                "",
                "&eClick to open Ability Activation."
        )));

        inventory.setItem(34, GuiItem.item(materialForState(identityState), "&bIdentity Diagnostic", GuiItem.lore(
                "&7State: &f" + identityState,
                "&7Total Pressure: &c" + round(totalPressure),
                "&7Fragment Pressure: &f" + round(fragmentPressure),
                "&7Intent Pressure: &f" + round(intentPressure),
                "&7Combined Stability: &f" + round(stability),
                "&7Remnant State: &f" + character.getString("remnant-state", "UNCOMMITTED"),
                "&7Profession: &f" + character.getString("profession", "UNFORMED"),
                "&8This panel is a framework readout, not balance."
        )));

        inventory.setItem(38, GuiItem.item(Material.BLACK_DYE, "&8Erasure Context", GuiItem.lore(
                "&7Stored Erasure: &f" + round(character.getDouble("erasure", 0.0)),
                "&7Erasure Pressure: &c" + round(erasurePressure),
                "&7Existence Strain: &f" + round(character.getDouble("existence-strain", 0.0)),
                "&8Future world systems will read this carefully.",
                "&8No dramatic apocalypse button yet. Shame."
        )));

        inventory.setItem(40, GuiItem.item(Material.AMETHYST_CLUSTER, "&dOutcome Hooks", GuiItem.lore(
                "&7Conversion: &8placeholder",
                "&7Amplification: &8placeholder",
                "&7Redirection: &8placeholder",
                "&7Delay: &8placeholder",
                "&7Suppression: &8placeholder",
                "&7Distortion: &8placeholder",
                "&8Outcome Effects will read Fragment + Intent state."
        )));

        inventory.setItem(42, GuiItem.item(Material.BARRIER, "&8Mutations", GuiItem.lore(
                "&7Status: &8Locked",
                "&7Requires late-game Discipline progression.",
                "&8Visible now, usable later. Very MMO."
        )));

        inventory.setItem(45, GuiItem.item(Material.ARROW, "&7Back", GuiItem.lore("&8No parent menu yet.")));
        inventory.setItem(49, GuiItem.item(Material.PAPER, "&bRefresh", GuiItem.lore("&eClick to reload this card.")));
        inventory.setItem(53, GuiItem.item(Material.RED_STAINED_GLASS_PANE, "&cClose", GuiItem.lore("&7Close this menu.")));

        player.openInventory(inventory);
    }

    private String diagnosticState(FragmentService.FragmentSummary fragments, IntentService.IntentSummary intents,
                                   double totalPressure, double stability, double erasurePressure) {
        if (erasurePressure >= 75.0 || stability <= 25.0 || totalPressure >= 90.0) {
            return "Critical";
        }
        if (erasurePressure >= 45.0 || stability <= 50.0 || totalPressure >= 55.0) {
            return "Strained";
        }
        if (fragments.equipped().isEmpty() && intents.usedSlots() == 0) {
            return "Unformed";
        }
        return "Coherent";
    }

    private Material materialForState(String state) {
        return switch (state.toLowerCase(java.util.Locale.ROOT)) {
            case "critical" -> Material.RED_DYE;
            case "strained" -> Material.PURPLE_DYE;
            case "unformed" -> Material.GRAY_DYE;
            default -> Material.LIME_DYE;
        };
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
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
