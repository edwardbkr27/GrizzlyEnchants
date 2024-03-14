package me.couph.grizzlyenchants.Items;

import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlyenchants.util.PlaceHoldersUtil;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnchantBlackscroll implements Listener {

    private GrizzlyTools grizzlyTools;
    private final GrizzlyEnchants plugin;
    public EnchantBlackscroll(GrizzlyEnchants plugin) {
        this.plugin = plugin;
        setGrizzlyTools(GrizzlyTools.getInstance());
    }

    public void setGrizzlyTools(GrizzlyTools grizzlyTools) {
        this.grizzlyTools = grizzlyTools;
    }

    public ItemStack createBlackscroll() {
        ItemStack blackscrollItem = new ItemStack(Material.INK_SACK);
        String displayName = CouphUtil.color("&d&ki&r &b&lEnchantment Blackscroll&r &d&ki&r");
        ItemMeta meta = blackscrollItem.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add(CouphUtil.color("&7This is an enchantment blackscroll."));
        lore.add("");
        lore.add(CouphUtil.color("&b▎ Drag and drop this item onto your pickaxe to remove a random enchantment."));
        lore.add(CouphUtil.color("&b▎ Removes a level from a random enchant on your pickaxe."));
        lore.add(CouphUtil.color("&c▎ Please ensure you have space in your inventory for the book to be returned to you!"));
        lore.add("");
        lore.add(CouphUtil.color("&7(One-time usage)."));
        meta.setLore(lore);
        meta.setDisplayName(displayName);
        blackscrollItem.setItemMeta(meta);
        return blackscrollItem;
    }

    public boolean isBlackscroll(ItemStack item) {
        if (!(item.getType() == Material.INK_SACK)) return false;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        for (String loreLine : lore) {
            if (CouphUtil.colorAndStrip(loreLine).contains("This is a")) {
                return true;
            }
        }
        return false;
    }

    public ItemMeta doRemoveEnchantment(ItemStack item, Player player) {
        List<Integer> validIndexList = new ArrayList<>();
        int indexCount = 0;
        ItemMeta meta = item.getItemMeta();
        for (String loreLine : meta.getLore()) {
            loreLine = CouphUtil.colorAndStrip(loreLine);
            if (loreLine.contains("This is a")) break;
            validIndexList.add(indexCount);
            indexCount++;
        }
        Random rand = new Random();
        int num = rand.nextInt(validIndexList.size());

        /// give book
        String enchantLine = CouphUtil.colorAndStrip(meta.getLore().get(num));
        int whitespaceIndex = enchantLine.indexOf(" ");
        String enchName = CouphUtil.colorAndStrip(meta.getLore().get(num)).substring(0, whitespaceIndex);
        if (enchantLine.contains("Dragons") || enchantLine.contains("Token") || enchantLine.contains("XP") || enchantLine.contains("Fortune")) {
            whitespaceIndex = CouphUtil.colorAndStrip(enchantLine).indexOf(" ", whitespaceIndex+1);
            enchName = CouphUtil.colorAndStrip(enchantLine).substring(0, whitespaceIndex);
        }
        Enchant enchant = this.plugin.getGrizzlyEnchantHandler().getByName(enchName);
        player.getInventory().addItem(enchant.createEnchant(Material.BOOK));

        ItemMeta newMeta = this.plugin.getGrizzlyEnchantHandler().decrementEnchant(item, num);
        return newMeta;
    }

    @EventHandler
    public void onRemoveEnchantment(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))return;
        if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
            ItemStack meantToBeBlackscroll = event.getCursor();
            ItemStack meantToBePickaxe = event.getCurrentItem();
            if (isBlackscroll(meantToBeBlackscroll)) {
                if (this.grizzlyTools.getGrizzlyPickaxeHandler().isGrizzlyPickaxe(meantToBePickaxe)) {
                    ItemMeta meta = doRemoveEnchantment(meantToBePickaxe, (Player) event.getWhoClicked());
                    if (meta == null) {
                        ((Player) event.getWhoClicked()).getPlayer().sendMessage(CouphUtil.color("&c(!) You do not have any enchantments you can remove on this item!"));
                        return;
                    }
                    meantToBePickaxe.setItemMeta(meta);
                    event.setCancelled(true);
                    int stackSize = meantToBeBlackscroll.getAmount();
                    ItemStack newItem = new ItemStack(meantToBeBlackscroll.getType());
                    newItem.setAmount(stackSize-1);
                    newItem.setItemMeta(meantToBeBlackscroll.getItemMeta());
                    event.setCursor(newItem);
                    CouphUtil.playSound(((Player) event.getWhoClicked()).getPlayer(), Sound.ENTITY_PLAYER_LEVELUP);
                } else {
                    ((Player) event.getWhoClicked()).getPlayer().sendMessage(CouphUtil.color("&c(!) You can't apply a blackscroll to this item!"));
                }
            }
        }
    }
}
