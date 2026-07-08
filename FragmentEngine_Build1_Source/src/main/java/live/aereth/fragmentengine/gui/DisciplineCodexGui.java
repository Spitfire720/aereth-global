package live.aereth.fragmentengine.gui;

import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.DisciplineService;
import live.aereth.fragmentengine.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DisciplineCodexGui {
    public static final String TITLE = "&b✦ Discipline Codex ✦";

    public static final int BACK_SLOT = 45;
    public static final int CLEAR_SLOT = 48;
    public static final int REFRESH_SLOT = 49;
    public static final int CLOSE_SLOT = 53;

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

        int level = character.getInt("progression.level", 1);
        DisciplineService.DisciplineSummary summary = disciplines.summary(character);
        DisciplineService.DisciplineProgressSummary progress = disciplines.progress(character);

        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.item(Material.NETHER_STAR, "&bDiscipline Codex", GuiItem.lore(
                "&7Character: &f" + character.getString("name", player.getName()),
                "&7Level: &f" + level,
                "&7Current: &f" + summary.display(),
                "&7Family: &f" + summary.family(),
                "&7Rank: &f" + progress.rank() + " &8/ &f" + progress.rankName(),
                "&7Progress: &f" + progress.progressPercent() + "%",
                "",
                "&8Click an unlocked Discipline to commit."
        )));

        List<String> ids = disciplines.allDisciplineIds();
        for (int i = 0; i < Math.min(ids.size(), DISCIPLINE_SLOTS.length); i++) {
            String id = ids.get(i);
            DisciplineService.DisciplineDefinition definition = disciplines.definition(id);
            boolean selected = summary.selected() && summary.id().equals(definition.id());
            boolean unlocked = level >= definition.unlockLevel();
            inventory.setItem(DISCIPLINE_SLOTS[i], disciplineItem(definition, level, selected, unlocked));
        }

        inventory.setItem(BACK_SLOT, GuiItem.item(Material.ARROW, "&bBack to Character Card", GuiItem.lore("&eClick to return.")));

        if (summary.selected()) {
            inventory.setItem(CLEAR_SLOT, GuiItem.item(Material.RED_DYE, "&cClear Current Discipline", GuiItem.lore(
                    "&7Current: &f" + summary.display(),
                    "&8Dev/admin reset path.",
                    "&eClick to return to Unformed."
            )));
        } else {
            inventory.setItem(CLEAR_SLOT, GuiItem.item(Material.GRAY_DYE, "&8Clear Discipline", GuiItem.lore(
                    "&7No Discipline selected."
            )));
        }

        inventory.setItem(REFRESH_SLOT, GuiItem.item(Material.PAPER, "&bRefresh", GuiItem.lore("&eClick to reload Discipline state.")));
        inventory.setItem(CLOSE_SLOT, GuiItem.item(Material.RED_STAINED_GLASS_PANE, "&cClose", GuiItem.lore("&7Close this menu.")));

        player.openInventory(inventory);
    }

    public String disciplineIdAt(int inventorySlot) {
        int index = -1;
        for (int i = 0; i < DISCIPLINE_SLOTS.length; i++) {
            if (DISCIPLINE_SLOTS[i] == inventorySlot) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            return null;
        }

        List<String> ids = disciplines.allDisciplineIds();
        if (index >= ids.size()) {
            return null;
        }

        return ids.get(index);
    }

    private org.bukkit.inventory.ItemStack disciplineItem(DisciplineService.DisciplineDefinition definition, int level,
                                                          boolean selected, boolean unlocked) {
        Material material = selected ? Material.NETHER_STAR : unlocked ? materialForFamily(definition.family()) : Material.BARRIER;
        String name = selected
                ? "&d" + definition.display() + " &e[Selected]"
                : unlocked ? "&b" + definition.display() : "&8" + definition.display() + " &7[Locked]";

        return GuiItem.item(material, name, GuiItem.lore(
                "&7Id: &f" + definition.id(),
                "&7Family: &f" + definition.family(),
                "&7Required Level: &f" + definition.unlockLevel(),
                "&7Current Level: &f" + level,
                "",
                "&8" + trim(definition.description()),
                "",
                selected ? "&aThis Discipline is currently active."
                        : unlocked ? "&eClick to select this Discipline."
                        : "&8Reach level " + definition.unlockLevel() + " to unlock."
        ));
    }

    private Material materialForFamily(String family) {
        return switch (family == null ? "" : family.toLowerCase()) {
            case "martial" -> Material.IRON_SWORD;
            case "defensive" -> Material.SHIELD;
            case "support" -> Material.GOLDEN_APPLE;
            case "arcane" -> Material.ENCHANTED_BOOK;
            case "summoning" -> Material.TOTEM_OF_UNDYING;
            case "construction" -> Material.ANVIL;
            case "aereth" -> Material.ECHO_SHARD;
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

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}
