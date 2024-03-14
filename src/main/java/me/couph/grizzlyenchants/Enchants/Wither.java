package me.couph.grizzlyenchants.Enchants;

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
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static me.couph.grizzlytools.GrizzlyTools.grizzlyBackpacks;

public class Wither extends Enchant implements Listener {

    public Wither() {
        super("Wither", ChatColor.DARK_GRAY);
        setAbility("Wither");
        setAbilityDescription("Chance to decay a large amount of blocks around you.");
        setLevelUpInfo("increase the chance of a proc and the amount of blocks you get.");
        setMaxLevel(500);
        setRequiredLevel(20);
    }

    public Enchant getEnchant() {
        return this;
    }


    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        if (!(canBreakBlock(event.getPlayer(), event.getBlock()))) return;
        if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(event.getPlayer().getInventory().getItemInMainHand()) == null) return;
        if (this.getPlugin().getGrizzlyEnchantHandler().itemHasEnchant(event.getPlayer().getInventory().getItemInMainHand(), this)) {
            if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(event.getPlayer().getInventory().getItemInMainHand())) {
                Random rand = new Random();
                int level = this.getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), this);
                int procChance = 750-level;
                if (CouphUtil.colorAndStrip(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).contains("Dream")) procChance = procChance/3;
                int proc = rand.nextInt(procChance);
                int blocks = rand.nextInt(level*40)+1;
                if (proc == 1) {
                    doWitherEffect(event.getPlayer(), event.getBlock().getLocation(), blocks);
                    CouphUtil.playSound(event.getPlayer(), Sound.ENTITY_WITHER_AMBIENT, 0.2f);
                    addToBackpack(event.getPlayer(), event.getBlock().getType(), (blocks+100));
                }
            }
        }

    }

    public void doWitherEffect(Player player, Location l, int blockMultiplier) {
        List<Block> blocks = getBlocksIn7x7(player, l);
        for (Block block : blocks) {
             player.spawnParticle(Particle.SLIME, block.getLocation(), 3);
             new BukkitRunnable() {
                 @Override
                 public void run() {
                     block.setType(Material.AIR);
                 }
             }.runTaskLater(this.getPlugin(), 40L);
        }
        if (hasMsgsEnabled(player)) {
            player.sendMessage(CouphUtil.color("&0&kii&r &7&l(!)&r &8&lWITHER: BROKE &f&l" + (blockMultiplier + 100) + " &8&lBLOCKS &7&l(!) &0&kii&r"));
        }
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

    public Boolean hasMsgsEnabled(Player player) {
        return Boolean.valueOf(PlaceholderAPI.setPlaceholders(player, "%prefs-enchMsgs%"));
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

    public void addToBackpack(Player player, Material mat, Integer amount) {
        this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, mat, amount);
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
