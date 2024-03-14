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
import me.couph.grizzlyenchants.util.PlayerTimerMap;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class Hallucination extends Enchant implements Listener {

    private HashMap<Material, Integer> worth;

    public Hallucination() {
        super("Hallucination", ChatColor.LIGHT_PURPLE);
        setAbility("Hallucination");
        setAbilityDescription("Become delusional and turn blocks around you into higher value blocks.");
        setLevelUpInfo("increase the chance of a proc and amount of blocks that change.");
        setMaxLevel(300);
        setRequiredLevel(20);
        this.worth = new HashMap<>();
        worth.put(Material.COAL_ORE, 1);
        worth.put(Material.COAL, 2);
        worth.put(Material.COAL_BLOCK, 3);
        worth.put(Material.IRON_ORE, 12);
        worth.put(Material.IRON_INGOT, 13);
        worth.put(Material.IRON_BLOCK, 14);
        worth.put(Material.GOLD_ORE, 15);
        worth.put(Material.GOLD_INGOT, 16);
        worth.put(Material.GOLD_BLOCK, 17);
        worth.put(Material.DIAMOND_ORE, 18);
        worth.put(Material.DIAMOND, 19);
        worth.put(Material.DIAMOND_BLOCK, 20);
        worth.put(Material.EMERALD_ORE, 21);
        worth.put(Material.EMERALD, 22);
        worth.put(Material.EMERALD_BLOCK, 23);
        worth.put(Material.OBSIDIAN, 24);
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
                int level = this.getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), this);
                Random rand = new Random();
                int bound = 2000 - level*5;
                if (CouphUtil.colorAndStrip(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).contains("Dream")) bound = bound/3;
                int num = rand.nextInt(bound);
                if (num == 1) {
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_WITHER_SHOOT, 0.2F, 0.1F);
                    if (hasMsgsEnabled(event.getPlayer())) {
                        event.getPlayer().sendMessage(CouphUtil.color("&5&kii&r &d&l&oYOU'RE HAVING A HALLUCINATION... &5&kii&r"));
                    }
                    List<Block> blocks = getBlocksIn3x3(event.getPlayer(), event.getBlock().getLocation());
                    if (level >= 100) {
                        blocks = getBlocksIn5x5(event.getPlayer(), event.getBlock().getLocation());
                    }
                    if (level >= 200) {
                        blocks = getBlocksIn7x7(event.getPlayer(), event.getBlock().getLocation());
                    }
                    Material highestPriorityMaterial = findHighestPriorityMaterial(worth, blocks);
                    for (Block block : blocks) {
                        Location l = block.getLocation();
                        if (block.getType() != Material.AIR) {
                            block.getWorld().getBlockAt(l).setType(highestPriorityMaterial);
                            block.getWorld().spawnParticle(Particle.CLOUD, l, 1);
                        }
                    }
                }
            }
        }
    }

    public Boolean hasMsgsEnabled(Player player) {
        return Boolean.valueOf(PlaceholderAPI.setPlaceholders(player, "%prefs-enchMsgs%"));
    }

    public static Material findHighestPriorityMaterial(Map<Material, Integer> worth, List<Block> blockList) {
        Material highestPriorityMaterial = null;
        int highestPriority = Integer.MIN_VALUE;

        for (Block block : blockList) {
            Material material = block.getType();
            if (worth.containsKey(material)) {
                int priority = worth.get(material);
                if (priority > highestPriority) {
                    highestPriority = priority;
                    highestPriorityMaterial = material;
                }
            }
        }
        return highestPriorityMaterial;
    }

    public List<Block> getBlocksIn3x3(Player player, Location location) {
        List<Block> blocks = new ArrayList<>();
        World world = location.getWorld();

        if (world != null) {
            int centerX = location.getBlockX();
            int centerY = location.getBlockY();
            int centerZ = location.getBlockZ();

            // Iterate over the blocks in a 3x3 radius
            for (int x = centerX - 1; x <= centerX + 1; x++) {
                for (int y = centerY - 1; y <= centerY + 1; y++) {
                    for (int z = centerZ - 1; z <= centerZ + 1; z++) {
                        Location blockLocation = new Location(world, x, y, z);
                        Block block = world.getBlockAt(blockLocation);
                        //Material blockType = block.getType();
                        if (canBreakBlock(player, block)) {
                            blocks.add(block);
                        }

                        // Add the dropped item to the list
//                        if (blockType != Material.AIR && blockType != Material.BEDROCK) {
//                            drops.add(new ItemStack(convertOreToMineral(blockType)));
//                        }
                    }
                }
            }
        }

        return blocks;
    }

    public List<Block> getBlocksIn7x7(Player player, Location location) {
        List<Block> blocks = new ArrayList<>();
        World world = location.getWorld();

        if (world != null) {
            int centerX = location.getBlockX();
            int centerY = location.getBlockY();
            int centerZ = location.getBlockZ();

            // Iterate over the blocks in a 3x3 radius
            for (int x = centerX - 3; x <= centerX + 3; x++) {
                for (int y = centerY - 3; y <= centerY + 3; y++) {
                    for (int z = centerZ - 3; z <= centerZ + 3; z++) {
                        Location blockLocation = new Location(world, x, y, z);
                        Block block = world.getBlockAt(blockLocation);
                        //Material blockType = block.getType();
                        if (canBreakBlock(player, block)) {
                            blocks.add(block);
                        }

                        // Add the dropped item to the list
//                        if (blockType != Material.AIR && blockType != Material.BEDROCK) {
//                            drops.add(new ItemStack(convertOreToMineral(blockType)));
//                        }
                    }
                }
            }
        }

        return blocks;
    }


    public List<Block> getBlocksIn5x5(Player player, Location location) {
        List<Block> blocks = new ArrayList<>();
        World world = location.getWorld();

        if (world != null) {
            int centerX = location.getBlockX();
            int centerY = location.getBlockY();
            int centerZ = location.getBlockZ();

            // Iterate over the blocks in a 3x3 radius
            for (int x = centerX - 2; x <= centerX + 2; x++) {
                for (int y = centerY - 2; y <= centerY + 2; y++) {
                    for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                        Location blockLocation = new Location(world, x, y, z);
                        Block block = world.getBlockAt(blockLocation);
                        //Material blockType = block.getType();
                        if (canBreakBlock(player, block)) {
                            blocks.add(block);
                        }

                        // Add the dropped item to the list
//                        if (blockType != Material.AIR && blockType != Material.BEDROCK) {
//                            drops.add(new ItemStack(convertOreToMineral(blockType)));
//                        }
                    }
                }
            }
        }

        return blocks;
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
