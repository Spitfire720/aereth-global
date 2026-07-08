package live.aereth.fragmentengine.gui;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DisciplineCodexGui {
    public static final String TITLE = "&b✦ Discipline Codex ✦";

    private static final int[] DISCIPLINE_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final DisciplineService disciplines;

    public DisciplineCodexGui(JavaPlugin plugin, CharacterService characters, DisciplineService disciplines) {
        this.plugin = plugin;
        this.characters = characters;
        this.disciplines = disciplines;
    }

    public void open(Player player) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            player.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        DisciplineService.DisciplineSummary current = disciplines.summary(character);
        DisciplineService.DisciplineProgressSummary progress = disciplines.progress(character);
        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.item(Material.NETHER_STAR, "&bDiscipline Codex", GuiItem.lore(
                "&7Character: &f" + character.getString("name", player.getName()),
                "&7Current: &f" + current.display(),
                "&7Rank: &f" + progress.rank() + " &8/ &f" + progress.rankName(),
                "&7Level: &f" + character.getInt("progression.level", 1),
                "&7Required Level: &f" + current.unlockLevel()
        )));

        for (Map.Entry<Integer, String> entry : disciplineSlots().entrySet()) {
            String id = entry.getValue();
            DisciplineService.DisciplineDefinition definition = disciplines.definition(id);
            boolean selected = current.id().equals(definition.id()) && current.selected();
            boolean unlocked = character.getInt("progression.level", 1) >= definition.unlockLevel();
            inventory.setItem(entry.getKey(), disciplineItem(definition, selected, unlocked, character.getInt("progression.level", 1)));
        }

        inventory.setItem(45, GuiItem.item(Material.ARROW, "&bBack to Character Card", GuiItem.lore("&eClick to return.")));
        inventory.setItem(47, GuiItem.item(Material.BLAZE_POWDER, "&bOpen Ability Codex", GuiItem.lore("&eClick to view Discipline abilities.")));
        inventory.setItem(49, GuiItem.item(Material.PAPER, "&bRefresh", GuiItem.lore("&eClick to reload Discipline state.")));
        inventory.setItem(51, GuiItem.item(Material.GRAY_DYE, "&8Clear Discipline", GuiItem.lore("&7Clears the current Discipline.", "&8Useful during testing. Dangerous later.")));
        inventory.setItem(53, GuiItem.item(Material.RED_STAINED_GLASS_PANE, "&cClose", GuiItem.lore("&7Close this menu.")));

        player.openInventory(inventory);
    }

    public void handleClick(Player player, int rawSlot) {
        if (rawSlot == 49) {
            open(player);
            return;
        }
        if (rawSlot == 53) {
            player.closeInventory();
            return;
        }
        if (rawSlot == 51) {
            try {
                DisciplineService.DisciplineResult result = disciplines.clearDiscipline(player);
                player.sendMessage(prefix() + Text.color("&aDiscipline cleared: &f" + result.summary().display()));
            } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
                player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            }
            open(player);
            return;
        }

        String disciplineId = disciplineSlots().get(rawSlot);
        if (disciplineId == null) {
            return;
        }

        try {
            DisciplineService.DisciplineResult result = disciplines.setDiscipline(player, disciplineId);
            player.sendMessage(prefix() + Text.color("&aDiscipline set: &f" + result.summary().display()));
        } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
            player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
        }
        open(player);
    }

    private ItemStack disciplineItem(DisciplineService.DisciplineDefinition definition, boolean selected, boolean unlocked, int currentLevel) {
        Material material;
        String name;
        if (selected) {
            material = Material.NETHER_STAR;
            name = "&a" + definition.display();
        } else if (unlocked) {
            material = materialForFamily(definition.family());
            name = "&b" + definition.display();
        } else {
            material = Material.GRAY_DYE;
            name = "&8" + definition.display();
        }

        return GuiItem.item(material, name, GuiItem.lore(
                "&7Id: &f" + definition.id(),
                "&7Family: &f" + definition.family(),
                "&7Required Level: &f" + definition.unlockLevel(),
                "&7Current Level: &f" + currentLevel,
                "&7Status: " + (selected ? "&aSelected" : unlocked ? "&bUnlocked" : "&8Locked"),
                "",
                "&8" + trim(definition.description()),
                "",
                unlocked ? "&eClick to select." : "&8Locked by level."
        ));
    }

    private Map<Integer, String> disciplineSlots() {
        List<String> ids = disciplines.allDisciplineIds();
        Map<Integer, String> result = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(ids.size(), DISCIPLINE_SLOTS.length); i++) {
            result.put(DISCIPLINE_SLOTS[i], ids.get(i));
        }
        return result;
    }

    private Material materialForFamily(String family) {
        if (family == null) {
            return Material.BOOK;
        }
        return switch (family.toLowerCase()) {
            case "martial" -> Material.IRON_SWORD;
            case "arcane" -> Material.ENCHANTED_BOOK;
            case "defensive" -> Material.SHIELD;
            case "support" -> Material.GOLDEN_APPLE;
            case "summoning" -> Material.ENDER_EYE;
            case "construction" -> Material.SMITHING_TABLE;
            case "aereth" -> Material.ECHO_SHARD;
            default -> Material.BOOK;
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

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}
