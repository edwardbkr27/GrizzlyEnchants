package me.couph.grizzlyenchants.util;

import me.couph.grizzlyenchants.GrizzlyEnchants;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuiUtil implements Listener {
    public final GrizzlyEnchants plugin;


    public GuiUtil(GrizzlyEnchants plugin) {
        this.plugin = plugin;
    }

    public static void openGui(Player player, List<ItemStack> enchants) {
        Inventory gui = Bukkit.createInventory(null, 18, "Enchantments GUI");
        int count=0;
        // add implementation for player credits
        for (ItemStack enchant : enchants) {
            gui.setItem(count, enchant);
            count++;
        }
        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equalsIgnoreCase("Enchantments GUI")) return;
        event.setCancelled(true);
        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        final Player player = (Player) event.getWhoClicked();
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent event) {
        if (event.getInventory().getTitle().equalsIgnoreCase("Enchantments GUI")) {
            event.setCancelled(true);
        }
    }
}
