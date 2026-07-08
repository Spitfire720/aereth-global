package live.aereth.fragmentengine.listener;

import live.aereth.fragmentengine.service.AbilityActivationService;
import live.aereth.fragmentengine.service.AbilityHotbarService;
import live.aereth.fragmentengine.service.AbilityService;
import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.DisciplineService;
import live.aereth.fragmentengine.util.Text;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class AbilityHotbarListener implements Listener {
    private final JavaPlugin plugin;
    private final AbilityActivationService activation;
    private final AbilityHotbarService hotbar;

    public AbilityHotbarListener(JavaPlugin plugin, CharacterService characters, DisciplineService disciplines, AbilityService abilities) {
        this.plugin = plugin;
        this.activation = new AbilityActivationService(plugin, characters, disciplines, abilities);
        this.hotbar = new AbilityHotbarService(plugin, characters, disciplines, abilities, activation);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                hotbar.sync(player, false);
            }
        }, 40L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSneakSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (!player.isSneaking()) {
            return;
        }

        event.setCancelled(true);
        hotbar.sync(player, true);
        safeSound(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.45f, 1.35f);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (!hotbar.isAbilityBindItem(item)) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        int slot = hotbar.slotFromItem(item);
        if (slot < 1 || slot > 4) {
            player.sendMessage(prefix() + Text.color("&cInvalid ability hotbar slot."));
            hotbar.sync(player, false);
            return;
        }

        try {
            AbilityActivationService.ActivationResult result = activation.activate(player, slot);
            player.sendMessage(prefix() + Text.color("&aHotbar ability activated: &f" + result.display()
                    + " &8(slot " + result.slot()
                    + ", target " + result.targetMode()
                    + ", cooldown " + round(result.cooldownSeconds()) + "s)"));
            hotbar.refreshSlot(player, slot);
        } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
            player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            hotbar.refreshSlot(player, slot);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrop(PlayerDropItemEvent event) {
        if (!hotbar.isAbilityBindItem(event.getItemDrop().getItemStack())) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        player.sendMessage(prefix() + Text.color("&7Ability bind items cannot be dropped. &8Sneak + swap hands to refresh them."));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        ItemStack current = event.getCurrentItem();
        ItemStack cursor = event.getCursor();
        if (!hotbar.isAbilityBindItem(current) && !hotbar.isAbilityBindItem(cursor)) {
            return;
        }

        event.setCancelled(true);
        player.updateInventory();
    }

    private void safeSound(Player player, Sound sound, float volume, float pitch) {
        try {
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (RuntimeException ignored) {
            // Sound feedback is helpful, not holy scripture.
        }
    }

    private String round(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}
