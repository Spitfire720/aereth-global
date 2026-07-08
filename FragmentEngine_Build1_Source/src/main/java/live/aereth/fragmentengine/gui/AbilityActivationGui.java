package live.aereth.fragmentengine.gui;

import live.aereth.fragmentengine.service.AbilityActivationService;
import live.aereth.fragmentengine.service.AbilityService;
import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.DisciplineService;
import live.aereth.fragmentengine.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;

public class AbilityActivationGui {
    public static final String TITLE = "&b✦ Ability Activation ✦";

    private static final Map<Integer, Integer> LOADOUT_BUTTONS = Map.of(
            19, 1,
            21, 2,
            23, 3,
            25, 4
    );

    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final DisciplineService disciplines;
    private final AbilityService abilities;
    private final AbilityActivationService activation;

    public AbilityActivationGui(JavaPlugin plugin, CharacterService characters, DisciplineService disciplines,
                                AbilityService abilities, AbilityActivationService activation) {
        this.plugin = plugin;
        this.characters = characters;
        this.disciplines = disciplines;
        this.abilities = abilities;
        this.activation = activation;
    }

    public void open(Player player) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            player.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        DisciplineService.DisciplineSummary discipline = disciplines.summary(character);
        DisciplineService.DisciplineProgressSummary progress = disciplines.progress(character);
        int maxSlots = activation.maxLoadoutSlots(character);

        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.item(Material.BLAZE_POWDER, "&bAbility Activation", GuiItem.lore(
                "&7Character: &f" + character.getString("name", player.getName()),
                "&7Discipline: &f" + discipline.display(),
                "&7Rank: &f" + progress.rank() + " &8/ &f" + progress.rankName(),
                "&7Active Slots: &f" + maxSlots + " &8/ &f4",
                "&8This build records activation, cooldown, and last-use state."
        )));

        placeLoadoutSlot(inventory, 19, 1, character, maxSlots);
        placeLoadoutSlot(inventory, 21, 2, character, maxSlots);
        placeLoadoutSlot(inventory, 23, 3, character, maxSlots);
        placeLoadoutSlot(inventory, 25, 4, character, maxSlots);

        inventory.setItem(31, GuiItem.item(Material.RECOVERY_COMPASS, "&bActivation Foundation", GuiItem.lore(
                "&7Click an equipped unlocked ability to activate it.",
                "&7Cooldowns are recorded to character YAML.",
                "&7Costs are displayed but not consumed yet.",
                "&8Actual combat effects arrive later. Humanity survives."
        )));

        inventory.setItem(45, GuiItem.item(Material.ARROW, "&bBack to Ability Loadout", GuiItem.lore("&eClick to return.")));
        inventory.setItem(46, GuiItem.item(Material.PLAYER_HEAD, "&bBack to Character Card", GuiItem.lore("&eClick to return.")));
        inventory.setItem(49, GuiItem.item(Material.PAPER, "&bRefresh", GuiItem.lore("&eClick to reload activation state.")));
        inventory.setItem(53, GuiItem.item(Material.RED_STAINED_GLASS_PANE, "&cClose", GuiItem.lore("&7Close this menu.")));

        player.openInventory(inventory);
    }

    public void handleClick(Player player, int rawSlot) {
        if (rawSlot == 45) {
            player.performCommand("aereth abilityloadout");
            return;
        }

        if (rawSlot == 46) {
            player.performCommand("aereth card");
            return;
        }

        if (rawSlot == 49) {
            open(player);
            return;
        }

        if (rawSlot == 53) {
            player.closeInventory();
            return;
        }

        Integer slot = LOADOUT_BUTTONS.get(rawSlot);
        if (slot == null) {
            return;
        }

        try {
            AbilityActivationService.ActivationResult result = activation.activate(player, slot);
            player.sendMessage(prefix() + Text.color("&aActivated: &f" + result.display()
                    + " &8(slot " + result.slot() + ", cooldown " + round(result.cooldownSeconds()) + "s)"));
            open(player);
        } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
            player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            open(player);
        }
    }

    private void placeLoadoutSlot(Inventory inventory, int inventorySlot, int loadoutSlot, YamlConfiguration character, int maxSlots) {
        if (loadoutSlot > maxSlots) {
            inventory.setItem(inventorySlot, GuiItem.item(Material.BARRIER, "&8Ability Slot " + loadoutSlot + " Locked", GuiItem.lore(
                    "&7Unlock this through Discipline rank.",
                    "&7Current active slots: &f" + maxSlots
            )));
            return;
        }

        String abilityId = activation.loadoutSlot(character, loadoutSlot);
        if (abilityId.isBlank()) {
            inventory.setItem(inventorySlot, GuiItem.item(Material.GRAY_DYE, "&7Ability Slot " + loadoutSlot + ": Empty", GuiItem.lore(
                    "&7Equip an unlocked ability in the loadout first.",
                    "&eUse /aereth abilityloadout."
            )));
            return;
        }

        boolean known = activation.isKnownLoadedAbility(abilityId);
        boolean unlocked = activation.isUnlocked(character, abilityId);
        long remaining = activation.remainingCooldownSeconds(character, abilityId);

        if (!known) {
            inventory.setItem(inventorySlot, GuiItem.item(Material.BARRIER, "&cSlot " + loadoutSlot + ": Unknown Ability", GuiItem.lore(
                    "&7Id: &f" + abilityId,
                    "&cThis ability no longer exists in abilities.yml."
            )));
            return;
        }

        AbilityService.AbilityDefinition definition = abilities.definition(abilityId);
        Material material = !unlocked ? Material.GRAY_DYE : remaining > 0L ? Material.CLOCK : materialForCost(definition.costType());
        String color = !unlocked ? "&8" : remaining > 0L ? "&e" : "&a";

        inventory.setItem(inventorySlot, GuiItem.item(material, color + "Slot " + loadoutSlot + ": " + definition.display(), GuiItem.lore(
                "&7Id: &f" + definition.id(),
                "&7Required Rank: &f" + definition.unlockRank(),
                "&7Status: " + (!unlocked ? "&8Locked" : remaining > 0L ? "&eCooldown" : "&aReady"),
                "&7Cooldown Remaining: &f" + remaining + "s",
                "&7Cost: &f" + readableCost(definition),
                "&7Cooldown: &f" + round(definition.cooldownSeconds()) + "s",
                "",
                unlocked && remaining <= 0L ? "&eClick to activate." : "&8Cannot activate right now."
        )));
    }

    private String readableCost(AbilityService.AbilityDefinition definition) {
        if (definition.costType() == null || definition.costType().isBlank() || definition.costType().equalsIgnoreCase("none")) {
            return "None";
        }
        return definition.costAmount() + " " + definition.costType();
    }

    private Material materialForCost(String costType) {
        if (costType == null) {
            return Material.BLAZE_POWDER;
        }
        return switch (costType.toLowerCase()) {
            case "mana", "arcane" -> Material.LAPIS_LAZULI;
            case "health", "hp", "vitality" -> Material.RED_DYE;
            case "stamina", "energy" -> Material.SUGAR;
            case "fragment", "pressure" -> Material.AMETHYST_SHARD;
            case "focus" -> Material.ENDER_EYE;
            case "instability" -> Material.CHORUS_FRUIT;
            default -> Material.BLAZE_POWDER;
        };
    }

    private void fillBorder(Inventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8) {
                inventory.setItem(slot, GuiItem.filler());
            }
        }
    }

    private String round(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}
