package me.couph.grizzlyenchants.Listeners;

import com.google.common.collect.Lists;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlyenchants.util.PlayerTimerMap;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import me.couph.grizzlytools.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;

import static org.bukkit.Bukkit.getPlayer;
import static org.bukkit.Bukkit.getWorld;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class EnchantListener implements Listener {
    private final GrizzlyEnchants plugin;
    private GrizzlyTools grizzlyTools;

    private static String TIMER_ID = "cluster";
    private static final PlayerTimerMap timerMap = new PlayerTimerMap(10000L);
    public static PlayerTimerMap getTimerMap() {
        return timerMap;
    }

    public EnchantListener(GrizzlyEnchants plugin) {
        this.plugin = plugin;
        setGrizzlyTools(GrizzlyTools.getInstance());
    }



    public void setGrizzlyTools(GrizzlyTools grizzlyTools) {
        this.grizzlyTools = grizzlyTools;
    }

    @EventHandler
    public void playerLogOn(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (Enchant enchant : this.plugin.getGrizzlyEnchantHandler().getGrizzlyEnchants()) {
            for (ItemStack i : player.getInventory().getContents()) {
                if (enchant.isGrizzlyEnchant(i)) {
                    this.plugin.getGrizzlyEnchantHandler().getByItem(i).reInitialise(player, this.plugin.getGrizzlyEnchantHandler().getByItem(i));
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))return;
        if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
            ItemStack meantToBeBook = event.getCursor();
            ItemStack meantToBePickaxe = event.getView().getItem(event.getRawSlot());
            if (this.plugin.getGrizzlyEnchantHandler().isGrizzlyEnchant(meantToBeBook)) {
                if (this.grizzlyTools.getGrizzlyPickaxeHandler().isGrizzlyPickaxe(meantToBePickaxe)) {
                    if (this.plugin.getGrizzlyEnchantHandler().canApplyEnchant(((Player) event.getWhoClicked()).getPlayer(), meantToBePickaxe, this.plugin.getGrizzlyEnchantHandler().getByItem(meantToBeBook), getLevelByName(meantToBePickaxe))) {
                        ItemMeta newMeta = this.plugin.getGrizzlyEnchantHandler().applyEnchant(((Player) event.getWhoClicked()).getPlayer(), meantToBePickaxe, this.plugin.getGrizzlyEnchantHandler().getByItem(meantToBeBook));
                        meantToBePickaxe.setItemMeta(newMeta);
                        event.setCancelled(true);
                        if (meantToBeBook.getAmount() == 1) {
                            ((Player) event.getWhoClicked()).getPlayer().setItemOnCursor(null);
                        } else {
                            int amount = meantToBeBook.getAmount();
                            amount = amount - 1;
                            meantToBeBook.setAmount(amount);
                            ((Player) event.getWhoClicked()).getPlayer().setItemOnCursor(meantToBeBook);
                        }
                        CouphUtil.playSound(((Player) event.getWhoClicked()).getPlayer(), Sound.ENTITY_PLAYER_LEVELUP);
                    }
                } else {
                    ((Player) event.getWhoClicked()).getPlayer().sendMessage(CouphUtil.color("&c(!) You can't apply an enchantment to this item!"));
                }
            }
        }
    }

    public Integer getLevelByName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        String displayName = CouphUtil.colorAndStrip(meta.getDisplayName());
        String numberString = displayName.replaceAll("[^0-9]", "");
        return Integer.parseInt(numberString);
    }

    @EventHandler
    public void loreFix(PlayerDropItemEvent e) {
        if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(e.getItemDrop().getItemStack())) {
            List<String> lore = e.getItemDrop().getItemStack().getItemMeta().getLore();
            List<String> newLore = lore;
            for (int x=0; x<lore.size(); x++) {
                String loreLine = lore.get(x);
                if (CouphUtil.colorAndStrip(loreLine).contains("Blocks Broken: ")) {
                    try {
                        if (CouphUtil.colorAndStrip(lore.get(x+1)).contains("Blocks Broken: ")) {
                            newLore.remove(x);
                        }
                    } catch (Exception ex) {
                        break;
                    }
                }
            }
            ItemMeta meta = e.getItemDrop().getItemStack().getItemMeta();
            meta.setLore(newLore);
            e.getItemDrop().getItemStack().setItemMeta(meta);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    //manage pick drop
    public void onPickaxeDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();
        GrizzlyPickaxe grizzlyPickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(item);
        if (grizzlyPickaxe == null) {
            return;
        }
        event.setCancelled(true);

        //String MM = PlaceholderAPI.setPlaceholders(player, "%IGNI%");
        //if (MM.equalsIgnoreCase("true")) {
         //   String petLevel = PlaceholderAPI.setPlaceholders(player, "%IGNILevel%");
         //   doClusterBomb(player, Integer.parseInt(petLevel));

        //} else {
        player.sendMessage(me.couph.grizzlytools.util.CouphUtil.color("&c(!) You can't drop this item!"));
    }

