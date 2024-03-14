package me.couph.grizzlyenchants.util;

import com.google.common.collect.Lists;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SimpleInventory implements Listener {
    public void setTitle(String title) {
        this.title = title;
    }

    public void setCloseable(boolean closeable) {
        this.closeable = closeable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public void setPaged(boolean paged) {
        this.paged = paged;
    }

    public void setEmptySlotsFilled(boolean emptySlotsFilled) {
        this.emptySlotsFilled = emptySlotsFilled;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public void setOpenOnClose(Inventory openOnClose) {
        this.openOnClose = openOnClose;
    }

    public void setDefaultContents(ItemStack[] defaultContents) {
        this.defaultContents = defaultContents;
    }

    public static List<SimpleInventory> getInventories() {
        return inventories;
    }

    private static List<SimpleInventory> inventories = Lists.newArrayList();

    private String title;

    private boolean closeable;

    private boolean clickable;

    private boolean paged;

    private boolean emptySlotsFilled;

    private Integer rows;

    private Inventory openOnClose;

    private ItemStack[] defaultContents;

    public String getTitle() {
        return this.title;
    }

    public boolean isCloseable() {
        return this.closeable;
    }

    public boolean isClickable() {
        return this.clickable;
    }

    public boolean isPaged() {
        return this.paged;
    }

    public boolean isEmptySlotsFilled() {
        return this.emptySlotsFilled;
    }

    public Integer getRows() {
        return this.rows;
    }

    public Inventory getOpenOnClose() {
        return this.openOnClose;
    }

    public ItemStack[] getDefaultContents() {
        return this.defaultContents;
    }

    public SimpleInventory(String title, int rows, ItemStack[] contents) {
        this.title = title;
        this.defaultContents = (contents == null) ? new ItemStack[rows * 9] : contents;
        this.rows = Integer.valueOf(rows);
        this.closeable = true;
        this.clickable = true;
        inventories.add(this);
        (new BukkitRunnable() {
            public void run() {
                SimpleInventory.this.dispose();
                return;
            }
        }).runTaskLater((Plugin) GrizzlyEnchants.getInstance(), 6000L);
    }

    public Inventory getNewInventory() {
        Inventory inventory = Bukkit.createInventory(null, this.rows.intValue() * 9, CouphUtil.color(this.title));
        inventory.setContents(this.defaultContents);
        if (this.paged) {
            int size = this.rows.intValue() * 9;
            ItemStack nextPage = CouphUtil.createItem(Material.CARPET, "&a&lNext Page", 1, 5, new String[] { "&7&m------------------------------", "&aClick here to go the the next page.", "&7&m------------------------------" });
            ItemStack previousPage = CouphUtil.createItem(Material.CARPET, "&c&lPrevious Page", 1, 14, new String[] { "&7&m------------------------------", "&cClick here to return to previous page.", "&7&m------------------------------" });
            inventory.setItem(size - 9, previousPage);
            inventory.setItem(size - 1, nextPage);
        }
        ItemStack pane = CouphUtil.createItem(Material.STAINED_GLASS_PANE, ChatColor.GOLD.toString(), 1, 7, new String[0]);
        if (this.emptySlotsFilled)
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null)
                    inventory.setItem(i, pane);
            }
        return inventory;
    }

    public void setItem(int slot, ItemStack item) {
        this.defaultContents[slot] = item;
    }

    public void show(Player player) {
        player.openInventory(getNewInventory());
    }

    public static SimpleInventory get(Inventory inventory) {
        return inventories.stream().filter(inv -> (inventory.getTitle().equals(inv.getNewInventory().getTitle()) && inv.getNewInventory().getSize() == inventory.getSize()))
                .findFirst().orElse(null);
    }

    public void dispose() {
        this.title = null;
        this.rows = null;
        this.openOnClose = null;
        this.defaultContents = null;
        inventories.remove(this);
    }

    public void onOpen() {}

    public void onClose() {
        dispose();
    }

    public void onReplace() {}
}

