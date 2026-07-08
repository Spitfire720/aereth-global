package live.aereth.fragmentengine.listener;

import live.aereth.fragmentengine.gui.CharacterCardGui;
import live.aereth.fragmentengine.gui.IntentSlotsGui;
import live.aereth.fragmentengine.service.AbilityService;
import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.DisciplineService;
import live.aereth.fragmentengine.service.FragmentService;
import live.aereth.fragmentengine.service.IntentService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FragmentEngineGuiListener implements Listener {
    private final CharacterCardGui characterCardGui;
    private final IntentSlotsGui intentSlotsGui;

    public FragmentEngineGuiListener(JavaPlugin plugin, CharacterService characters, FragmentService fragments,
                                     IntentService intents, DisciplineService disciplines, AbilityService abilities) {
        this.characterCardGui = new CharacterCardGui(plugin, characters, fragments, intents, disciplines, abilities);
        this.intentSlotsGui = new IntentSlotsGui(plugin, characters, intents);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (!isAerethGui(title)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int slot = event.getRawSlot();

        if (title.equals(ChatColor.stripColor(org.bukkit.ChatColor.translateAlternateColorCodes('&', CharacterCardGui.TITLE)))) {
            if (slot == 22) {
                intentSlotsGui.open(player);
            } else if (slot == 49) {
                characterCardGui.open(player);
            } else if (slot == 53) {
                player.closeInventory();
            }
            return;
        }

        if (title.equals(ChatColor.stripColor(org.bukkit.ChatColor.translateAlternateColorCodes('&', IntentSlotsGui.TITLE)))) {
            if (slot == 45) {
                characterCardGui.open(player);
            } else if (slot == 49) {
                intentSlotsGui.open(player);
            } else if (slot == 53) {
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = ChatColor.stripColor(event.getView().getTitle());
        if (isAerethGui(title)) {
            event.setCancelled(true);
        }
    }

    private boolean isAerethGui(String title) {
        return title != null && (title.equals("✦ Character Card ✦") || title.equals("✦ Intent Slots ✦"));
    }
}
