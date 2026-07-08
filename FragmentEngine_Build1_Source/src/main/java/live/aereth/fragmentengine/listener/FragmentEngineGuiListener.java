package live.aereth.fragmentengine.listener;

import live.aereth.fragmentengine.gui.CharacterCardGui;
import live.aereth.fragmentengine.gui.DisciplineCodexGui;
import live.aereth.fragmentengine.gui.IntentSlotsGui;
import live.aereth.fragmentengine.service.AbilityService;
import live.aereth.fragmentengine.service.CharacterService;
import live.aereth.fragmentengine.service.DisciplineService;
import live.aereth.fragmentengine.service.FragmentService;
import live.aereth.fragmentengine.service.IntentService;
import live.aereth.fragmentengine.util.Text;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FragmentEngineGuiListener implements Listener {
    private final JavaPlugin plugin;
    private final CharacterService characters;
    private final IntentService intents;
    private final DisciplineService disciplines;
    private final CharacterCardGui characterCardGui;
    private final IntentSlotsGui intentSlotsGui;
    private final DisciplineCodexGui disciplineCodexGui;
    private final Map<UUID, Integer> selectedIntentSlots = new HashMap<>();

    public FragmentEngineGuiListener(JavaPlugin plugin, CharacterService characters, FragmentService fragments,
                                     IntentService intents, DisciplineService disciplines, AbilityService abilities) {
        this.plugin = plugin;
        this.characters = characters;
        this.intents = intents;
        this.disciplines = disciplines;
        this.characterCardGui = new CharacterCardGui(plugin, characters, fragments, intents, disciplines, abilities);
        this.intentSlotsGui = new IntentSlotsGui(plugin, characters, intents);
        this.disciplineCodexGui = new DisciplineCodexGui(plugin, characters, disciplines);
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
            handleCharacterCardClick(player, slot);
            return;
        }

        if (title.equals(ChatColor.stripColor(org.bukkit.ChatColor.translateAlternateColorCodes('&', IntentSlotsGui.TITLE)))) {
            handleIntentSlotsClick(player, slot);
            return;
        }

        if (title.equals(ChatColor.stripColor(org.bukkit.ChatColor.translateAlternateColorCodes('&', DisciplineCodexGui.TITLE)))) {
            handleDisciplineCodexClick(player, slot);
        }
    }

    private void handleCharacterCardClick(Player player, int slot) {
        if (slot == 22) {
            intentSlotsGui.open(player, selectedIntentSlots.getOrDefault(player.getUniqueId(), 0));
        } else if (slot == 24) {
            disciplineCodexGui.open(player);
        } else if (slot == 49) {
            characterCardGui.open(player);
        } else if (slot == 53) {
            selectedIntentSlots.remove(player.getUniqueId());
            player.closeInventory();
        }
    }

    private void handleIntentSlotsClick(Player player, int slot) {
        if (slot == IntentSlotsGui.BACK_SLOT) {
            characterCardGui.open(player);
            return;
        }

        if (slot == IntentSlotsGui.REFRESH_SLOT) {
            intentSlotsGui.open(player, selectedIntentSlots.getOrDefault(player.getUniqueId(), 0));
            return;
        }

        if (slot == IntentSlotsGui.CLOSE_SLOT) {
            selectedIntentSlots.remove(player.getUniqueId());
            player.closeInventory();
            return;
        }

        if (slot == IntentSlotsGui.CLEAR_SLOT) {
            clearSelectedIntent(player);
            return;
        }

        Integer intentSlot = intentSlotsGui.intentSlotFromButton(slot);
        if (intentSlot != null) {
            selectIntentSlot(player, intentSlot);
            return;
        }

        String intentId = intentSlotsGui.intentIdAt(slot);
        if (intentId != null) {
            assignSelectedIntent(player, intentId);
        }
    }

    private void handleDisciplineCodexClick(Player player, int slot) {
        if (slot == DisciplineCodexGui.BACK_SLOT) {
            characterCardGui.open(player);
            return;
        }

        if (slot == DisciplineCodexGui.REFRESH_SLOT) {
            disciplineCodexGui.open(player);
            return;
        }

        if (slot == DisciplineCodexGui.CLOSE_SLOT) {
            player.closeInventory();
            return;
        }

        if (slot == DisciplineCodexGui.CLEAR_SLOT) {
            clearDiscipline(player);
            return;
        }

        String disciplineId = disciplineCodexGui.disciplineIdAt(slot);
        if (disciplineId != null) {
            selectDiscipline(player, disciplineId);
        }
    }

    private void selectIntentSlot(Player player, int intentSlot) {
        YamlConfiguration character = characters.getActiveCharacter(player);
        if (character == null) {
            player.sendMessage(prefix() + Text.color("&cNo active character."));
            return;
        }

        IntentService.IntentSummary summary = intents.summary(character);
        if (intentSlot < 1 || intentSlot > summary.maxSlots()) {
            player.sendMessage(prefix() + Text.color("&cSlot " + intentSlot + " is locked. Current max: " + summary.maxSlots() + "."));
            selectedIntentSlots.remove(player.getUniqueId());
            intentSlotsGui.open(player);
            return;
        }

        selectedIntentSlots.put(player.getUniqueId(), intentSlot);
        player.sendMessage(prefix() + Text.color("&bSelected Intent Slot &f" + intentSlot + "&b."));
        intentSlotsGui.open(player, intentSlot);
    }

    private void assignSelectedIntent(Player player, String intentId) {
        Integer selectedSlot = selectedIntentSlots.get(player.getUniqueId());
        if (selectedSlot == null) {
            player.sendMessage(prefix() + Text.color("&cSelect an unlocked Intent slot first."));
            intentSlotsGui.open(player);
            return;
        }

        try {
            IntentService.IntentResult result = intents.setIntent(player, String.valueOf(selectedSlot), intentId);
            selectedIntentSlots.put(player.getUniqueId(), selectedSlot);
            player.sendMessage(prefix() + Text.color("&aSlot " + selectedSlot + " set to &f" + intents.displayName(result.intentId()) + "&a."));
            intentSlotsGui.open(player, selectedSlot);
        } catch (IOException ex) {
            player.sendMessage(prefix() + Text.color("&cCould not save Intent slot. Check console."));
            plugin.getLogger().warning("Could not save intent slot for " + player.getName() + ": " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            selectedIntentSlots.remove(player.getUniqueId());
            player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            intentSlotsGui.open(player);
        }
    }

    private void clearSelectedIntent(Player player) {
        Integer selectedSlot = selectedIntentSlots.get(player.getUniqueId());
        if (selectedSlot == null) {
            player.sendMessage(prefix() + Text.color("&cSelect an unlocked Intent slot first."));
            intentSlotsGui.open(player);
            return;
        }

        try {
            intents.clearIntent(player, String.valueOf(selectedSlot));
            selectedIntentSlots.put(player.getUniqueId(), selectedSlot);
            player.sendMessage(prefix() + Text.color("&aSlot " + selectedSlot + " cleared."));
            intentSlotsGui.open(player, selectedSlot);
        } catch (IOException ex) {
            player.sendMessage(prefix() + Text.color("&cCould not clear Intent slot. Check console."));
            plugin.getLogger().warning("Could not clear intent slot for " + player.getName() + ": " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            selectedIntentSlots.remove(player.getUniqueId());
            player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            intentSlotsGui.open(player);
        }
    }

    private void selectDiscipline(Player player, String disciplineId) {
        try {
            DisciplineService.DisciplineResult result = disciplines.setDiscipline(player, disciplineId);
            player.sendMessage(prefix() + Text.color("&aDiscipline selected: &f" + result.summary().display() + " &8/ &7Family: &f" + result.summary().family()));
            disciplineCodexGui.open(player);
        } catch (IOException ex) {
            player.sendMessage(prefix() + Text.color("&cCould not save Discipline. Check console."));
            plugin.getLogger().warning("Could not save discipline for " + player.getName() + ": " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            disciplineCodexGui.open(player);
        }
    }

    private void clearDiscipline(Player player) {
        try {
            DisciplineService.DisciplineResult result = disciplines.clearDiscipline(player);
            player.sendMessage(prefix() + Text.color("&aDiscipline cleared: &f" + result.summary().display()));
            disciplineCodexGui.open(player);
        } catch (IOException ex) {
            player.sendMessage(prefix() + Text.color("&cCould not clear Discipline. Check console."));
            plugin.getLogger().warning("Could not clear discipline for " + player.getName() + ": " + ex.getMessage());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            player.sendMessage(prefix() + Text.color("&c" + ex.getMessage()));
            disciplineCodexGui.open(player);
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
        return title != null && (title.equals("✦ Character Card ✦")
                || title.equals("✦ Intent Slots ✦")
                || title.equals("✦ Discipline Codex ✦"));
    }

    private String prefix() {
        return Text.color(plugin.getConfig().getString("messages.prefix", "&8[&bAereth&8]&r "));
    }
}
