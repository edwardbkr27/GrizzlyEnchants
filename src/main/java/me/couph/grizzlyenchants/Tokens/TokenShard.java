package me.couph.grizzlyenchants.Tokens;

import me.couph.grizzlybackpacks.util.CouphUtil;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class TokenShard implements Listener {

    private GrizzlyEnchants plugin;
    private TokenManager tokenHandler;

    public TokenShard(GrizzlyEnchants plugin) {
        this.plugin = plugin;
        this.tokenHandler = plugin.tokenHandler;
    }

    public ItemStack createItem(long amount) {
        ItemStack item = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CouphUtil.color("&3&lToken Shard (&b" + amount + "&3&l)&r"));
        List<String> lore = new ArrayList<>();
        lore.add(CouphUtil.color("&7This is a token shard."));
        lore.add(" ");
        lore.add(CouphUtil.color("&3» Value: " + amount));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void addTokens(Player player, ItemStack item) {
        if (isTokenShard(item)) {
            long amount = getAmountFromItem(item);
            this.tokenHandler.addTokens(player, amount);
            player.sendMessage(CouphUtil.color("&a(!) &a&l+" + amount + " Tokens"));
        }
    }

    public boolean isTokenShard(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        for (String loreLine : meta.getLore()) {
            if (CouphUtil.colorAndStrip(loreLine).contains("This is a token shard")) return true;
        }
        return false;
    }

    public long getAmountFromItem(ItemStack item) {
        long amount=-1;
        if (isTokenShard(item)) {
            ItemMeta meta = item.getItemMeta();
            for (String loreLine : meta.getLore()) {
                if (CouphUtil.colorAndStrip(loreLine).contains("Value: ")) {
                    String numStr = CouphUtil.colorAndStrip(loreLine).split(":")[1].trim();
                    amount = Long.parseLong(numStr);
                }
            }
        }
        return amount;
    }

    @EventHandler
    public void onTokenUse(PlayerInteractEvent event) {
        try {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
                if (isTokenShard(item)) {
                    addTokens(event.getPlayer(), item);
                    removeItem(event.getPlayer());
                }
            }
        } catch (Exception e) {
            return;
        }
    }

    public void removeItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Integer stackSize = item.getAmount();
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        ItemStack newItem = new ItemStack(item.getType());
        newItem.setAmount(stackSize-1);
        newItem.setItemMeta(item.getItemMeta());
        player.getInventory().addItem(newItem);
    }
}
