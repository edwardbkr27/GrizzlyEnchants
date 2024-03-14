package me.couph.grizzlyenchants.util;

import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class RecipeHandler implements Listener {

    GrizzlyEnchants plugin;

    public RecipeHandler(GrizzlyEnchants plugin) {
        this.plugin = plugin;
//        NamespacedKey key = new NamespacedKey(this.plugin, "ApplyEnchant");
//        ShapelessRecipe recipe = new ShapelessRecipe(key, new ItemStack(Material.AIR));
//        recipe.addIngredient(Material.BOOK);
//        recipe.addIngredient(Material.DIAMOND_PICKAXE);
//        Bukkit.addRecipe(recipe);
    }

    public List<Enchant> getEnchants() {
        return this.plugin.getGrizzlyEnchantHandler().getGrizzlyEnchants();
    }

    @EventHandler
    public void craft(PrepareItemCraftEvent e) {
        if (e.getInventory().getContents().length < 2) {
            return;
        }
        GrizzlyPickaxe pickaxe = null;
        Enchant enchant = null;
        ItemStack pickaxeItem = null;

        List<ItemStack> ingredients = Arrays.asList(e.getInventory().getContents());
        int bookCount =0;
        for (ItemStack item : ingredients) {
            if (item.getType() == Material.BOOK) {
                bookCount++;
            }
        }
        if (bookCount>1) return;
        ItemStack enchBook = (ItemStack) ingredients.stream().filter(item -> item.getType() == Material.BOOK).findFirst().orElse(null);
        if (enchBook != null) {
            if (enchBook.getAmount() > 1) return;
            if (this.plugin.getGrizzlyEnchantHandler().getByItem(enchBook) == null) {
                return;
            }
            enchant = this.plugin.getGrizzlyEnchantHandler().getByItem(enchBook);
        } else {
            return;
        }
        int count = 0;
        for (ItemStack item : ingredients) {
            if (item.getType() == Material.DIAMOND_PICKAXE) {
                count++;
                if (item.hasItemMeta()) {
                    if (item.getItemMeta().hasLore()) {
                        if (CouphUtil.colorAndStrip(item.getItemMeta().getDisplayName()).contains("Pickaxe [")) {
                            pickaxeItem = item;
                        }
                    }
                }
            }
        }
        if (count > 1) return;
        if (pickaxeItem != null) {
            if (pickaxeItem.getItemMeta().hasDisplayName()) {
                String displayName = pickaxeItem.getItemMeta().getDisplayName();
                displayName = CouphUtil.colorAndStrip(displayName);
                int spaceIndex = displayName.indexOf(" ");
                displayName = displayName.substring(0, spaceIndex);
                if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByName(displayName) == null) {
                    return;
                } else {
                    pickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByName(displayName);
                }
            } else {
                return;
            }

            if (this.plugin.getGrizzlyEnchantHandler().canApplyEnchantPlayerless(pickaxeItem, enchant, getPickaxeLevelFromName(pickaxeItem.getItemMeta().getDisplayName()))) {
                ItemMeta meta = this.plugin.getGrizzlyEnchantHandler().applyEnchantPlayerless(pickaxeItem, enchant);
                pickaxeItem.setItemMeta(meta);
                for (int slot=0; slot < e.getInventory().getSize(); slot++) {
                    try {
                        if (e.getInventory().getItem(slot).getType() == Material.BOOK) {
                            e.getInventory().setItem(slot, new ItemStack(Material.AIR));
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

//    @EventHandler
//    public void onCraftEvent(CraftItemEvent event) {
//        boolean enchBook = false;
//        Enchant enchant = null;
//        boolean pickaxe = false;
//        ItemStack pickaxeItem = null;
//        GrizzlyPickaxe grizzlyPickaxe = null;
//        int enchSlot = 0;
//        int pickSlot = 0;
//
//        for (ItemStack item : event.getInventory().getContents()) {
//            if (item.getType() == Material.BOOK) {
//                if (this.plugin.getGrizzlyEnchantHandler().isGrizzlyEnchant(item)) {
//                    enchBook = true;
//                    enchant = this.plugin.getGrizzlyEnchantHandler().getByItem(item);
//                    enchSlot = event.getSlot();
//                }
//            }
//            if (item.getType() == Material.DIAMOND_PICKAXE) {
//                if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(item)) {
//                    pickaxe = true;
//                    grizzlyPickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(item);
//                    pickaxeItem = item;
//                    pickSlot = event.getSlot();
//                }
//            }
//        }
//        if (grizzlyPickaxe != null && enchant != null) {
//            if (this.plugin.getGrizzlyEnchantHandler().canApplyEnchant((Player) event.getWhoClicked(), pickaxeItem, enchant, grizzlyPickaxe.getLevel())) {
//                ItemMeta meta = this.plugin.getGrizzlyEnchantHandler().applyEnchant((Player) event.getWhoClicked(), pickaxeItem, enchant);
//                pickaxeItem.setItemMeta(meta);
//                event.setCancelled(true);
//                event.getInventory().setItem(enchSlot, new ItemStack(Material.AIR));
//                event.getInventory().setItem(pickSlot, new ItemStack(Material.AIR));
//                Player player = ((Player) event.getWhoClicked()).getPlayer();
//                player.getInventory().addItem(pickaxeItem);
//            }
//        }
//    }

    public Integer getPickaxeLevelFromName(String displayName) {
        try {
            displayName = CouphUtil.colorAndStrip(displayName);
            int index1 = displayName.indexOf("[");
            int index2 = displayName.indexOf("]");
            return (Integer.parseInt(displayName.substring(index1 + 1, index2)));
        } catch (Exception e) {
            return null;
        }
    }


}
