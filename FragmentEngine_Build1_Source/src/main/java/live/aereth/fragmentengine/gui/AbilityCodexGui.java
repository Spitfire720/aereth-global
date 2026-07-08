package live.aereth.fragmentengine.gui;

import live.aereth.fragmentengine.service.AbilityService;
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

import java.util.ArrayList;
import java.util.List;

public class AbilityCodexGui {
    public static final String TITLE = "&b✦ Ability Codex ✦";

    private static final int[] ABILITY_SLOTS = {
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
    };

    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final DisciplineService disciplines;
    private final AbilityService abilities;

    public AbilityCodexGui(JavaPlugin plugin, CharacterService characters, DisciplineService disciplines, AbilityService abilities) {
        this.plugin = plugin;
        this.characters = characters;
        this.disciplines = disciplines;
        this.abilities = abilities;
    }

    public void open(Player player) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            player.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        DisciplineService.DisciplineSummary discipline = disciplines.summary(character);
        DisciplineService.DisciplineProgressSummary progress = disciplines.progress(character);
        AbilityService.AbilitySummary summary = abilities.summary(character);

        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.item(Material.BLAZE_POWDER, "&bAbility Codex", GuiItem.lore(
                "&7Character: &f" + character.getString("name", player.getName()),
                "&7Discipline: &f" + discipline.display(),
                "&7Rank: &f" + progress.rank() + " &8/ &f" + progress.rankName(),
                "&7Unlocked: &f" + summary.unlocked().size(),
                "&7Locked: &f" + summary.locked().size(),
                "&7Loadout Count: &f" + character.getInt("abilities.loadout.count", 0)
        )));

        inventory.setItem(10, GuiItem.item(discipline.selected() ? Material.NETHER_STAR : Material.GRAY_DYE, "&bCurrent Discipline", GuiItem.lore(
                "&7Current: &f" + discipline.display(),
                "&7Family: &f" + discipline.family(),
                "&7Selected: &f" + yesNo(discipline.selected()),
                "&7Unlocked: &f" + yesNo(discipline.unlocked()),
                "&7Required Level: &f" + discipline.unlockLevel()
        )));

        inventory.setItem(12, GuiItem.item(Material.EXPERIENCE_BOTTLE, "&bRank Progression", GuiItem.lore(
                "&7Rank: &f" + progress.rank() + " &8/ &f" + progress.maxRank(),
                "&7Name: &f" + progress.rankName(),
                "&7XP: &f" + progress.xp() + " &8/ &f" + progress.xpRequired(),
                "&7Progress: &f" + round(progress.progressPercent()) + "%",
                "&7At Cap: &f" + yesNo(progress.atCap())
        )));

        inventory.setItem(14, GuiItem.item(Material.HEART_OF_THE_SEA, "&bAbility State", GuiItem.lore(
                "&7Available: &f" + summary.available().size(),
                "&7Unlocked: &f" + summary.unlocked().size(),
                "&7Locked: &f" + summary.locked().size(),
                "&8Abilities unlock through Discipline rank."
        )));

        inventory.setItem(16, GuiItem.item(Material.BARRIER, "&8Progression Actions", GuiItem.lore(
                "&7Direct XP buttons are intentionally disabled.",
                "&7Rank XP should come from quests, combat, trials,",
                "&7or admin commands during testing.",
                "",
                "&8Use /aereth adddisciplinexp for dev testing."
        )));

        List<AbilityService.AbilityDefinition> definitions = definitionsFor(summary.discipline());
        if (!discipline.selected()) {
            inventory.setItem(31, GuiItem.item(Material.GRAY_DYE, "&8No Discipline Selected", GuiItem.lore(
                    "&7Select a Discipline first.",
                    "&eUse the Discipline Codex."
            )));
        } else if (definitions.isEmpty()) {
            inventory.setItem(31, GuiItem.item(Material.GRAY_DYE, "&8No Abilities Defined", GuiItem.lore(
                    "&7This Discipline has no abilities in abilities.yml yet.",
                    "&8The Codex is ready. The content is not. Classic."
            )));
        } else {
            for (int i = 0; i < Math.min(definitions.size(), ABILITY_SLOTS.length); i++) {
                AbilityService.AbilityDefinition definition = definitions.get(i);
                boolean unlocked = summary.unlocked().contains(definition.id());
                boolean equipped = isEquipped(character, definition.id());
                inventory.setItem(ABILITY_SLOTS[i], abilityItem(definition, unlocked, equipped, progress.rank()));
            }
        }

        inventory.setItem(45, GuiItem.item(Material.ARROW, "&bBack to Character Card", GuiItem.lore("&eClick to return.")));
        inventory.setItem(47, GuiItem.item(Material.NETHER_STAR, "&bOpen Discipline Codex", GuiItem.lore("&eClick to manage Disciplines.")));
        inventory.setItem(48, GuiItem.item(Material.CHEST, "&bOpen Ability Loadout", GuiItem.lore(
                "&7Equip unlocked abilities into active slots.",
                "&eClick to manage loadout."
        )));
        inventory.setItem(49, GuiItem.item(Material.PAPER, "&bRefresh", GuiItem.lore("&eClick to reload ability state.")));
        inventory.setItem(53, GuiItem.item(Material.RED_STAINED_GLASS_PANE, "&cClose", GuiItem.lore("&7Close this menu.")));

        player.openInventory(inventory);
    }

    public void handleClick(Player player, int rawSlot) {
        if (rawSlot == 49) {
            open(player);
        } else if (rawSlot == 53) {
            player.closeInventory();
        }
    }

    private ItemStack abilityItem(AbilityService.AbilityDefinition definition, boolean unlocked, boolean equipped, int currentRank) {
        Material material = equipped ? Material.LIME_DYE : (unlocked ? materialForCost(definition.costType()) : Material.GRAY_DYE);
        String name = equipped ? "&a✓ " + definition.display() : (unlocked ? "&a" : "&8") + definition.display();
        return GuiItem.item(material, name, GuiItem.lore(
                "&7Id: &f" + definition.id(),
                "&7Required Rank: &f" + definition.unlockRank(),
                "&7Current Rank: &f" + currentRank,
                "&7Status: " + (unlocked ? "&aUnlocked" : "&8Locked"),
                "&7Equipped: &f" + yesNo(equipped),
                "&7Cost: &f" + readableCost(definition),
                "&7Cooldown: &f" + round(definition.cooldownSeconds()) + "s",
                "",
                unlocked ? "&eOpen Ability Loadout to equip." : "&8Rank up to unlock.",
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
            if (abilityId.equalsIgnoreCase(character.getString("abilities.loadout.slots.slot" + i, ""))) {
                return true;
            }
        }
        return false;
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
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}
