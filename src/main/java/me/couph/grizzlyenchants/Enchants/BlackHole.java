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
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlackHole extends Enchant implements Listener {

    public BlackHole() {
        super("Black Hole", ChatColor.DARK_BLUE);
        setAbility("Black Hole");
        setAbilityDescription("Chance to spawn a black hole in your mine, sucking in nearby blocks.");
        setLevelUpInfo("increase the chance of a proc and the amount of blocks given");
        setMaxLevel(500);
        setRequiredLevel(50);
    }

    public Enchant getEnchant() {
        return this;
    }


    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        if (!(canBreakBlock(event.getPlayer(), event.getBlock().getLocation()))) return;
        if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(event.getPlayer().getInventory().getItemInMainHand()) == null) return;
        if (this.getPlugin().getGrizzlyEnchantHandler().itemHasEnchant(event.getPlayer().getInventory().getItemInMainHand(), this)) {
            if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(event.getPlayer().getInventory().getItemInMainHand())) {
                Random rand = new Random();
                int level = this.getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), this);
                int procChance = 850;
                int bound = procChance - level;
                if (CouphUtil.colorAndStrip(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).contains("Dream")) bound = bound/3;
                int proc = rand.nextInt(bound);
                int blocks = rand.nextInt(level)*45;
                blocks = blocks+(level*2);
                if (proc == 1) {
                    doAnimation(event.getPlayer(), event.getBlock().getLocation(), blocks+100, event.getBlock().getType());
                }
            }
        }

    }

    public void doAnimation(Player player, Location l, int amount, Material material) {
        l.getWorld().getBlockAt(l).setType(Material.AIR);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 0.5f, 0.2f);
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Block> firstSurrounding = getFirstSurroundingBlocks(player, l);
                for (Block block : firstSurrounding) {
                    block.setType(Material.AIR);
                    player.spawnParticle(Particle.SPELL_WITCH, block.getLocation(), 1);
                }
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 0.5f, 1f);
            }
        }.runTaskLater(this.getPlugin(), 20);
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Block> secondSurrounding = getSecondSurroundingBlocks(player, l);
                for (Block block : secondSurrounding) {
                    block.setType(Material.AIR);
                    player.spawnParticle(Particle.SPELL_WITCH, block.getLocation(), 1);
                }
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 0.5f, 1f);
            }
        }.runTaskLater(this.getPlugin(), 40);
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Block> thirdSurrounding = getThirdSurroundingBlocks(player, l);
                for (Block block : thirdSurrounding) {
                    block.setType(Material.AIR);
                    player.spawnParticle(Particle.SPELL_WITCH, block.getLocation(), 1);
                }
                player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 0.5f, 1f);
                if (hasMsgsEnabled(player)) {
                    player.sendMessage(CouphUtil.color("&8&kii&r &9&l(!)&r &1&lBLACK HOLE: DESTROYED &9&l" + amount + " &1&lBLOCKS &9&l(!) &8&kii&r"));
                    addToBackpack(player, convertOreToMineral(material), amount);
                }
            }
        }.runTaskLater(this.getPlugin(), 60);

    }

    public List<Block> getFirstSurroundingBlocks(Player player, Location location) {
        List<Block> surroundingBlocks = new ArrayList<>();

        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerY = location.getBlockY()-1;
        int centerZ = location.getBlockZ();

        for (int x = centerX - 1; x <= centerX + 1; x++) {
            for (int z = centerZ - 1; z <= centerZ + 1; z++) {
                Block block = world.getBlockAt(x, centerY, z);
                if (!block.getLocation().equals(location)) {
                    if (canBreakBlock(player, block.getLocation())) {
                        surroundingBlocks.add(block);
                    }
                }
            }
        }
        return surroundingBlocks;
    }

    public List<Block> getSecondSurroundingBlocks(Player player, Location location) {
        List<Block> surroundingBlocks = new ArrayList<>();

        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerY = location.getBlockY()-1;
        int centerZ = location.getBlockZ();

        for (int x = centerX - 2; x <= centerX + 2; x++) {
            for (int z = centerZ - 2; z <= centerZ + 2; z++) {
                Block block = world.getBlockAt(x, centerY, z);
                if (!block.getLocation().equals(location)) {
                    if (canBreakBlock(player, block.getLocation())) {
                        surroundingBlocks.add(block);
                    }
                }
            }
        }
        return surroundingBlocks;
    }

    public List<Block> getThirdSurroundingBlocks(Player player, Location location) {
        List<Block> surroundingBlocks = new ArrayList<>();

        World world = location.getWorld();
        int centerX = location.getBlockX();
        int centerY = location.getBlockY()-1;
        int centerZ = location.getBlockZ();

        for (int x = centerX - 3; x <= centerX + 3; x++) {
            for (int z = centerZ - 3; z <= centerZ + 3; z++) {
                Block block = world.getBlockAt(x, centerY, z);
                if (!block.getLocation().equals(location)) {
                    if (canBreakBlock(player, block.getLocation())) {
                        surroundingBlocks.add(block);
                    }
                }
            }
        }
        return surroundingBlocks;
    }

    public void addToBackpack(Player player, Material mat, Integer amount) {
        this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, mat, amount);
    }

    public Boolean hasMsgsEnabled(Player player) {
        return Boolean.valueOf(PlaceholderAPI.setPlaceholders(player, "%prefs-enchMsgs%"));
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

    public boolean canBreakBlock(Player player, Location l) {
        Plugin plugin = WorldGuardPlugin.inst();
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(l);

        for (ProtectedRegion region : regions) {
            Flag<?> blockBreakFlag = DefaultFlag.BLOCK_BREAK;
            Object flagValue = region.getFlag(blockBreakFlag);

            if (regions.testState(localPlayer, DefaultFlag.BLOCK_BREAK)) {
                return true;
            }
        }

        return false;
    }
}
