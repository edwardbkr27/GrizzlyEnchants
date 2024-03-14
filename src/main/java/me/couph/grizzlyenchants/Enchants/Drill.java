package me.couph.grizzlyenchants.Enchants;

import com.boydti.fawe.Fawe;
import com.boydti.fawe.object.FaweQueue;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.entity.SpawnEntityEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlytools.GrizzlyTools;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import com.sk89q.worldedit.EditSession;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.boydti.fawe.FaweAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import static me.couph.grizzlytools.GrizzlyTools.grizzlyBackpacks;
import static me.couph.grizzlytools.GrizzlyTools.worldEdit;

public class Drill extends Enchant implements Listener {
    private List<Block> currBlocks = new ArrayList<>();

    private static final Random rand = new Random();

    public Drill() {
        super("Drill", ChatColor.WHITE);
        setAbility("Drill");
        setAbilityDescription("Chance to break an entire layer of blocks.");
        setLevelUpInfo("increase the chance of a proc and amount of blocks gained.");
        setMaxLevel(1000);
        setRequiredLevel(5);
    }

    public Enchant getEnchant() {
        return this;
    }


    public List<Integer> getMineLocation(String mine) {
        List<Integer> minmax = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(GrizzlyEnchants.getInstance().getDataFolder() + File.separator + "mineLocations.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(":");
                String mineName = parts[0].trim();
                if (mineName.equalsIgnoreCase(mine)) {
                    int minX = Integer.parseInt(parts[1].trim());
                    int maxX = Integer.parseInt(parts[2].trim());
                    int minZ = Integer.parseInt(parts[3].trim());
                    int maxZ = Integer.parseInt(parts[4].trim());
                    minmax.add(minX);
                    minmax.add(maxX);
                    minmax.add(minZ);
                    minmax.add(maxZ);
                    return minmax;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        if (!(canBreakBlock(event.getPlayer(), event.getBlock()))) return;
        if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(event.getPlayer().getInventory().getItemInMainHand()) == null) return;
        if (this.getPlugin().getGrizzlyEnchantHandler().itemHasEnchant(event.getPlayer().getInventory().getItemInMainHand(), this)) {
            if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(event.getPlayer().getInventory().getItemInMainHand())) {
                        int level = getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), getEnchant());
                        int bound = 2250 - (level*2);

                        // pet calc
                        String MM = PlaceholderAPI.setPlaceholders(event.getPlayer(), "%MM%");
                        if (MM.equalsIgnoreCase("true")) {
                            String petLevel = PlaceholderAPI.setPlaceholders(event.getPlayer(), "%MMLevel%");
                            if (petLevel.equalsIgnoreCase("0")) {
                                bound = (int)(bound - ((bound/100)*5));
                            }
                            if (petLevel.equalsIgnoreCase("1")) {
                                bound = (int)(bound - ((bound/100)*10));
                            }
                            if (petLevel.equalsIgnoreCase("2")) {
                                bound = (int)(bound - ((bound/100)*15));
                            }
                            if (petLevel.equalsIgnoreCase("3")) {
                                bound = (int)(bound - ((bound/100)*20));
                            }
                            if (petLevel.equalsIgnoreCase("4")) {
                                bound = (int)(bound - ((bound/100)*25));
                            }
                            if (petLevel.equalsIgnoreCase("5")) {
                                bound = (int)(bound - ((bound/100)*30));
                            }
                        }
                        if (event.getBlock().getType() == Material.AIR) bound = bound*3;
                        if (CouphUtil.colorAndStrip(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).contains("Dream")) bound = bound/3;
                        int proc = rand.nextInt(bound);

                        if (proc == 1) {
                            if (!canBreakBlock(event.getPlayer(), event.getBlock())) return;
                            int num = 1;
                            if (event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                                num = rand.nextInt(2) + 1;
                                if (getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), getEnchant()) > 250) {
                                    num = rand.nextInt(3) + 1;
                                }
                                if (getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), getEnchant()) > 500) {
                                    num = rand.nextInt(4) + 1;
                                }
                                if (getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), getEnchant()) > 750) {
                                    num = rand.nextInt(5) + 1;
                                }
                            }
                            List<Location> minmax = performAsyncTasks(event.getBlock().getLocation());
                            int amo = 0;
                            Material blockType = event.getBlock().getType();
                            amo = processBlocks(minmax.get(0), minmax.get(1), event.getPlayer(), num);
                            addToBackpack(event.getPlayer(), convertOreToMineral(blockType), num*amo);
                            CouphUtil.playSound(event.getPlayer(), Sound.BLOCK_ANVIL_LAND, 0.3F);
                            if (hasMsgsEnabled(event.getPlayer())) {
                                event.getPlayer().sendMessage(CouphUtil.color("&8&kii&r &7&l(!)&r &f&lDRILL: BROKE &8&l" + amo * num + " &f&lBLOCKS &7&l(!) &8&kii&r"));
                            }
                            currBlocks = new ArrayList<>();
                        }
            }
        }
    }

    public List<Location> performAsyncTasks(Location blockLoc) {
        List<Location> minmax = findGridBounds(blockLoc);
        return minmax;
    }


    public List<Location> findGridBounds(Location blockLocation) {
        List<Location> locList = new ArrayList<>();
        World world = blockLocation.getWorld();
        String mineName = getMineName(blockLocation);
        List<Integer> minmax = getMineLocation(mineName);
        Integer minX = minmax.get(0);
        Integer maxX = minmax.get(1);
        Integer minZ = minmax.get(2);
        Integer maxZ = minmax.get(3);
        double y = blockLocation.getY()+1;
        Location bottomLeft = new Location(world, minX, y, minZ);
        Location topRight = new Location(world, maxX, y, maxZ);
        locList.add(bottomLeft);
        locList.add(topRight);
        return locList;
    }

    public Integer processBlocks(Location bottomLeft, Location topRight, Player player, int num) {
                World world = bottomLeft.getWorld();
                int minX = bottomLeft.getBlockX();
                int minZ = bottomLeft.getBlockZ();
                int maxX = topRight.getBlockX();
                int maxZ = topRight.getBlockZ();
                int amo = 0;

                EditSession editSession = FaweAPI.getEditSessionBuilder(FaweAPI.getWorld(world.getName()))
                        .fastmode(true)
                        .build();

                try {
                    for (int x = minX; x <= maxX; x++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            Vector blockVector = new Vector(x, topRight.getBlockY(), z);

                            // Check if the block can be broken using your canBreakBlock method
                            if (canBreakBlock(player, world.getBlockAt(x, topRight.getBlockY(), z))) {
                                BaseBlock airBlock = new BaseBlock(0); // Assuming 0 represents air
                                editSession.setBlock(blockVector, airBlock);
                                amo++;
                            }
                        }
                    }

                    // Remember to flush the FAWE edit session to apply changes
                    editSession.flushQueue();
                } catch (Exception e){
                    e.printStackTrace();
                }

                return amo;
    }



    public void addToBackpack(Player player, Material mat, Integer amount) {
        this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, mat, amount);
    }

    public boolean canBreakBlock(Player player, Block block) {
        if (block.getType() == Material.BEDROCK) return false;
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

    public Material convertOreToMineral(Material material) {
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
        return material;
    }

    public Boolean hasMsgsEnabled(Player player) {
        return Boolean.valueOf(PlaceholderAPI.setPlaceholders(player, "%prefs-enchMsgs%"));
    }

    public String getMineName(Location locat) {
        locat.setY(locat.getY()-1);
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(locat);

        for (ProtectedRegion region : regions) {
            if (region.getId().contains("mine")) {
                return region.getId();
            }
        }
        return null;
    }
}
