package live.aereth.fragmentengine.gui;

import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.IntentService;
import live.aereth.fragmentengine.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public class IntentSlotsGui {
    public static final String TITLE = "&b✦ Intent Slots ✦";

    private static final int[] INTENT_DEFINITION_SLOTS = {
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final IntentService intents;

    public IntentSlotsGui(JavaPlugin plugin, CharacterService characters, IntentService intents) {
        this.plugin = plugin;
        this.characters = characters;
        this.intents = intents;
    }

    public void open(Player player) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            player.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        IntentService.IntentSummary summary = intents.summary(character);
        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.item(Material.ECHO_SHARD, "&bIntent Profile", GuiItem.lore(
                "&7Character: &f" + character.getString("name", player.getName()),
                "&7Primary: &f" + intents.displayName(summary.primary()),
                "&7Slots Used: &f" + summary.usedSlots() + " &8/ &f" + summary.maxSlots(),
                "&7Pressure: &f" + round(summary.pressure()),
                "&7Stability Impact: &f" + round(summary.stabilityImpact())
        )));

        placeIntentSlot(inventory, 19, 1, summary);
        placeIntentSlot(inventory, 21, 2, summary);
        placeIntentSlot(inventory, 23, 3, summary);
        placeIntentSlot(inventory, 25, 4, summary);

        List<String> ids = intents.allIntentIds();
        for (int i = 0; i < Math.min(ids.size(), INTENT_DEFINITION_SLOTS.length); i++) {
            String id = ids.get(i);
            IntentService.IntentDefinition definition = intents.definition(id);
            Material material = materialForIntent(definition.id());
            inventory.setItem(INTENT_DEFINITION_SLOTS[i], GuiItem.item(material, "&b" + definition.display(), GuiItem.lore(
                    "&7Id: &f" + definition.id(),
                    "&7Family: &f" + definition.family(),
                    "&7Pressure: &f" + round(definition.pressure()),
                    "&7Stability Impact: &f" + round(definition.stabilityImpact()),
                    "",
                    "&8" + trim(definition.description()),
                    "",
                    "&8View-only in Build S2A."
            )));
        }

        inventory.setItem(45, GuiItem.item(Material.ARROW, "&bBack to Character Card", GuiItem.lore("&eClick to return.")));
        inventory.setItem(49, GuiItem.item(Material.PAPER, "&bRefresh", GuiItem.lore("&eClick to reload Intent state.")));
        inventory.setItem(53, GuiItem.item(Material.RED_STAINED_GLASS_PANE, "&cClose", GuiItem.lore("&7Close this menu.")));

        player.openInventory(inventory);
    }

    private void placeIntentSlot(Inventory inventory, int inventorySlot, int intentSlot, IntentService.IntentSummary summary) {
        boolean unlocked = intentSlot <= summary.maxSlots();
        String key = "slot" + intentSlot;
        Map<String, String> slots = summary.slots();
        String active = slots.get(key);

        if (!unlocked) {
            inventory.setItem(inventorySlot, GuiItem.item(Material.BARRIER, "&8Slot " + intentSlot + " Locked", GuiItem.lore(
                    "&7Unlock this through progression.",
                    "&7Current unlocked slots: &f" + summary.maxSlots()
            )));
            return;
        }

        if (active == null || active.isBlank()) {
            inventory.setItem(inventorySlot, GuiItem.item(Material.GRAY_DYE, "&7Slot " + intentSlot + ": Empty", GuiItem.lore(
                    "&7Status: &fAvailable",
                    "&8Intent assignment arrives in Build S2B."
            )));
            return;
        }

        IntentService.IntentDefinition definition = intents.definition(active);
        inventory.setItem(inventorySlot, GuiItem.item(Material.LIME_DYE, "&aSlot " + intentSlot + ": " + definition.display(), GuiItem.lore(
                "&7Id: &f" + definition.id(),
                "&7Family: &f" + definition.family(),
                "&7Pressure: &f" + round(definition.pressure()),
                "&7Stability Impact: &f" + round(definition.stabilityImpact())
        )));
    }

    private Material materialForIntent(String id) {
        return switch (id) {
            case "anchor" -> Material.ANVIL;
            case "hunger" -> Material.ROTTEN_FLESH;
            case "mercy" -> Material.WHITE_DYE;
            case "defiance" -> Material.IRON_AXE;
            case "ruin" -> Material.CRACKED_STONE_BRICKS;
            case "clarity" -> Material.SPYGLASS;
            case "echo" -> Material.ECHO_SHARD;
            case "dominion" -> Material.GOLDEN_HELMET;
            case "veil" -> Material.ENDER_PEARL;
            case "fracture" -> Material.AMETHYST_CLUSTER;
            default -> Material.KNOWLEDGE_BOOK;
        };
    }

    private void fillBorder(Inventory inventory) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            if (slot < 9 || slot >= 45 || slot % 9 == 0 || slot % 9 == 8) {
                inventory.setItem(slot, GuiItem.filler());
            }
        }
    }

    private String trim(String text) {
        if (text == null || text.isBlank()) {
            return "No description written yet.";
        }
        return text.length() <= 44 ? text : text.substring(0, 41) + "...";
    }

    private String round(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}
