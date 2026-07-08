package live.aereth.fragmentengine.service;

import live.aereth.fragmentengine.util.Text;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AbilityHotbarService {
    private static final Map<Integer, Integer> LOADOUT_TO_HOTBAR = Map.of(
            1, 4,
            2, 5,
            3, 6,
            4, 7
    );

    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final DisciplineService disciplines;
    private final AbilityService abilities;
    private final AbilityActivationService activation;
    private final AbilityTargetingService targeting;
    private final NamespacedKey slotKey;
    private final NamespacedKey abilityKey;
    private final NamespacedKey markerKey;

    public AbilityHotbarService(JavaPlugin plugin, CharacterService characters, DisciplineService disciplines,
                                AbilityService abilities, AbilityActivationService activation) {
        this.plugin = plugin;
        this.characters = characters;
        this.disciplines = disciplines;
        this.abilities = abilities;
        this.activation = activation;
        this.targeting = new AbilityTargetingService();
        this.slotKey = new NamespacedKey(plugin, "ability_bar_slot");
        this.abilityKey = new NamespacedKey(plugin, "ability_bar_id");
        this.markerKey = new NamespacedKey(plugin, "ability_bar_item");
    }

    public SyncResult sync(Player player, boolean verbose) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            if (verbose) {
                player.sendMessage(prefix() + Text.color("&cNo active character. Ability hotbar was not synced."));
            }
            return new SyncResult(0, 0, 4, 0);
        }

        DisciplineService.DisciplineSummary discipline = disciplines.summary(character);
        if (!discipline.selected()) {
            clearManagedSlots(player);
            if (verbose) {
                player.sendMessage(prefix() + Text.color("&cNo Discipline selected. Ability hotbar cleared."));
            }
            return new SyncResult(0, 0, 4, 4);
        }

        activation.resourceSnapshot(character, player);

        int maxSlots = activation.maxLoadoutSlots(character);
        int written = 0;
        int skippedBlocked = 0;
        int empty = 0;
        int locked = 0;
        List<String> blockedSlots = new ArrayList<>();

        PlayerInventory inventory = player.getInventory();
        for (int loadoutSlot = 1; loadoutSlot <= 4; loadoutSlot++) {
            int hotbarIndex = LOADOUT_TO_HOTBAR.get(loadoutSlot);
            ItemStack existing = inventory.getItem(hotbarIndex);

            if (loadoutSlot > maxSlots) {
                locked++;
                clearIfManaged(inventory, hotbarIndex, existing);
                continue;
            }

            String abilityId = activation.loadoutSlot(character, loadoutSlot);
            if (abilityId.isBlank() || !activation.isKnownLoadedAbility(abilityId)) {
                empty++;
                clearIfManaged(inventory, hotbarIndex, existing);
                continue;
            }

            if (!isAir(existing) && !isAbilityBindItem(existing)) {
                skippedBlocked++;
                blockedSlots.add(String.valueOf(hotbarIndex + 1));
                continue;
            }

            AbilityService.AbilityDefinition definition = abilities.definition(abilityId);
            inventory.setItem(hotbarIndex, abilityItem(player, character, definition, loadoutSlot));
            written++;
        }

        if (verbose) {
            AbilityResourceService.ResourceSnapshot resources = activation.resourceSnapshot(character, player);
            player.sendMessage(prefix() + Text.color("&aAbility hotbar synced. &7Written: &f" + written
                    + " &8| &7Blocked: &f" + skippedBlocked
                    + " &8| &7Empty: &f" + empty
                    + " &8| &7Locked: &f" + locked));
            player.sendMessage(prefix() + Text.color("&7Resources: &fSTA " + resources.stamina().line()
                    + " &8| &fMAN " + resources.mana().line()
                    + " &8| &fFOC " + resources.focus().line()
                    + " &8| &fINS " + resources.instability().line()));
            if (!blockedSlots.isEmpty()) {
                player.sendMessage(prefix() + Text.color("&eSkipped occupied hotbar slots: &f" + String.join(", ", blockedSlots)
                        + "&7. Move your item or clear the slot, then sneak-swap again."));
            }
        }

        return new SyncResult(written, skippedBlocked, empty, locked);
    }

    public void refreshSlot(Player player, int loadoutSlot) {
        if (loadoutSlot < 1 || loadoutSlot > 4) {
            return;
        }
        sync(player, false);
    }

    public boolean isAbilityBindItem(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.has(markerKey, PersistentDataType.STRING) && data.has(slotKey, PersistentDataType.INTEGER);
    }

    public int slotFromItem(ItemStack item) {
        if (!isAbilityBindItem(item)) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }
        Integer value = meta.getPersistentDataContainer().get(slotKey, PersistentDataType.INTEGER);
        return value == null ? 0 : value;
    }

    public String abilityIdFromItem(ItemStack item) {
        if (!isAbilityBindItem(item)) {
            return "";
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return "";
        }
        String value = meta.getPersistentDataContainer().get(abilityKey, PersistentDataType.STRING);
        return value == null ? "" : value;
    }

    public void clearManagedSlots(Player player) {
        PlayerInventory inventory = player.getInventory();
        for (int hotbarIndex : LOADOUT_TO_HOTBAR.values()) {
            clearIfManaged(inventory, hotbarIndex, inventory.getItem(hotbarIndex));
        }
    }

    private ItemStack abilityItem(Player player, YamlConfiguration character, AbilityService.AbilityDefinition definition, int loadoutSlot) {
        long remaining = activation.remainingCooldownSeconds(character, definition.id());
        boolean unlocked = activation.isUnlocked(character, definition.id());
        AbilityScalingService.ScalingResult scaling = activation.scalingPreview(character, definition, loadoutSlot);
        AbilityResourceService.CostPreview cost = activation.costPreviewScaled(character, player, definition, loadoutSlot);
        Material material = !unlocked ? Material.GRAY_DYE : remaining > 0L ? Material.CLOCK : !cost.affordable() ? Material.REDSTONE : materialFor(definition);
        String color = !unlocked ? "&8" : remaining > 0L ? "&e" : !cost.affordable() ? "&c" : "&b";
        String targetMode = targeting.targetMode(definition);

        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(Text.color(color + "✦ " + loadoutSlot + " · " + definition.display()));
        meta.setLore(List.of(
                Text.color("&7Loadout Slot: &f" + loadoutSlot),
                Text.color("&7Ability Id: &f" + definition.id()),
                Text.color("&7Role: &f" + scaling.role() + " &8(x" + round(scaling.potencyMultiplier()) + ")"),
                Text.color("&7Target: &f" + targetMode),
                Text.color("&7Cost: &f" + cost.display()),
                Text.color("&7Cost Status: " + (cost.affordable() ? "&a" : "&c") + cost.detail()),
                Text.color("&7Cooldown: &f" + round(scaling.scaledCooldownSeconds()) + "s &8(base " + round(definition.cooldownSeconds()) + ")"),
                Text.color("&7Remaining: &f" + remaining + "s"),
                Text.color(""),
                Text.color(unlocked && remaining <= 0L && cost.affordable() ? "&eRight-click to pay and activate." : "&8Cannot activate right now."),
                Text.color("&8Sneak + swap hands refreshes resources and binds.")
        ));

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(markerKey, PersistentDataType.STRING, "true");
        data.set(slotKey, PersistentDataType.INTEGER, loadoutSlot);
        data.set(abilityKey, PersistentDataType.STRING, definition.id());
        item.setItemMeta(meta);
        return item;
    }

    private void clearIfManaged(PlayerInventory inventory, int hotbarIndex, ItemStack existing) {
        if (isAbilityBindItem(existing)) {
            inventory.setItem(hotbarIndex, null);
        }
    }

    private boolean isAir(ItemStack item) {
        return item == null || item.getType().isAir();
    }

    private Material materialFor(AbilityService.AbilityDefinition definition) {
        String id = safe(definition.id());
        String cost = safe(definition.costType());

        if (id.contains("guardian") || id.contains("wall") || id.contains("stance")) {
            return Material.SHIELD;
        }
        if (id.contains("blood") || cost.equals("health") || cost.equals("hp")) {
            return Material.RED_DYE;
        }
        if (cost.equals("mana") || cost.equals("arcane")) {
            return Material.LAPIS_LAZULI;
        }
        if (cost.equals("stamina") || cost.equals("energy")) {
            return Material.SUGAR;
        }
        if (cost.equals("focus")) {
            return Material.ENDER_EYE;
        }
        if (cost.equals("fragment") || cost.equals("pressure")) {
            return Material.AMETHYST_SHARD;
        }
        if (id.contains("shadow") || id.contains("ghost")) {
            return Material.ENDER_PEARL;
        }
        if (id.contains("root") || id.contains("bind")) {
            return Material.VINE;
        }
        if (id.contains("grave") || id.contains("death")) {
            return Material.BONE;
        }
        return Material.BLAZE_POWDER;
    }

    private String round(double value) {
        return String.format(Locale.US, "%.1f", value);
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).trim().replace(" ", "_").replace("-", "_");
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }

    public record SyncResult(int written, int blocked, int empty, int locked) {
    }
}
