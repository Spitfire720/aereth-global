package live.aereth.fragmentengine.gui;

import live.aereth.fragmentengine.service.AbilityActivationService;
import live.aereth.fragmentengine.service.AbilityProgressionPolishService;
import live.aereth.fragmentengine.service.AbilityResourceService;
import live.aereth.fragmentengine.service.AbilityScalingService;
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
import java.util.Locale;
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
    private final AbilityProgressionPolishService polish;

    public AbilityActivationGui(JavaPlugin plugin, CharacterService characters, DisciplineService disciplines,
                                AbilityService abilities, AbilityActivationService activation) {
        this.plugin = plugin;
        this.characters = characters;
        this.disciplines = disciplines;
        this.abilities = abilities;
        this.activation = activation;
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
        int maxSlots = activation.maxLoadoutSlots(character);
        AbilityResourceService.ResourceSnapshot resources = activation.resourceSnapshot(character, player);
        AbilityProgressionPolishService.ProgressionView progressionView = polish.view(character);

        Inventory inventory = Bukkit.createInventory(player, 54, Text.color(TITLE));
        fillBorder(inventory);

        inventory.setItem(4, GuiItem.item(Material.BLAZE_POWDER, "&bAbility Activation", GuiItem.lore(
                "&7Character: &f" + character.getString("name", player.getName()),
                "&7Discipline: &f" + discipline.display(),
                "&7Rank: &f" + progress.rank() + " &8/ &f" + progress.rankName(),
                "&7Active Slots: &f" + maxSlots + " &8/ &f4",
                "&7Ability Reveals: &f" + progressionView.unlockedAbilities() + "&8/&f" + progressionView.totalAbilities(),
                "&8S3L polishes reveal feedback and progression visibility."
        )));

        inventory.setItem(13, GuiItem.item(Material.HEART_OF_THE_SEA, "&bAbility Resources", GuiItem.lore(
                "&7Stamina: &f" + resources.stamina().line() + " &8(+" + round(resources.stamina().regenPerSecond()) + "/s)",
                "&7Mana: &f" + resources.mana().line() + " &8(+" + round(resources.mana().regenPerSecond()) + "/s)",
                "&7Focus: &f" + resources.focus().line() + " &8(+" + round(resources.focus().regenPerSecond()) + "/s)",
                "&7Instability: &f" + resources.instability().line() + " &8(+" + round(resources.instability().regenPerSecond()) + "/s)",
                "&7Health: &f" + resources.health().line(),
                "&8Resources regenerate lazily when checked or spent."
        )));

        placeLoadoutSlot(inventory, 19, 1, player, character, maxSlots);
        placeLoadoutSlot(inventory, 21, 2, player, character, maxSlots);
        placeLoadoutSlot(inventory, 23, 3, player, character, maxSlots);
        placeLoadoutSlot(inventory, 25, 4, player, character, maxSlots);

        inventory.setItem(30, GuiItem.item(Material.WRITABLE_BOOK, "&dProgression Roadmap", GuiItem.lore(
                "&7Completion: &f" + round(progressionView.completionPercent()) + "%",
                "&7Unlocked: &f" + progressionView.unlockedAbilities() + " &8/ &f" + progressionView.totalAbilities(),
                "&7Next Reveal: &f" + trim(progressionView.nextUnlockDisplay()),
                "&7Required Rank: &f" + (progressionView.nextUnlockRank() <= 0 ? "complete" : progressionView.nextUnlockRank()),
                "&7Status: &f" + trim(progressionView.stageLine()),
                "&8" + trim(progressionView.unlockMap())
        )));

        inventory.setItem(31, GuiItem.item(Material.NETHER_STAR, "&bScaling + Resources", GuiItem.lore(
                "&7Abilities scale from level, rank, and Discipline role.",
                "&7Scaled values affect cooldown, cost, duration, radius, and potency.",
                "&7Costs are still paid before effects route.",
                "&8The spreadsheet has learned violence."
        )));

        String lastAbility = character.getString("abilities.activation.last.display", "none");
        String lastEffect = character.getString("abilities.activation.last.effect-id", "none");
        String lastResource = character.getString("abilities.activation.last.resource.type", "none");
        double lastCost = character.getDouble("abilities.activation.last.resource.amount", 0.0);
        double lastRemaining = character.getDouble("abilities.activation.last.resource.remaining", 0.0);
        String lastTargetMode = character.getString("abilities.activation.last.target.mode", "none");
        String lastTarget = character.getString("abilities.activation.last.target.description", "none");
        String lastRole = character.getString("abilities.activation.last.scaling.role", "none");
        double lastPotency = character.getDouble("abilities.activation.last.scaling.potency-multiplier", 1.0);
        String lastIdentity = character.getString("abilities.activation.last.scaling.identity-line", "none");

        inventory.setItem(35, GuiItem.item(Material.PAPER, "&bLast Activation", GuiItem.lore(
                "&7Ability: &f" + lastAbility,
                "&7Effect: &f" + lastEffect,
                "&7Resource: &f" + lastResource + " &8(-" + round(lastCost) + ", left " + round(lastRemaining) + ")",
                "&7Scaling: &f" + lastRole + " &8(x" + round(lastPotency) + ")",
                "&7Identity: &f" + trim(lastIdentity),
                "&7Target Mode: &f" + lastTargetMode,
                "&7Target: &f" + trim(lastTarget)
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
                    + " &8(slot " + result.slot()
                    + ", target " + result.targetMode()
                    + ", role " + result.scalingRole()
                    + ", cost " + round(result.resourceCost()) + " " + result.resourceType()
                    + ", cooldown " + round(result.cooldownSeconds()) + "s)"));
            player.sendMessage(prefix() + Text.color("&7Resource remaining: &f" + round(result.resourceRemaining())
                    + " &8/ &f" + round(result.resourceMax())));
            player.sendMessage(prefix() + Text.color("&7Scaling: &f" + result.scalingRole()
                    + " &8[p" + round(result.potencyMultiplier())
                    + ", d" + round(result.durationMultiplier())
                    + ", r" + round(result.radiusMultiplier()) + "]"));
            player.sendMessage(prefix() + Text.color("&7Target resolved: &f" + result.targetDescription()
                    + " &8[" + result.targetStatus() + "]"));
            open(player);
        } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
            player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            open(player);
        }
    }

    private void placeLoadoutSlot(Inventory inventory, int inventorySlot, int loadoutSlot, Player player, YamlConfiguration character, int maxSlots) {
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
        AbilityScalingService.ScalingResult scaling = activation.scalingPreview(character, definition, loadoutSlot);
        String targetMode = activation.targetMode(definition);
        AbilityActivationService.TargetPreview preview = activation.previewTarget(player, loadoutSlot);
        AbilityResourceService.CostPreview cost = activation.costPreviewScaled(character, player, definition, loadoutSlot);
        Material material = !unlocked ? Material.GRAY_DYE : remaining > 0L ? Material.CLOCK : !cost.affordable() ? Material.REDSTONE : materialForCost(definition.costType());
        String color = !unlocked ? "&8" : remaining > 0L ? "&e" : !cost.affordable() ? "&c" : "&a";

        inventory.setItem(inventorySlot, GuiItem.item(material, color + "Slot " + loadoutSlot + ": " + definition.display(), GuiItem.lore(
                "&7Id: &f" + definition.id(),
                "&7Required Rank: &f" + definition.unlockRank(),
                "&7Status: " + (!unlocked ? "&8Locked" : remaining > 0L ? "&eCooldown" : !cost.affordable() ? "&cNo Resource" : "&aReady"),
                "&7Role: &f" + scaling.role() + " &8(" + trim(scaling.identityLine()) + ")",
                "&7Power Scale: &f" + round(scaling.potencyMultiplier()) + "x &8| Duration " + round(scaling.durationMultiplier()) + "x",
                "&7Reveal State: &f" + trim(polish.abilityState(character, definition).revealLine()),
                "&7Target Mode: &f" + targetMode,
                "&7Target Preview: &f" + trim(preview.targetDescription()),
                "&7Cooldown Remaining: &f" + remaining + "s",
                "&7Cost: &f" + cost.display(),
                "&7Cost Status: " + (cost.affordable() ? "&a" : "&c") + trim(cost.detail()),
                "&7Cooldown: &f" + round(scaling.scaledCooldownSeconds()) + "s &8(base " + round(definition.cooldownSeconds()) + ")",
                "",
                unlocked && remaining <= 0L && cost.affordable() ? "&eClick to pay cost and activate." : "&8Cannot activate right now."
        )));
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

    private String trim(String text) {
        if (text == null || text.isBlank()) {
            return "none";
        }
        return text.length() <= 38 ? text : text.substring(0, 35) + "...";
    }

    private String round(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}
