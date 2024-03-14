package me.couph.grizzlyenchants.util;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SimpleInventoryListener implements Listener {
    private JavaPlugin plugin;

    public SimpleInventoryListener(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, (Plugin)plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        SimpleInventory simpleInventory = SimpleInventory.get(event.getInventory());
        if (simpleInventory == null)
            return;
        if (!simpleInventory.isClickable())
            event.setCancelled(true);
    }

    @EventHandler
    public void onClose(final InventoryCloseEvent event) {
        final SimpleInventory simpleInventory = SimpleInventory.get(event.getInventory());
        if (simpleInventory == null)
            return;
        if (!simpleInventory.isCloseable()) {
            (new BukkitRunnable() {
                public void run() {
                    event.getPlayer().openInventory(event.getInventory());
                    return;
                }
            }).runTaskLater((Plugin)this.plugin, 1L);
        } else {
            (new BukkitRunnable() {
                public void run() {
                    if (event.getPlayer().getOpenInventory().getTopInventory() == null) {
                        simpleInventory.onClose();
                    } else {
                        simpleInventory.onReplace();
                    }
                    return;
                }
            }).runTaskLater((Plugin)this.plugin, 2L);
        }
    }
}
