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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class IntentSlotsGui {
    public static final String TITLE = "&b✦ Intent Slots ✦";

    private static final int[] INTENT_DEFINITION_SLOTS = {
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private static final Map<Integer, Integer> SLOT_BUTTONS = Map.of(
            19, 1,
            21, 2,
            23, 3,
            25, 4
    );

    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final IntentService intents;
    private final Map<UUID, Integer> selectedSlots = new HashMap<>();

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
        int selectedSlot = selectedSlots.getOrDefault(player.getUniqueId(), 1);
        if (selectedSlot < 1 || selectedSlot > summary.maxSlots()) {
            selectedSlot = 1;
            selectedSlots.put(player.getUniqueId(), selectedSlot);
        }

        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.item(Material.ECHO_SHARD, "&bIntent Profile", GuiItem.lore(
                "&7Character: &f" + character.getString("name", player.getName()),
                "&7Primary: &f" + intents.displayName(summary.primary()),
                "&7Slots Used: &f" + summary.usedSlots() + " &8/ &f" + summary.maxSlots(),
                "&7Selected Slot: &c" + selectedSlot,
                "&7Pressure: &f" + round(summary.pressure()),
                "&7Stability Impact: &f" + round(summary.stabilityImpact())
        )));

        placeIntentSlot(inventory, 19, 1, selectedSlot, summary);
        placeIntentSlot(inventory, 21, 2, selectedSlot, summary);
        placeIntentSlot(inventory, 23, 3, selectedSlot, summary);
        placeIntentSlot(inventory, 25, 4, selectedSlot, summary);

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
                    "&eClick to assign to Slot " + selectedSlot + "."
            )));
        }

        inventory.setItem(45, GuiItem.item(Material.ARROW, "&bBack to Character Card", GuiItem.lore("&eClick to return.")));
        inventory.setItem(48, GuiItem.item(Material.RED_DYE, "&cClear Selected Slot", GuiItem.lore(
                "&7Selected Slot: &f" + selectedSlot,
                "&eClick to clear this Intent slot."
        )));
        inventory.setItem(49, GuiItem.item(Material.PAPER, "&bRefresh", GuiItem.lore("&eClick to reload Intent state.")));
        inventory.setItem(53, GuiItem.item(Material.RED_STAINED_GLASS_PANE, "&cClose", GuiItem.lore("&7Close this menu.")));

        player.openInventory(inventory);
    }

    public void handleClick(Player player, int rawSlot) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            player.sendMessage(prefix() + Text.color("&cNo active character."));
            player.closeInventory();
            return;
        }

        IntentService.IntentSummary summary = intents.summary(character);

        if (rawSlot == 45) {
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

        if (SLOT_BUTTONS.containsKey(rawSlot)) {
            int intentSlot = SLOT_BUTTONS.get(rawSlot);
            if (intentSlot > summary.maxSlots()) {
                player.sendMessage(prefix() + Text.color("&cSlot " + intentSlot + " is locked."));
                return;
            }

            selectedSlots.put(player.getUniqueId(), intentSlot);
            player.sendMessage(prefix() + Text.color("&aSelected Intent Slot " + intentSlot + "."));
            open(player);
            return;
        }

        if (rawSlot == 48) {
            int selectedSlot = selectedSlots.getOrDefault(player.getUniqueId(), 1);
            try {
                intents.clearIntent(player, "slot" + selectedSlot);
                player.sendMessage(prefix() + Text.color("&aCleared Intent Slot " + selectedSlot + "."));
                open(player);
            } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
                player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            }
            return;
        }

        int definitionIndex = indexOfDefinitionSlot(rawSlot);
        if (definitionIndex < 0) {
            return;
        }

        List<String> ids = intents.allIntentIds();
        if (definitionIndex >= ids.size()) {
            return;
        }

        int selectedSlot = selectedSlots.getOrDefault(player.getUniqueId(), 1);
        String intentId = ids.get(definitionIndex);

        try {
            IntentService.IntentResult result = intents.setIntent(player, "slot" + selectedSlot, intentId);
            player.sendMessage(prefix() + Text.color("&aIntent set: &f" + result.slot() + " -> " + intents.displayName(result.intentId())));
            open(player);
        } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
            player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
        }
    }

    private int indexOfDefinitionSlot(int rawSlot) {
        for (int i = 0; i < INTENT_DEFINITION_SLOTS.length; i++) {
            if (INTENT_DEFINITION_SLOTS[i] == rawSlot) {
                return i;
            }
        }
        return -1;
    }

    private void placeIntentSlot(Inventory inventory, int inventorySlot, int intentSlot, int selectedSlot, IntentService.IntentSummary summary) {
        boolean unlocked = intentSlot <= summary.maxSlots();
        boolean selected = intentSlot == selectedSlot;
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

        String prefix = selected ? "&e▶ " : "";
        Material material = selected ? Material.YELLOW_DYE : Material.GRAY_DYE;

        if (active == null || active.isBlank()) {
            inventory.setItem(inventorySlot, GuiItem.item(material, prefix + "&7Slot " + intentSlot + ": Empty", GuiItem.lore(
                    "&7Status: &fAvailable",
                    selected ? "&eCurrently selected." : "&eClick to select this slot."
            )));
            return;
        }

        IntentService.IntentDefinition definition = intents.definition(active);
        inventory.setItem(inventorySlot, GuiItem.item(selected ? Material.YELLOW_DYE : Material.LIME_DYE, prefix + "&aSlot " + intentSlot + ": " + definition.display(), GuiItem.lore(
                "&7Id: &f" + definition.id(),
                "&7Family: &f" + definition.family(),
                "&7Pressure: &f" + round(definition.pressure()),
                "&7Stability Impact: &f" + round(definition.stabilityImpact()),
                selected ? "&eCurrently selected." : "&eClick to select this slot."
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
            case "distortion" -> Material.CHORUS_FRUIT;
            case "memory" -> Material.WRITABLE_BOOK;
            case "null" -> Material.BLACK_DYE;
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
        return String.format(Locale.US, "%.2f", value);
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}