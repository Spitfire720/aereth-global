package live.aereth.fragmentengine.gui;

import live.aereth.fragmentengine.util.Text;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public final class GuiItem {
    private GuiItem() {
    }

    public static ItemStack item(Material material, String name, List<String> lore) {
        return item(material, name, lore, 1);
    }

    public static ItemStack item(Material material, String name, List<String> lore, int amount) {
        ItemStack stack = new ItemStack(material, clampAmount(amount));
        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Text.color(name));
            meta.setLore(colorLore(lore));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ARMOR_TRIM);
            stack.setItemMeta(meta);
        }
        return stack;
    }

    public static ItemStack playerHead(OfflinePlayer player, String name, List<String> lore) {
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(player);
            skullMeta.setDisplayName(Text.color(name));
            skullMeta.setLore(colorLore(lore));
            stack.setItemMeta(skullMeta);
        }
        return stack;
    }

    public static ItemStack filler() {
        return item(Material.BLACK_STAINED_GLASS_PANE, "&8", List.of());
    }

    public static List<String> lore(String... lines) {
        return List.of(lines);
    }

    private static List<String> colorLore(List<String> lore) {
        List<String> colored = new ArrayList<>();
        if (lore == null) {
            return colored;
        }
        for (String line : lore) {
            colored.add(Text.color(line));
        }
        return colored;
    }

    private static int clampAmount(int amount) {
        return Math.max(1, Math.min(64, amount));
    }
}
