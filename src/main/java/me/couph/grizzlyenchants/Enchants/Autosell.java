package me.couph.grizzlyenchants.Enchants;

import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlyenchants.util.EnchantHandler;
import me.couph.grizzlyenchants.util.PlayerTimerMap;
import me.couph.grizzlytools.GrizzlyTools;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import me.couph.grizzlybackpacks.GrizzlyBackpacks;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Autosell extends Enchant implements Listener {
    //private static Economy econ = null;
    private static String TIMER_ID = "sf";
    private static final PlayerTimerMap timerMap = new PlayerTimerMap(10000L);

    public static boolean timerFlag = false;
    public Autosell instance;

    HashMap<Player, Boolean> cooldownMap;

    public static PlayerTimerMap getTimerMap() {
        return timerMap;
    }

    public Autosell() {
        super("Autosell", ChatColor.GOLD);
        setAbility("Autosell");
        setAbilityDescription("Automatically sell blocks from mining.");
        setMaxLevel(1);
        setRequiredLevel(5);
        instance = this;
        cooldownMap = new HashMap<>();
        //econ = GrizzlyEnchants.getEconomy();
    }

     public Enchant getEnchant() {
        return this;
     }

     @EventHandler
    public void doAutosell(BlockBreakEvent event) {
        try {
            if (event.getBlock().getType() == Material.AIR) return;
        } catch (Exception e) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();
        Material material = convertOreToMineral(block.getType(), item);
        String mat = material.toString();
        double amount = 0.0;
        if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(item) == null) return;
        if (!(getPlugin().getGrizzlyEnchantHandler().itemHasEnchant(item, instance))) return;
        if (cooldownMap.containsKey(event.getPlayer())) return;
        cooldownMap.put(event.getPlayer(), true);
        new BukkitRunnable() {
            @Override
            public void run() {
                double sellPrice = getPriceByMaterial(mat);
                doSell(player, sellPrice, amount, mat);
                cooldownMap.remove(event.getPlayer());
            }
        }.runTaskLater(this.getPlugin(), 20 * 10);
    }

    public Material convertOreToMineral(Material material, ItemStack item) {
        if (!(item.getEnchantments().containsKey(Enchantment.SILK_TOUCH))) {
            if (material == Material.DIAMOND_ORE) {
                return Material.DIAMOND;
            }
            if (material == Material.EMERALD_ORE) {
                return Material.EMERALD;
            }
            if (material == Material.COAL_ORE) {
                return Material.COAL;
            }
            if (material == Material.QUARTZ_ORE) {
                return Material.QUARTZ;
            }
            if (material == Material.REDSTONE_ORE) {
                return Material.REDSTONE;
            }
            if (material == Material.STONE) {
                return Material.COBBLESTONE;
            }
        }
        return material;
    }

    public void doSell(Player player, double sellPrice, double amount, String mat) {
        int multiplier = 1;
        if (timerMap.isOnTimer(player, TIMER_ID)) {
            timerFlag = true;
            multiplier = 2;
        } else {
            if (timerFlag) {
                timerFlag = false;
            }
        }
        this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().sellContents(player, multiplier, "Autosell");
    }

//    public static double getAmountInInv(Player player, Material material) {
//        double amount = 0.0;
//        if (!(player.getInventory().contains(material))) return 0.0;
//        try {
//            for (int i = 0; i < player.getInventory().getSize(); i++) {
//                if (player.getInventory().getItem(i).getType() == material) {
//                    amount = amount + player.getInventory().getItem(i).getAmount();
//                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
//                }
//            }
//        }catch (Exception e) {
//            return (double)amount;
//        }
//        return (double)amount;
//    }

//        public double getAmountInInv(Player player, Material material) {
//        double amount = 0.0;
//        if (!(this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().backpackContainsMaterial(player, material))) return 0.0;
//        try {
//            for (int i = 0; i < player.getInventory().getSize(); i++) {
//                if (player.getInventory().getItem(i).getType() == material) {
//                    amount = amount + player.getInventory().getItem(i).getAmount();
//                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
//                }
//            }
//        }catch (Exception e) {
//            return (double)amount;
//        }
//        return (double)amount;
//    }

    public static double getPriceByMaterial(String material) {
        try {
            FileReader fileReader = new FileReader(GrizzlyEnchants.getInstance().getDataFolder() + File.separator + "prices.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(":");
                String currentMaterial = parts[0].trim();
                double number = Double.parseDouble(parts[1].trim());

                if (currentMaterial.equalsIgnoreCase(material)) {
                    bufferedReader.close();
                    return number;
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1.0;
    }
}
