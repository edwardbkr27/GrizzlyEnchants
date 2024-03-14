package me.couph.grizzlyenchants.util;

import me.couph.grizzlyenchants.Enchants.Autosell;
import me.couph.grizzlyenchants.Enchants.Efficiency;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlyenchants.Tokens.TokenShard;
import me.couph.grizzlytools.GrizzlyTools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Handler;

public class Tinkerer implements Listener {

    private Integer itemCount;
    private Integer slotCount;
    private List<Player> accepted;
    private List<Integer> playerSlots = new ArrayList<>();
    private List<Integer> returnSlots = new ArrayList<>();
    private EnchantHandler enchHandler = GrizzlyEnchants.getInstance().getGrizzlyEnchantHandler();

    private HashMap<Player, List<ItemStack>> returnItems = new HashMap<>();
    private HashMap<Player, List<ItemStack>> playerItems = new HashMap<>();

    private HashMap<String, Integer> tokenMap = new HashMap<>();

    private HashMap<Player, Inventory> savedInventories = new HashMap<>();

    private Player player;

    public Tinkerer(Player player) {
        this.itemCount = 0;
        this.slotCount=0;
        this.accepted = new ArrayList<>();
        playerSlots.add(0);
        playerSlots.add(1);
        playerSlots.add(2);
        playerSlots.add(3);
        playerSlots.add(9);
        playerSlots.add(10);
        playerSlots.add(11);
        playerSlots.add(12);
        playerSlots.add(18);
        playerSlots.add(19);
        playerSlots.add(20);
        playerSlots.add(21);
        playerSlots.add(27);
        playerSlots.add(28);
        playerSlots.add(29);
        playerSlots.add(30);
        playerSlots.add(36);
        playerSlots.add(37);
        playerSlots.add(38);
        playerSlots.add(39);
        playerSlots.add(45);
        playerSlots.add(46);
        playerSlots.add(47);
        playerSlots.add(48);

        for (int x=0; x<54; x++) {
            if (!(playerSlots.contains(x))) {
                if (x!=4 && x!=13 && x!=22 && x!=31 && x!=40 && x!=49) returnSlots.add(x);
            }
        }

        createTokenMap();
    }

    public void openGui(Player player) {
        this.player = player;
        Inventory gui = Bukkit.createInventory(null, 54, CouphUtil.color("&6Tinkerer"));

        ItemStack filler = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 1);
        ItemMeta meta = filler.getItemMeta();
        meta.setDisplayName(" ");
        filler.setItemMeta(meta);
        gui.setItem(13, filler);
        gui.setItem(22, filler);
        gui.setItem(31, filler);
        gui.setItem(40, filler);