//    public void doClusterBomb(Player player, int level) {
//        if (regionIsNotValid(player.getLocation())) return;
//        PlayerTimerMap timerMap = getTimerMap();
//        if (timerMap.isOnTimer(player, "cluster")) {
//            player.sendMessage(CouphUtil.color("&c(!) Please wait " + Math.round(timerMap.getRemainingTime(player, "cluster") / 1000)) + "s before using this again!");
//        } else {
//            timerMap.add(player, 30*1000, "cluster");
//            TNTPrimed tnt = (TNTPrimed) player.getWorld().spawnEntity(player.getLocation(), EntityType.PRIMED_TNT);
//            tnt.setFuseTicks(60);
//            tnt.setGravity(true);
//            tnt.setIsIncendiary(false);
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    if (regionIsMine(tnt.getLocation())) {
//                        Random rand = new Random();
//                        int amount = rand.nextInt(5000)+5000;
//                        amount = amount * (level+1);
//                        plugin.grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, getBlockFromTntLoc(tnt.getLocation()), amount);
//                        player.sendMessage(CouphUtil.color("&c&kii&r &4&lIGNITION PET: &8&lBLOWN UP &4&l" + amount + " &8&lBLOCKS &c&kii&r"));
//                    }
//                }
//            }.runTaskLater(this.plugin, 59);
//        }
//    }

    public Material getBlockFromTntLoc(Location loc) {
        loc.setY(loc.getY());
        Block block = loc.getBlock();
        if (block.getType() == Material.AIR || block.getType() == Material.BEDROCK) {
            loc.setY(loc.getY()-1);
            block = loc.getBlock();
        }
        if (block.getType() == Material.AIR || block.getType() == Material.BEDROCK) {
            loc.setY(loc.getY()-2);
            block = loc.getBlock();
        }
        if (block.getType() == Material.AIR || block.getType() == Material.BEDROCK) {
            loc.setY(loc.getY()-3);
            block = loc.getBlock();
        }
        if (block.getType() == Material.AIR || block.getType() == Material.BEDROCK) {
            loc.setY(loc.getY()-4);
            block = loc.getBlock();
        }
        if (block.getType() == Material.AIR || block.getType() == Material.BEDROCK) {
            loc.setY(loc.getY()-5);
            block = loc.getBlock();
        }
        if (!(block.getType() == Material.AIR || block.getType() == Material.BEDROCK)) {
            return block.getType();
        }
        else {
            return Material.AIR;
        }
    }

    public boolean regionIsPvP(Location locat) {
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(locat);

        for (ProtectedRegion region : regions) {
            if (region.getId().contains("pvp") || region.getId().contains("arena")) {
                return true;
            }
        }
        return false;
    }

    public boolean regionIsMine(Location locat) {
        locat.setY(locat.getY()-1);
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(locat);

        for (ProtectedRegion region : regions) {
            return region.getId().contains("mine");
        }
        return false;
    }

    public boolean regionIsNotValid(Location locat) {
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(locat);

        for (ProtectedRegion region : regions) {
            return region.getId().contains("spawn") || region.getId().contains("pvp") || region.getId().contains("parkour");
        }
        return false;
    }

    public boolean canBreakBlock(Player player, Block block) {
        Plugin plugin = WorldGuardPlugin.inst();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());

        for (ProtectedRegion region : regions) {
            Flag<?> blockBreakFlag = DefaultFlag.BLOCK_BREAK;
            Object flagValue = region.getFlag(blockBreakFlag);

            if (regions.testState(localPlayer, DefaultFlag.BLOCK_BREAK)) {
                return true;
            }
        }

        return false;
    }

    @EventHandler
    public void blockFishingRod(PlayerFishEvent event) {
        if (!(regionIsPvP(event.getPlayer().getLocation()))) {
            event.setCancelled(true);
        }
    }
}
