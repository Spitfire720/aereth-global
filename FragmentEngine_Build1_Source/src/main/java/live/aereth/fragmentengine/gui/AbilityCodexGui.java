package live.aereth.fragmentengine.gui;

import live.aereth.fragmentengine.service.AbilityProgressionPolishService;
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
    private final AbilityProgressionPolishService polish;

    public AbilityCodexGui(JavaPlugin plugin, CharacterService characters, DisciplineService disciplines, AbilityService abilities) {
        this.plugin = plugin;
        this.characters = characters;
        this.disciplines = disciplines;
        this.abilities = abilities;
        this.polish = new AbilityProgressionPolishService(abilities);
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
        AbilityProgressionPolishService.ProgressionView view = polish.view(character);

        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.item(Material.BLAZE_POWDER, "&bAbility Codex", GuiItem.lore(
                "&7Character: &f" + character.getString("name", player.getName()),
                "&7Discipline: &f" + discipline.display(),
                "&7Rank: &f" + progress.rank() + " &8/ &f" + progress.rankName(),
                "&7Unlocked: &f" + view.unlockedAbilities() + " &8/ &f" + view.totalAbilities(),
                "&7Completion: &f" + round(view.completionPercent()) + "%",
                "&7Loadout Count: &f" + character.getInt("abilities.loadout.count", 0),
                "&8S3L adds reveal progress, roadmap, and cleaner unlock feedback."
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
                "&7At Cap: &f" + yesNo(progress.atCap()),
                "&7Ability Path: &f" + view.unlockedAbilities() + "&8/&f" + view.totalAbilities() + " revealed"
        )));

        inventory.setItem(14, GuiItem.item(Material.HEART_OF_THE_SEA, "&bAbility Reveal State", GuiItem.lore(
                "&7Available: &f" + summary.available().size(),
                "&7Unlocked: &f" + view.unlockedAbilities(),
                "&7Locked: &f" + view.lockedAbilities(),
                "&7Next: &f" + trim(view.nextLine(), 42),
                "&8Abilities unlock through Discipline rank. Apparently progress matters."
        )));

        inventory.setItem(16, GuiItem.item(nextMaterial(view), "&dNext Ability Reveal", GuiItem.lore(
                "&7Next Ability: &f" + trim(view.nextUnlockDisplay(), 34),
                "&7Required Rank: &f" + (view.nextUnlockRank() <= 0 ? "complete" : view.nextUnlockRank()),
                "&7Ranks Away: &f" + view.ranksAway(),
                "&7Status: &f" + trim(view.stageLine(), 42),
                "",
                "&7Roadmap: &f" + trim(view.unlockMap(), 44)
        )));

        List<AbilityService.AbilityDefinition> definitions = polish.definitionsFor(summary.discipline());
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
                boolean equipped = isEquipped(character, definition.id());
                AbilityProgressionPolishService.AbilityState state = polish.abilityState(character, definition);
                inventory.setItem(ABILITY_SLOTS[i], abilityItem(definition, state, equipped));
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

    private ItemStack abilityItem(AbilityService.AbilityDefinition definition, AbilityProgressionPolishService.AbilityState state, boolean equipped) {
        Material material = equipped ? Material.LIME_DYE : (state.unlocked() ? materialForCost(definition.costType()) : state.ranksAway() == 1 ? Material.YELLOW_DYE : Material.GRAY_DYE);
        String name = equipped ? "&a✓ " + definition.display() : (state.unlocked() ? "&a" : state.ranksAway() == 1 ? "&e" : "&8") + definition.display();
        return GuiItem.item(material, name, GuiItem.lore(
                "&7Id: &f" + definition.id(),
                "&7Required Rank: &f" + definition.unlockRank(),
                "&7Current Rank: &f" + state.currentRank(),
                "&7Status: " + state.style(),
                "&7Reveal: &f" + trim(state.revealLine(), 40),
                "&7Milestone: &f" + state.milestone(),
                "&7Equipped: &f" + yesNo(equipped),
                "&7Cost: &f" + readableCost(definition),
                "&7Cooldown: &f" + round(definition.cooldownSeconds()) + "s",
                "",
                state.unlocked() ? "&eOpen Ability Loadout to equip." : "&8Rank up to unlock.",
                "&8" + trim(definition.description(), 44)
        ));
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

    private Material nextMaterial(AbilityProgressionPolishService.ProgressionView view) {
        if (!view.selected()) {
            return Material.GRAY_DYE;
        }
        if (view.nextUnlockRank() <= 0) {
            return Material.NETHER_STAR;
        }
        return view.ranksAway() <= 1 ? Material.AMETHYST_SHARD : Material.WRITABLE_BOOK;
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

    private String trim(String text, int max) {
        if (text == null || text.isBlank()) {
            return "none";
        }
        return text.length() <= max ? text : text.substring(0, Math.max(0, max - 3)) + "...";
    }

    private String round(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}