        ItemStack confirm = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)5);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(CouphUtil.color("&a&lCONFIRM"));
        List<String> confirmLore = new ArrayList<>();
        confirmLore.add(CouphUtil.color("&c(!) This cannot be undone!"));
        confirmMeta.setLore(confirmLore);
        confirm.setItemMeta(confirmMeta);
        gui.setItem(49, confirm);

        ItemStack cancel = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)14);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(CouphUtil.color("&c&lCANCEL"));
        List<String> cancelLore = new ArrayList<>();
        cancelLore.add(CouphUtil.color("&7(!) Cancel the trade."));
        cancelMeta.setLore(cancelLore);
        cancel.setItemMeta(cancelMeta);
        gui.setItem(4, cancel);

        this.itemCount=0;
        this.slotCount=0;
        this.accepted.remove(player);

        player.openInventory(gui);
    }

    public HashMap<Enchant, Integer> getEnchants(ItemStack item) {
        HashMap<Enchant, Integer> enchantMap = new HashMap<>();
        if (item.getEnchantments().containsKey(Enchantment.DIG_SPEED)) {
            enchantMap.put(new Efficiency(), item.getEnchantments().get(Enchantment.DIG_SPEED)-10);
        }
        for (Enchant enchant : enchHandler.getGrizzlyEnchants()) {
            if (enchHandler.itemHasEnchant(item, enchant)) {
                int level = enchHandler.getEnchantLevel(item, enchant);
                enchantMap.put(enchant, level);
            }
        }
        return enchantMap;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (CouphUtil.colorAndStrip(e.getInventory().getTitle()).equalsIgnoreCase("Tinkerer")) {
            e.setCancelled(true);
        }
    }

    public ItemStack removeEnchants(ItemStack item) {
        item.removeEnchantment(Enchantment.DIG_SPEED);
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 10);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = item.getItemMeta().getLore();
        List<String> loreCopy = item.getItemMeta().getLore();
        for (String loreLine : lore) {
            String loreLine2 = CouphUtil.colorAndStrip(loreLine);
            if (loreLine2.contains("This is a")) break;
            loreCopy.remove(loreLine);
        }
        meta.setLore(loreCopy);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent e) {
        if ((Player)e.getWhoClicked() != this.player) return;
        try {
            // 0-53 = in tinkerer inv
            // >=54 = in player inv
            if (CouphUtil.colorAndStrip(e.getInventory().getTitle()).equalsIgnoreCase("Tinkerer")) {
                e.setCancelled(true);
                ItemStack clickedItem = e.getCurrentItem();
                int slot = e.getRawSlot();
                if (slot < 54) {
                    if (CouphUtil.colorAndStrip(clickedItem.getItemMeta().getDisplayName()).contains("CONFIRM")) {
                        for (int returnSlot : this.returnSlots) {
                            if (e.getInventory().getItem(returnSlot) != null) {
                                if (e.getInventory().getItem(returnSlot).getType() != Material.AIR) {
                                    if (e.getWhoClicked() == this.player) {
                                        e.getWhoClicked().getInventory().addItem(addID(e.getInventory().getItem(returnSlot)));
                                    }
                                }
                            }
                        }
                        this.accepted.add((Player)e.getWhoClicked());
                        e.getWhoClicked().sendMessage(CouphUtil.color("&a(!) Tinkerer trade accepted."));
                        e.getWhoClicked().getOpenInventory().close();
                    }
                    if (CouphUtil.colorAndStrip(clickedItem.getItemMeta().getDisplayName()).contains("CANCEL")) {
                        e.getWhoClicked().getOpenInventory().close();
                        e.getWhoClicked().sendMessage(CouphUtil.color("&c(!) Tinkerer trade closed."));
                    }
                    return;
                }
                if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(clickedItem)) {
                    e.getWhoClicked().getOpenInventory().setItem(slot, new ItemStack(Material.AIR));
                    this.itemCount++;
                    HashMap<Enchant, Integer> enchantMap = new HashMap<>();
                    enchantMap = getEnchants(clickedItem);
                    int tokenCount = 0;
                    for (Enchant enchant : enchantMap.keySet()) {
                        tokenCount += getTokens(enchant, enchantMap.get(enchant));
                    }
                    TokenShard tokenShard = new TokenShard(GrizzlyEnchants.getInstance());
                    ItemStack tokenItem = tokenShard.createItem(tokenCount+20);
                    if (itemCount == 1) {
                        e.getWhoClicked().getOpenInventory().setItem(this.returnSlots.get(this.itemCount - 1), tokenItem);
                        e.getWhoClicked().getOpenInventory().setItem(this.playerSlots.get(this.itemCount - 1), clickedItem);
                        e.getWhoClicked().getOpenInventory().setItem(this.returnSlots.get(this.itemCount), removeEnchants(clickedItem));
                    } else {
                        e.getWhoClicked().getOpenInventory().setItem(this.returnSlots.get(this.itemCount + slotCount), tokenItem);
                        e.getWhoClicked().getOpenInventory().setItem(this.playerSlots.get(this.itemCount - 1), clickedItem);
                        e.getWhoClicked().getOpenInventory().setItem(this.returnSlots.get(this.itemCount + slotCount+1), removeEnchants(clickedItem));
                        this.slotCount++;
                    }
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            return;
        }
    }

    @EventHandler
    public void invClose(InventoryCloseEvent e) {
        if (CouphUtil.colorAndStrip(e.getPlayer().getOpenInventory().getTitle()).equalsIgnoreCase("Tinkerer")) {
            if (this.accepted.contains((Player) e.getPlayer())) {
                reset((Player) e.getPlayer());
                return;
            }
            for (int slot : this.playerSlots) {
                if (e.getPlayer().getOpenInventory().getItem(slot) != null) {
                    if (e.getPlayer().getOpenInventory().getItem(slot).getType() != Material.AIR) {
                        if (!(this.accepted.contains((Player) e.getPlayer()))) {
                            if (e.getPlayer() == this.player) {
                                e.getPlayer().getInventory().addItem(addID(e.getPlayer().getOpenInventory().getItem(slot)));
                            }
                        }
                    }
                }
            }
            reset((Player) e.getPlayer());
            return;
        }
        postTinkerCheck((Player)e.getPlayer());
    }

    public int getTokens(Enchant enchant, int level) {
        if (enchant.getName().equalsIgnoreCase("Efficiency"))
            return ((this.tokenMap.get(enchant.getName())) * level-10);
        else return ((this.tokenMap.get(enchant.getName())) * level);
    }

    public void reset(Player player) {
        this.itemCount=0;
        this.accepted.remove(player);
        this.slotCount=0;
        Tinkerer tinkerer = GrizzlyEnchants.getInstance().activeTinkerers.remove(player);
        if (tinkerer != null) {
            HandlerList.unregisterAll(tinkerer);
        }
        postTinkerCheck((Player)player);
    }

    public ItemStack addID(ItemStack item) {
        if (item.getType() != Material.DIAMOND_PICKAXE) return item;
        Random rand = new Random();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add( CouphUtil.color("&7Tinkerer ID: " + rand.nextInt(1000000000)));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void postTinkerCheck(Player player) {
                List<String> savedID = new ArrayList<>();
                for (int x=0; x<player.getInventory().getSize(); x++) {
                    try {
                        ItemStack item = player.getInventory().getItem(x);
                        if (item.getType() == Material.DIAMOND_PICKAXE) {
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = meta.getLore();
                            for (String loreLine : lore) {
                                loreLine = CouphUtil.colorAndStrip(loreLine);
                                if (loreLine.contains("Tinkerer ID")) {
                                    if (savedID.contains(loreLine)) {
                                        player.getInventory().setItem(x, new ItemStack(Material.AIR));
                                        savedID.remove(loreLine);
                                    }
                                    savedID.add(loreLine);
                                }
                            }
                        }
                    } catch (Exception ignored) {

                    }
                }
                for (int i=0; i<player.getInventory().getSize(); i++) {
                    try {
                        ItemStack item2 = player.getInventory().getItem(i);
                        if (item2.getType() == Material.DIAMOND_PICKAXE) {
                            ItemMeta meta2 = item2.getItemMeta();
                            List<String> lore2 = meta2.getLore();
                            for (String loreLine2 : lore2) {
                                if (CouphUtil.colorAndStrip(loreLine2).contains("Tinkerer ID")) {
                                    lore2.remove(loreLine2);
                                    meta2.setLore(lore2);
                                    item2.setItemMeta(meta2);
                                    player.getInventory().setItem(i, item2);
                                    return;
                                }
                            }
                        }
                    } catch (Exception ignored) {

                    }
                }
    }

    // wont work as unregisterd
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e) {
        postTinkerCheck(e.getPlayer());
    }

    public void createTokenMap() {
        this.tokenMap.put("Efficiency", 6000);
        this.tokenMap.put("Token Boost", 22500);
        this.tokenMap.put("XP Boost", 22500);
        this.tokenMap.put("Treasure", 37500);
        this.tokenMap.put("Drill", 37500);
        this.tokenMap.put("Laser", 37500);
        this.tokenMap.put("Dragons Breath", 75000);
        this.tokenMap.put("Thor", 37500);
        this.tokenMap.put("Autosell", 2500000);
        this.tokenMap.put("Fortune Frenzy", 150000);
        this.tokenMap.put("Hallucination", 60000);
        this.tokenMap.put("Wither", 37500);
        this.tokenMap.put("Bounty", 75000);
        this.tokenMap.put("Black Hole", 93750);
    }
}
