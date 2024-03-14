package me.couph.grizzlyenchants.Enchants;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Laser extends Enchant implements Listener {
    private boolean completedAsync = false;
    private List<Block> currBlocks = new ArrayList<>();
    public Laser() {
        super("Laser", ChatColor.DARK_RED);
        setAbility("Laser");
        setAbilityDescription("Chance to break all blocks in an vertical or horizontal direction.");
        setLevelUpInfo("increase the amount of blocks gained and proc rate.");
        setMaxLevel(1000);
        setRequiredLevel(5);
    }

    public Enchant getEnchant() {
        return this;
    }


    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        if (!(canBreakBlock(event.getPlayer(), event.getBlock()))) return;
        if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(event.getPlayer().getInventory().getItemInMainHand()) == null)
            return;
        if (this.getPlugin().getGrizzlyEnchantHandler().itemHasEnchant(event.getPlayer().getInventory().getItemInMainHand(), this)) {
            if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(event.getPlayer().getInventory().getItemInMainHand())) {
                        int level = getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), getEnchant());
                        Random rand = new Random();
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
                        if (CouphUtil.colorAndStrip(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).contains("Dream")) bound = bound/3;
                        int proc = rand.nextInt(bound);
                        if (proc == 1) {
                            if (!canBreakBlock(event.getPlayer(), event.getBlock())) return;
                            int num = 1;
                            if (event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                                num = rand.nextInt(2) + 1;
                                num = num * 30;
                                if (getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), getEnchant()) > 250) {
                                    num = rand.nextInt(3) + 1;
                                    num = num * 30;
                                }
                                if (getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), getEnchant()) > 500) {
                                    num = rand.nextInt(4) + 1;
                                    num = num * 30;
                                }
                                if (getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), getEnchant()) > 750) {
                                    num = rand.nextInt(5) + 1;
                                    num = num * 30;
                                }
                            }
                            performAsyncTasks(event.getBlock().getLocation(), event.getBlock().getZ(), event.getBlock().getX());
                            int amo = 0;
                            if (completedAsync) {
                                for (Block block : currBlocks) {
                                    if (canBreakBlock(event.getPlayer(), block)) {
                                        addToBackpack(event.getPlayer(), convertOreToMineral(block.getType()), num);
                                        block.getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
                                        event.getPlayer().spawnParticle(Particle.FLAME, block.getLocation(), 1);
                                        event.getPlayer().spawnParticle(Particle.REDSTONE, block.getLocation(), 1);
                                        amo++;
                                    }
                                }
                                if (amo == 0) {
                                    addToBackpack(event.getPlayer(), convertOreToMineral(event.getBlock().getType()), num*5);
                                    amo = 5;
                                }
                            } else {
                                addToBackpack(event.getPlayer(), convertOreToMineral(event.getBlock().getType()), num*3);
                                amo = 3;
                            }
                            CouphUtil.playSound(event.getPlayer(), Sound.ENTITY_WITHER_SHOOT, 0.2F, 3.0F);
                            if (hasMsgsEnabled(event.getPlayer())) {
                                event.getPlayer().sendMessage(CouphUtil.color("&c&kii&r &7&l(!)&r &4&lLASER: BROKE &c&l" + amo * num + " &4&lBLOCKS &7&l(!) &c&kii&r"));
                            }
                        }
            }
        }
        currBlocks = new ArrayList<>();
        completedAsync = false;
    }

    public Boolean hasMsgsEnabled(Player player) {
        return Boolean.valueOf(PlaceholderAPI.setPlaceholders(player, "%prefs-enchMsgs%"));
    }

    public void performAsyncTasks(Location blockLoc, int Z, int X) {
        List<Location> minmax = findGridBounds(blockLoc);
        List<Block> blocks = scanBlocksWithinBorder(minmax.get(0), minmax.get(1), Z, X);
        currBlocks = blocks;
        completedAsync = true;
    }

    public List<Location> findGridBounds(Location blockLocation) {
                World world = blockLocation.getWorld();
                int centerX = blockLocation.getBlockX();
                int centerZ = blockLocation.getBlockZ();

                int minX = centerX;
                int maxX = centerX;
                int minZ = centerZ;
                int maxZ = centerZ;

                // Scan towards negative X direction until bedrock is encountered
                int count1 = 0;
                int x1 = centerX - 1;
                Block block = world.getBlockAt(x1, blockLocation.getBlockY(), centerZ);
                while (block.getType() != Material.BEDROCK) {
                    count1++;
                    x1--;
                    minX = x1;
                    block = world.getBlockAt(x1, blockLocation.getBlockY(), centerZ);
                    if (count1 > 10000) break;
                }

                // Scan towards positive X direction until bedrock is encountered
                int count2 = 0;
                int x2 = centerX + 1;
                Block block2 = world.getBlockAt(x2, blockLocation.getBlockY(), centerZ);
                while (block2.getType() != Material.BEDROCK) {
                    count2++;
                    x2++;
                    maxX = x2;
                    block2 = world.getBlockAt(x2, blockLocation.getBlockY(), centerZ);
                    if (count2 > 10000) break;
                }

                // Scan towards negative Z direction until bedrock is encountered
                int count3 = 0;
                int z1 = centerZ - 1;
                Block block3 = world.getBlockAt(centerX, blockLocation.getBlockY(), z1);
                while (block3.getType() != Material.BEDROCK) {
                    count3++;
                    z1--;
                    minZ = z1;
                    block3 = world.getBlockAt(centerX, blockLocation.getBlockY(), z1);
                    if (count3 > 10000) break;
                }

                // Scan towards positive Z direction until bedrock is encountered
                int count4 = 0;
                int z2 = centerZ + 1;
                Block block4 = world.getBlockAt(centerX, blockLocation.getBlockY(), z2);
                while (block4.getType() != Material.BEDROCK) {
                    count4++;
                    z2++;
                    maxZ = z2;
                    block4 = world.getBlockAt(centerX, blockLocation.getBlockY(), z2);
                    if (count4 > 10000) break;
                }

                List<Location> minmax = new ArrayList<>();
                minmax.add(new Location(blockLocation.getWorld(), minX, blockLocation.getBlockY(), minZ));
                minmax.add(new Location(blockLocation.getWorld(), maxX, blockLocation.getBlockY(), maxZ));
                return minmax;
    }

    public List<Block> scanBlocksWithinBorder(Location bottomLeft, Location topRight, int origz, int origx) {
                List<Block> blocks = new ArrayList<>();
                World world = bottomLeft.getWorld();
                int minX = bottomLeft.getBlockX();
                int minZ = bottomLeft.getBlockZ();
                int maxX = topRight.getBlockX();
                int maxZ = topRight.getBlockZ();

                Random rand = new Random();
                int num = rand.nextInt(2);
                if (num == 0) {
                    for (int x = minX; x <= maxX; x++) {
                        Block block = world.getBlockAt(x, topRight.getBlockY(), origz);
                        if (block.getType() != Material.BEDROCK && block.getType() != Material.AIR) {
                            blocks.add(block);
                        }
                    }
                } else {
                    for (int z = minZ; z <= maxZ; z++) {
                        Block block = world.getBlockAt(origx, topRight.getBlockY(), z);
                        if (block.getType() != Material.BEDROCK && block.getType() != Material.AIR) {
                            blocks.add(block);
                        }
                    }
                }
                return blocks;
    }



    public void addToBackpack(Player player, Material mat, Integer amount) {
        this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, mat, amount);
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

}
