package me.couph.grizzlyenchants.Tokens;

import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.util.CouphUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TokenShop implements Listener {
    private GrizzlyEnchants plugin;
    private FileConfiguration config;
    private TokenManager tokenHandler;

    private HashMap<Integer, BuyableItem> buyableItems;

    public TokenShop(GrizzlyEnchants plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.tokenHandler = plugin.tokenHandler;
        this.buyableItems = new HashMap<>();
        createItems();
    }

    public void openGui(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, CouphUtil.color("&3Token Shop"));
        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(CouphUtil.color("&b&ki&r &3Token Shop &b&ki&r"));
        filler.setItemMeta(meta);
        for (int x=0; x<9; x++) {
            gui.setItem(x, filler);
        }
        for (int x=45; x<54; x++) {
            gui.setItem(x, filler);
        }
        ItemStack nextPage = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextPage.getItemMeta();
        nextMeta.setDisplayName(CouphUtil.color("&c&lNext Page"));
        nextPage.setItemMeta(nextMeta);
        gui.setItem(53, nextPage);

        ItemStack tokenCount = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta tokenMeta = tokenCount.getItemMeta();
        long tokenAmount = 0;
        if (tokenHandler.isOnMap(player)) {
            tokenAmount = tokenHandler.getTokens(player);
        }
        tokenMeta.setDisplayName(CouphUtil.color("&3&lYour Tokens:&r &b" + tokenAmount));
        List<String> yourtokenslore = new ArrayList<>();
        yourtokenslore.add(CouphUtil.color("&7- Your Token Multiplier: &b" + getTokenBoost(player) + "x"));
        tokenMeta.setLore(yourtokenslore);
        tokenCount.setItemMeta(tokenMeta);
        gui.setItem(49, tokenCount);

        for (int ID : buyableItems.keySet()) {
            if (ID+9 <= 44) {
                gui.setItem(ID + 9, buyableItems.get(ID).createItem());
            }
        }

        player.openInventory(gui);
    }

    public Inventory OpenPage2(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, CouphUtil.color("&3Token Shop"));
        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(CouphUtil.color("&b&ki&r &3Token Shop &b&ki&r"));
        filler.setItemMeta(meta);
        for (int x=0; x<9; x++) {
            gui.setItem(x, filler);
        }
        for (int x=45; x<54; x++) {
            gui.setItem(x, filler);
        }

        ItemStack nextPage = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextPage.getItemMeta();
        nextMeta.setDisplayName(CouphUtil.color("&c&lPrevious Page"));
        nextPage.setItemMeta(nextMeta);
        gui.setItem(45, nextPage);

        ItemStack tokenCount = new ItemStack(Material.PRISMARINE_SHARD);
        ItemMeta tokenMeta = tokenCount.getItemMeta();
        long tokenAmount = 0;
        if (tokenHandler.isOnMap(player)) {
            tokenAmount = tokenHandler.getTokens(player);
        }
        tokenMeta.setDisplayName(CouphUtil.color("&3&lYour Tokens:&r &b" + tokenAmount));
        List<String> yourtokenslore = new ArrayList<>();
        yourtokenslore.add(CouphUtil.color("&7- Your Token Multiplier: &b" + getTokenBoost(player) + "x"));
        tokenMeta.setLore(yourtokenslore);
        tokenCount.setItemMeta(tokenMeta);

        for (int ID : buyableItems.keySet()) {
            if (ID+9 > 44) {
                gui.setItem((ID + 9)-36, buyableItems.get(ID).createItem());
            }
        }

        return gui;
    }


    public int getTokenBoost(Player player) {
        String rank = PlaceholderAPI.setPlaceholders(player, "%luckperms_highest_group_by_weight%");
        if (rank.equalsIgnoreCase("default")) return 1;
        if (rank.equalsIgnoreCase("hustler")) return 1;
        if (rank.equalsIgnoreCase("officer")) return 1;
        if (rank.equalsIgnoreCase("vigilante")) return 2;
        if (rank.equalsIgnoreCase("kingpin")) return 2;
        if (rank.equalsIgnoreCase("grizzly")) return 3;
        if (rank.equalsIgnoreCase("grizzlyplus")) return 3;
        if (rank.equalsIgnoreCase("warden")) return 3;
        if (rank.equalsIgnoreCase("partner")) return 3;
        if (rank.equalsIgnoreCase("helper")) return 3;
        if (rank.equalsIgnoreCase("jrmod")) return 3;
        if (rank.equalsIgnoreCase("mod")) return 3;
        if (rank.equalsIgnoreCase("srmod")) return 3;
        if (rank.equalsIgnoreCase("jradmin")) return 3;
        if (rank.equalsIgnoreCase("admin")) return 3;
        if (rank.equalsIgnoreCase("owner")) return 3;
        return 1;
    }

    public void createItems() {
        ConfigurationSection tokenshopSection = config.getConfigurationSection("tokenshop");
        if (tokenshopSection == null) return;

        for (String key : tokenshopSection.getKeys(false)) {
            ConfigurationSection currItem = tokenshopSection.getConfigurationSection(key);
            if (currItem == null) return;

            String name = currItem.getName();
            String displayName = currItem.getString("displayName");
            int price = currItem.getInt("price");
            Material material = Material.getMaterial(currItem.getString("displayItem"));
            String command = currItem.getString("command");
            int id = currItem.getInt("id");

            BuyableItem item = new BuyableItem(name, displayName, command, price, material, id, 0, null, null, 0);
            buyableItems.put(id, item);
        }
    }

    public void refreshTokenCount(Player player, Inventory gui) {
            ItemStack tokenCount = new ItemStack(Material.PRISMARINE_SHARD);
            ItemMeta tokenMeta = tokenCount.getItemMeta();
            long tokenAmount = 0;
            if (tokenHandler.isOnMap(player)) {
                tokenAmount = tokenHandler.getTokens(player);
            }
            tokenMeta.setDisplayName(CouphUtil.color("&3&lYour Tokens:&r &b" + tokenAmount));
            List<String> yourtokenslore = new ArrayList<>();
            yourtokenslore.add(CouphUtil.color("&7- Your Token Multiplier: &b" + getTokenBoost(player) + "x"));
            tokenMeta.setLore(yourtokenslore);
            tokenCount.setItemMeta(tokenMeta);
            player.getOpenInventory().setItem(49, tokenCount);
    }


    @EventHandler
    public void onItemClick(final InventoryClickEvent event) {
        try {
            if (CouphUtil.colorAndStrip(event.getInventory().getTitle()).equalsIgnoreCase("Token Shop")) {
                int id = 0;
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                ItemStack clickedItem = event.getCurrentItem();
                if (!(event.getRawSlot() < 54)) return;
                if (event.getRawSlot()==49) return;
                if (event.getRawSlot() == 53) {
                    if (clickedItem.getType() == Material.ARROW) {
                        player.getOpenInventory().close();
                        player.openInventory(OpenPage2(player));
                    }
                }
                if (event.getRawSlot() == 45) {
                    if (clickedItem.getType() == Material.ARROW) {
                        player.getOpenInventory().close();
                        openGui(player);
                    }
                }
                ItemMeta clickedMeta = clickedItem.getItemMeta();
                if (clickedMeta.hasLore()) {
                    for (String loreLine : clickedMeta.getLore()) {
                        loreLine = CouphUtil.colorAndStrip(loreLine);
                        if (loreLine.contains("ID:")) {
                            String numString = loreLine.split(":")[1].trim();
                            id = Integer.parseInt(numString);
                        }
                    }
                    BuyableItem item = buyableItems.get(id);
                    int price = item.getPrice();
                    if (tokenHandler.canAfford(player, price)) {
                        tokenHandler.removeTokens(player, price);
                        player.sendMessage(CouphUtil.color("&c(!) &c&l-" + price + " Tokens"));
                        String cmd = item.getCommand();
                        cmd = cmd.replace("{player}", player.getName());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                        refreshTokenCount(player, event.getInventory());
                    } else {
                        player.sendMessage(CouphUtil.color("&c(!) You can't afford this!"));
                    }
                }

            }
        } catch (Exception e) {
            return;
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (CouphUtil.colorAndStrip(event.getInventory().getTitle()).equalsIgnoreCase("Token Shop")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if (CouphUtil.colorAndStrip(event.getSource().getTitle()).equalsIgnoreCase("Token Shop")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!tokenHandler.isOnMap(event.getPlayer())) {
            tokenHandler.addToMap(event.getPlayer());
        }
    }

}
