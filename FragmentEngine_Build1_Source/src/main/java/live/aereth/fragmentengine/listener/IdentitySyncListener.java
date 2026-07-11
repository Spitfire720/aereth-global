package live.aereth.fragmentengine.listener;

import live.aereth.fragmentengine.service.CharacterIdentityService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class IdentitySyncListener implements Listener {
    private final JavaPlugin plugin;
    private final CharacterIdentityService identity;

    public IdentitySyncListener(JavaPlugin plugin, CharacterIdentityService identity) {
        this.plugin = plugin;
        this.identity = identity;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        scheduleSync(event.getPlayer(), 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        scheduleSync(event.getPlayer(), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        identity.syncSilently(event.getPlayer());
    }

    private void scheduleSync(Player player, long delayTicks) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                identity.syncSilently(player);
            }
        }, delayTicks);
    }
}
