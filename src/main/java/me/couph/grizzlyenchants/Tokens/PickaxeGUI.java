package me.couph.grizzlyenchants.Tokens;

import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.event.EventPriority.HIGH;

public class PickaxeGUI implements Listener {

    private GrizzlyEnchants plugin;
    private TokenManager tokenHandler;
    private HashMap<Integer, BuyableItem> buyableItems;
    private FileConfiguration config;
    private List<Integer> slots;

    public PickaxeGUI(GrizzlyEnchants plugin) {
        this.plugin = plugin;
        this.tokenHandler = plugin.tokenHandler;
        this.buyableItems = new HashMap<>();
        this.config = plugin.getConfig();
        createItems();
        addSlots();
    }

    // slots to use: 10,12,14,16 : 19, 21, 23, 25 : 28, 30, 32, 34 : 37, 39, 41, 43
    // extra: 38,42

    public void addSlots() {
        this.slots = new ArrayList<>();
        this.slots.add(10);
        this.slots.add(12);
        this.slots.add(14);
        this.slots.add(16);
        this.slots.add(19);
        this.slots.add(21);
        this.slots.add(23);
        this.slots.add(25);
        this.slots.add(28);
        this.slots.add(30);
        this.slots.add(32);
        this.slots.add(34);
        this.slots.add(37);
        this.slots.add(39);
//        this.slots.add(41);
//        this.slots.add(43);

    }


    public void openGUI(Player player, ItemStack item) {
        Inventory gui = Bukkit.createInventory(null, 54, CouphUtil.color("&bEnchant Menu &7» &3Tier " + getLevelByName(item)));
        List<ItemStack> enchantBooks = new ArrayList<>();

        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 3);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(CouphUtil.color("&b&ki&r &3Enchanter &b&ki&r"));
        filler.setItemMeta(meta);
        for (int x=0; x<54; x++) {
            if (!(slots.contains(x))) {
                gui.setItem(x, filler);
            }
        }
        for (int ID : buyableItems.keySet()) {
            gui.setItem(slots.get(ID-1), buyableItems.get(ID).createEnchantItem());
        }

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
        //gui.setItem(31, tokenCount);
        player.openInventory(gui);
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
       // player.getOpenInventory().setItem(31, tokenCount);
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
        ConfigurationSection tokenshopSection = config.getConfigurationSection("pickaxegui");
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
            int level = currItem.getInt("level");
            String color = currItem.getString("color");
            String description = currItem.getString("description");
            int maxlevel = currItem.getInt("maxLevel");

            BuyableItem item = new BuyableItem(name, displayName, command, price, material, id, level, color, description, maxlevel);
            buyableItems.put(id, item);
        }
    }

    @EventHandler
    public void onItemClick(final InventoryClickEvent event) {
        try {
            if (CouphUtil.colorAndStrip(event.getInventory().getTitle()).contains("Enchant Menu")) {
                if (event.getClick().equals(ClickType.DROP)) return;
                int id = 0;
                event.setCancelled(true);
                Player player = (Player) event.getWhoClicked();
                ItemStack clickedItem = event.getCurrentItem();
                if (!(event.getRawSlot() < 44)) return;
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
                    long price = item.getPrice();
                    if (tokenHandler.canAfford(player, price)) {
                        GrizzlyPickaxe pickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(player.getInventory().getItemInMainHand());
                        if (pickaxe != null) {
                            Enchant enchant = this.plugin.getGrizzlyEnchantHandler().getByName(item.getCommand());
                            if (enchant != null) {
                                if (this.plugin.getGrizzlyEnchantHandler().canApplyEnchant(player, player.getInventory().getItemInMainHand(), enchant, getLevelByName(player.getInventory().getItemInMainHand()))) {
                                    player.getInventory().getItemInMainHand().setItemMeta(this.plugin.getGrizzlyEnchantHandler().applyEnchant(player, player.getInventory().getItemInMainHand(), enchant));
                                    tokenHandler.removeTokens(player, price);
                                    player.sendMessage(CouphUtil.color("&c(!) &c&l-" + price + " Tokens"));
                                    CouphUtil.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 0.5f);
                                }
                            }
                        } else {
                            player.sendMessage(CouphUtil.color("&c(!) You need to be holding your pickaxe!"));
                        }
                        refreshTokenCount(player, event.getInventory());
                    } else {
                        player.sendMessage(CouphUtil.color("&c(!) You can't afford this!"));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @EventHandler(priority = HIGH)
    public void ApplyMax(InventoryClickEvent e) {
        try {
            Player player = (Player)e.getWhoClicked();
            if (!(e.getClick().equals(ClickType.DROP))) return;
            if (CouphUtil.colorAndStrip(player.getOpenInventory().getTitle()).contains("Enchant Menu")) {
                e.setCancelled(true);
                ItemStack clickedItem = e.getCurrentItem();
                ItemMeta clickedMeta = clickedItem.getItemMeta();
                int id = 0;
                if (clickedMeta.hasLore()) {
                    for (String loreLine : clickedMeta.getLore()) {
                        loreLine = CouphUtil.colorAndStrip(loreLine);
                        if (loreLine.contains("ID:")) {
                            String numString = loreLine.split(":")[1].trim();
                            id = Integer.parseInt(numString);
                        }
                    }
                    BuyableItem item = buyableItems.get(id);
                    long singlePrice = item.getPrice();
                    long playerTokenCount = tokenHandler.getTokens(player);
                    long applyAmount = (playerTokenCount / singlePrice);
                    long price = applyAmount * singlePrice;
                    GrizzlyPickaxe pickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(player.getInventory().getItemInMainHand());
                    if (pickaxe != null) {
                        Enchant enchant = this.plugin.getGrizzlyEnchantHandler().getByName(item.getCommand());
                        if (enchant != null) {
                            for (int x = 0; x < applyAmount; x++) {
                                if (!(tokenHandler.canAfford(player, singlePrice))) return;
                                if (this.plugin.getGrizzlyEnchantHandler().canApplyEnchant(player, player.getInventory().getItemInMainHand(), enchant, getLevelByName(player.getInventory().getItemInMainHand()))) {
                                    player.getInventory().getItemInMainHand().setItemMeta(this.plugin.getGrizzlyEnchantHandler().applyEnchant(player, player.getInventory().getItemInMainHand(), enchant));
                                    tokenHandler.removeTokens(player, singlePrice);
                                } else {
                                    break;
                                }
                            }
                        }
                    } else {
                        player.sendMessage(CouphUtil.color("&c(!) You need to be holding a pickaxe to do this!"));
                    }
                }
            }
        } catch (Exception ex) {
            return;
        }
    }

    public Integer getLevelByName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String displayName = CouphUtil.colorAndStrip(meta.getDisplayName());
        String numberString = displayName.replaceAll("[^0-9]", "");
        return Integer.parseInt(numberString);
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (CouphUtil.colorAndStrip(event.getInventory().getTitle()).contains("Enchant Menu")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(item)) {
                if (event.getPlayer().getName().contains("*")) return;
                openGUI(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
            }
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if (CouphUtil.colorAndStrip(event.getSource().getTitle()).contains("Enchant Menu")) {
            event.setCancelled(true);
        }
    }
}
