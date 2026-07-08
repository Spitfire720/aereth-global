package live.aereth.fragmentengine.gui;

import live.aereth.fragmentengine.service.AbilityService;
import live.aereth.fragmentengine.service.AbilitySlotFrameworkService;
import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.DisciplineService;
import live.aereth.fragmentengine.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class AbilityLoadoutGui {
    public static final String TITLE = "&b✦ Ability Loadout ✦";

    private static final int[] ABILITY_SLOTS = {
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private static final Map<Integer, Integer> LOADOUT_SLOT_BUTTONS = Map.of(
            19, 1,
            21, 2,
            23, 3,
            25, 4
    );

    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final DisciplineService disciplines;
    private final AbilityService abilities;
    private final AbilitySlotFrameworkService slotFramework;
    private final Map<UUID, Integer> selectedSlots = new HashMap<>();

    public AbilityLoadoutGui(JavaPlugin plugin, CharacterService characters, DisciplineService disciplines, AbilityService abilities) {
        this.plugin = plugin;
        this.characters = characters;
        this.disciplines = disciplines;
        this.abilities = abilities;
        this.slotFramework = new AbilitySlotFrameworkService(abilities);
    }

    public void open(Player player) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            player.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        AbilitySlotFrameworkService.ValidationResult validation = slotFramework.sanitizeLoadout(character);
        if (validation.changed()) {
            try {
                saveCharacter(player, character);
                player.sendMessage(prefix() + Text.color("&eAbility slots auto-cleaned: &f" + validation.compactLine()));
            } catch (IOException ex) {
                player.sendMessage(prefix() + Text.color("&cCould not save cleaned ability slots."));
                plugin.getLogger().warning("Could not save cleaned ability loadout: " + ex.getMessage());
            }
        }

        DisciplineService.DisciplineSummary discipline = disciplines.summary(character);
        DisciplineService.DisciplineProgressSummary progress = disciplines.progress(character);
        AbilityService.AbilitySummary summary = abilities.summary(character);
        int maxSlots = slotFramework.maxSlots(character);
        int selectedSlot = selectedSlots.getOrDefault(player.getUniqueId(), 1);
        if (selectedSlot < 1 || selectedSlot > Math.max(1, maxSlots)) {
            selectedSlot = 1;
            selectedSlots.put(player.getUniqueId(), selectedSlot);
        }

        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.item(Material.BLAZE_POWDER, "&bAbility Loadout", GuiItem.lore(
                "&7Character: &f" + character.getString("name", player.getName()),
                "&7Discipline: &f" + discipline.display(),
                "&7Rank: &f" + progress.rank() + " &8/ &f" + progress.rankName(),
                "&7Unlocked Abilities: &f" + summary.unlocked().size(),
                "&7Loadout Slots: &f" + maxSlots + " &8/ &f4",
                "&7Selected Slot: &c" + selectedSlot,
                "&7Framework: &fS4C slot contract"
        )));

        inventory.setItem(16, diagnosticsItem(validation));

        placeLoadoutSlot(inventory, 19, 1, selectedSlot, maxSlots, character, summary);
        placeLoadoutSlot(inventory, 21, 2, selectedSlot, maxSlots, character, summary);
        placeLoadoutSlot(inventory, 23, 3, selectedSlot, maxSlots, character, summary);
        placeLoadoutSlot(inventory, 25, 4, selectedSlot, maxSlots, character, summary);

        if (!discipline.selected()) {
            inventory.setItem(31, GuiItem.item(Material.GRAY_DYE, "&8No Discipline Selected", GuiItem.lore(
                    "&7Select a Discipline before building a loadout.",
                    "&eUse the Discipline Codex."
            )));
        } else if (summary.unlocked().isEmpty()) {
            inventory.setItem(31, GuiItem.item(Material.GRAY_DYE, "&8No Unlocked Abilities", GuiItem.lore(
                    "&7Rank up your Discipline to unlock abilities.",
                    "&8The framework works. The character has no tools yet."
            )));
        } else {
            List<AbilityService.AbilityDefinition> definitions = definitionsFor(summary.discipline());
            for (int i = 0; i < Math.min(definitions.size(), ABILITY_SLOTS.length); i++) {
                AbilityService.AbilityDefinition definition = definitions.get(i);
                boolean unlocked = summary.unlocked().contains(definition.id());
                boolean equipped = isEquipped(character, definition.id());
                inventory.setItem(ABILITY_SLOTS[i], abilityItem(definition, unlocked, equipped, progress.rank(), selectedSlot));
            }
        }

        inventory.setItem(45, GuiItem.item(Material.ARROW, "&bBack to Ability Codex", GuiItem.lore("&eClick to return.")));
        inventory.setItem(47, GuiItem.item(Material.NETHER_STAR, "&bOpen Discipline Codex", GuiItem.lore("&eClick to manage Disciplines.")));
        inventory.setItem(48, GuiItem.item(Material.RED_DYE, "&cClear Selected Slot", GuiItem.lore(
                "&7Selected Slot: &f" + selectedSlot,
                "&eClick to clear this ability slot."
        )));
        inventory.setItem(49, GuiItem.item(Material.PAPER, "&bRefresh + Validate", GuiItem.lore(
                "&7Runs the S4C slot validator.",
                "&7Auto-cleans unknown, locked, duplicate, or broken IDs.",
                "&eClick to reload loadout state."
        )));
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

        AbilitySlotFrameworkService.ValidationResult validation = slotFramework.sanitizeLoadout(character);
        if (validation.changed()) {
            try {
                saveCharacter(player, character);
                player.sendMessage(prefix() + Text.color("&eAbility slots auto-cleaned before click: &f" + validation.compactLine()));
            } catch (IOException ex) {
                player.sendMessage(prefix() + Text.color("&cCould not save cleaned ability slots."));
                plugin.getLogger().warning("Could not save cleaned ability loadout before click: " + ex.getMessage());
                return;
            }
        }

        DisciplineService.DisciplineSummary discipline = disciplines.summary(character);
        DisciplineService.DisciplineProgressSummary progress = disciplines.progress(character);
        AbilityService.AbilitySummary summary = abilities.summary(character);
        int maxSlots = slotFramework.maxSlots(character);

        if (rawSlot == 49) {
            open(player);
            return;
        }

        if (rawSlot == 53) {
            player.closeInventory();
            return;
        }

        if (LOADOUT_SLOT_BUTTONS.containsKey(rawSlot)) {
            int loadoutSlot = LOADOUT_SLOT_BUTTONS.get(rawSlot);
            if (loadoutSlot > maxSlots) {
                player.sendMessage(prefix() + Text.color("&cLoadout Slot " + loadoutSlot + " is locked."));
                return;
            }
            selectedSlots.put(player.getUniqueId(), loadoutSlot);
            player.sendMessage(prefix() + Text.color("&aSelected Ability Loadout Slot " + loadoutSlot + "."));
            open(player);
            return;
        }

        if (rawSlot == 48) {
            int selectedSlot = selectedSlots.getOrDefault(player.getUniqueId(), 1);
            if (selectedSlot > maxSlots) {
                player.sendMessage(prefix() + Text.color("&cThat loadout slot is locked."));
                return;
            }
            try {
                slotFramework.clear(character, selectedSlot);
                saveCharacter(player, character);
                player.sendMessage(prefix() + Text.color("&aCleared Ability Loadout Slot " + selectedSlot + "."));
                open(player);
            } catch (IOException ex) {
                player.sendMessage(prefix() + Text.color("&cCould not save ability loadout."));
                plugin.getLogger().warning("Could not clear ability loadout slot: " + ex.getMessage());
            }
            return;
        }

        int definitionIndex = indexOfAbilitySlot(rawSlot);
        if (definitionIndex < 0) {
            return;
        }

        if (!discipline.selected()) {
            player.sendMessage(prefix() + Text.color("&cSelect a Discipline first."));
            return;
        }

        List<AbilityService.AbilityDefinition> definitions = definitionsFor(summary.discipline());
        if (definitionIndex >= definitions.size()) {
            return;
        }

        AbilityService.AbilityDefinition definition = definitions.get(definitionIndex);
        if (!summary.unlocked().contains(definition.id())) {
            player.sendMessage(prefix() + Text.color("&cAbility locked. Required rank: " + definition.unlockRank() + "."));
            return;
        }

        int selectedSlot = selectedSlots.getOrDefault(player.getUniqueId(), 1);
        if (selectedSlot > maxSlots) {
            player.sendMessage(prefix() + Text.color("&cThat loadout slot is locked."));
            return;
        }

        try {
            slotFramework.equip(character, selectedSlot, definition.id());
            saveCharacter(player, character);
            player.sendMessage(prefix() + Text.color("&aEquipped ability: &f" + definition.display() + " &7-> Slot " + selectedSlot + "."));
            player.sendMessage(prefix() + Text.color("&7Slot framework: &fduplicates cleared, summary rebuilt."));
            open(player);
        } catch (IOException ex) {
            player.sendMessage(prefix() + Text.color("&cCould not save ability loadout."));
            plugin.getLogger().warning("Could not save ability loadout: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            open(player);
        }
    }

    private ItemStack diagnosticsItem(AbilitySlotFrameworkService.ValidationResult validation) {
        List<String> lore = new ArrayList<>();
        lore.add("&7Status: &f" + validation.status());
        lore.add("&7Active: &f" + validation.activeCount() + " &8/ &f" + validation.maxSlots());
        lore.add("&7Cleaned: &f" + validation.cleanedCount());
        lore.add("&7Invalid: &f" + validation.invalidCount());
        lore.add("&7Duplicates: &f" + validation.duplicateCount());
        lore.add("&7Locked Clears: &f" + validation.lockedCount());
        lore.add("");
        if (validation.issues().isEmpty()) {
            lore.add("&aNo slot issues detected.");
        } else {
            lore.add("&eRecent Issues:");
            for (String issue : validation.issues().subList(0, Math.min(4, validation.issues().size()))) {
                lore.add("&8- &7" + trim(issue));
            }
        }
        lore.add("");
        lore.add("&8S4C validates slots. It does not define final abilities.");
        return GuiItem.item(validation.issues().isEmpty() ? Material.COMPARATOR : Material.ORANGE_DYE, "&dSlot Diagnostics", GuiItem.lore(lore.toArray(new String[0])));
    }

    private void saveCharacter(Player player, YamlConfiguration character) throws IOException {
        int slot = character.getInt("slot", characters.getActiveSlot(player));
        characters.storage().saveCharacter(player.getUniqueId(), slot, character);
    }

    private void placeLoadoutSlot(Inventory inventory, int inventorySlot, int loadoutSlot, int selectedSlot, int maxSlots,
                                  YamlConfiguration character, AbilityService.AbilitySummary summary) {
        boolean unlocked = loadoutSlot <= maxSlots;
        boolean selected = loadoutSlot == selectedSlot;
        String abilityId = slotFramework.slotValue(character, loadoutSlot);

        if (!unlocked) {
            inventory.setItem(inventorySlot, GuiItem.item(Material.BARRIER, "&8Loadout Slot " + loadoutSlot + " Locked", GuiItem.lore(
                    "&7Unlock through Discipline rank.",
                    "&7Current unlocked slots: &f" + maxSlots,
                    "&7Slot unlocks: &fRank 1/2/3/4",
                    "&8S4C will clear data stored in locked slots."
            )));
            return;
        }

        String marker = selected ? "&e▶ " : "";
        if (abilityId.isBlank()) {
            inventory.setItem(inventorySlot, GuiItem.item(selected ? Material.YELLOW_DYE : Material.GRAY_DYE, marker + "&7Loadout Slot " + loadoutSlot + ": Empty", GuiItem.lore(
                    "&7Status: &fAvailable",
                    "&7Contract: &fslot accepts one unlocked ability id",
                    selected ? "&eCurrently selected." : "&eClick to select this slot."
            )));
            return;
        }

        if (!abilities.allAbilityIds().contains(abilityId)) {
            inventory.setItem(inventorySlot, GuiItem.item(Material.ORANGE_DYE, marker + "&cLoadout Slot " + loadoutSlot + ": Invalid", GuiItem.lore(
                    "&7Id: &f" + abilityId,
                    "&cUnknown ability id.",
                    "&eRefresh to auto-clean this slot."
            )));
            return;
        }

        AbilityService.AbilityDefinition definition = abilities.definition(abilityId);
        boolean stillUnlocked = summary.unlocked().contains(definition.id());
        Material material = stillUnlocked ? Material.LIME_DYE : Material.ORANGE_DYE;
        inventory.setItem(inventorySlot, GuiItem.item(selected ? Material.YELLOW_DYE : material, marker + "&aLoadout Slot " + loadoutSlot + ": " + definition.display(), GuiItem.lore(
                "&7Id: &f" + definition.id(),
                "&7Status: " + (stillUnlocked ? "&aReady" : "&cInvalid / locked"),
                "&7Cost: &f" + readableCost(definition),
                "&7Cooldown: &f" + round(definition.cooldownSeconds()) + "s",
                "&7Route: &ftemporary test route",
                selected ? "&eCurrently selected." : "&eClick to select this slot."
        )));
    }

    private ItemStack abilityItem(AbilityService.AbilityDefinition definition, boolean unlocked, boolean equipped, int currentRank, int selectedSlot) {
        Material material = unlocked ? materialForCost(definition.costType()) : Material.GRAY_DYE;
        if (equipped) {
            material = Material.LIME_DYE;
        }
        String name = equipped ? "&a✓ " + definition.display() : (unlocked ? "&b" : "&8") + definition.display();
        return GuiItem.item(material, name, GuiItem.lore(
                "&7Id: &f" + definition.id(),
                "&7Required Rank: &f" + definition.unlockRank(),
                "&7Current Rank: &f" + currentRank,
                "&7Status: " + (unlocked ? "&aUnlocked" : "&8Locked"),
                "&7Equipped: &f" + yesNo(equipped),
                "&7Cost: &f" + readableCost(definition),
                "&7Cooldown: &f" + round(definition.cooldownSeconds()) + "s",
                "&7Contract: &fdefinition only, final design later",
                "",
                unlocked ? "&eClick to equip to Slot " + selectedSlot + "." : "&8Rank up to unlock.",
                "&8" + trim(definition.description())
        ));
    }

    private List<AbilityService.AbilityDefinition> definitionsFor(String disciplineId) {
        List<AbilityService.AbilityDefinition> result = new ArrayList<>();
        for (AbilityService.AbilityDefinition definition : abilities.allDefinitions()) {
            if (definition.discipline().equalsIgnoreCase(disciplineId)) {
                result.add(definition);
            }
        }
        result.sort((a, b) -> {
            int rankCompare = Integer.compare(a.unlockRank(), b.unlockRank());
            return rankCompare != 0 ? rankCompare : a.id().compareTo(b.id());
        });
        return result;
    }

    private boolean isEquipped(YamlConfiguration character, String abilityId) {
        for (int i = 1; i <= 4; i++) {
            if (abilityId.equalsIgnoreCase(slotFramework.slotValue(character, i))) {
                return true;
            }
        }
        return false;
    }

    private int indexOfAbilitySlot(int rawSlot) {
        for (int i = 0; i < ABILITY_SLOTS.length; i++) {
            if (ABILITY_SLOTS[i] == rawSlot) {
                return i;
            }
        }
        return -1;
    }

    private String readableCost(AbilityService.AbilityDefinition definition) {
        if (definition.costType() == null || definition.costType().isBlank() || definition.costType().equalsIgnoreCase("none")) {
            return "None";
        }
        return round(definition.costAmount()) + " " + definition.costType();
    }

    private Material materialForCost(String costType) {
        if (costType == null) {
            return Material.BLAZE_POWDER;
        }
        return switch (costType.toLowerCase(Locale.ROOT)) {
            case "mana", "arcane" -> Material.LAPIS_LAZULI;
            case "health", "hp", "vitality" -> Material.RED_DYE;
            case "stamina", "energy" -> Material.SUGAR;
            case "focus" -> Material.ENDER_EYE;
            case "instability" -> Material.AMETHYST_CLUSTER;
            case "fragment", "pressure" -> Material.AMETHYST_SHARD;
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

    private String yesNo(boolean value) {
        return value ? "&aYes" : "&8No";
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
