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
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

import static me.couph.grizzlytools.GrizzlyTools.grizzlyBackpacks;

// register enchant
// modify applyEnchant if enchant has space in the name


public class DragonsBreath extends Enchant implements Listener {
    private Block lastBroken;

    public DragonsBreath() {
        super("Dragons Breath", ChatColor.RED);
        setAbility("Dragons Breath");
        setAbilityDescription("Chance to spawn a dragon above you causing mass destruction in your mine.");
        setLevelUpInfo("increase the amount of blocks gained per proc.");
        setMaxLevel(30);
        setRequiredLevel(10);
    }

    public Enchant getEnchant() {
        return this;
    }


    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        if (!(canBreakBlock(event.getPlayer(), event.getBlock()))) return;
        Material mat = event.getBlock().getType();
        if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(event.getPlayer().getInventory().getItemInMainHand()) == null) return;
        try {
            if (this.getPlugin().getGrizzlyEnchantHandler().itemHasEnchant(event.getPlayer().getInventory().getItemInMainHand(), this)) {
                Random rand = new Random();
                int bound = 500;
                if (CouphUtil.colorAndStrip(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).contains("Dream")) bound=bound/3;
                int num = rand.nextInt(bound);
                int level = this.getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), this);
                if (num == 1) {
                    Player player = event.getPlayer();
                    CouphUtil.playSound(player, Sound.ENTITY_ENDERDRAGON_FLAP, 0.5F);
                    givePlayerBlocks(player, level, mat);
                }
            }
        } catch(Exception e) {
            return;
        }
    }

    public void simulateExplosion(Player player, double radius) {
        player.spawnParticle(Particle.FLAME, player.getLocation(), 10);
    }

    public void givePlayerBlocks(Player player, Integer level, Material mat) {
        // Make it so that an "explosion" happens around the player
        // Using bedrock as border
        // with worldguard integration (would need for later ench anyway)
        // check item is a valid mining item
        simulateExplosion(player, 5.0);
        Random rand = new Random();
        int num = rand.nextInt(level+1);
        if (num==0) num++;
        //ItemStack item = new ItemStack(convertOreToMineral(mat), (Integer)(num*100));
        if (mat != Material.BEDROCK) {
            //player.sendMessage(String.valueOf(item));
            addToBackpack(player, convertOreToMineral(mat), (Integer)(num*100));
            //player.getInventory().addItem(item);
        }
    }

    public void addToBackpack(Player player, Material mat, Integer amount) {
        this.getPlugin().grizzlyBackpacks.getGrizzlyBackpackHandler().addBlock(player, mat, amount);
        if (hasMsgsEnabled(player)) {
            player.sendMessage(CouphUtil.color("&4&kii&r &6&l(!)&r &c&lDragon's Breath: DESTROYED &4&l" + amount.toString() + " &c&lBLOCKS &6&l(!) &4&kii&r"));
        }
    }


    public boolean canBreakBlock(Player player, Block block) {
        if (player.isOp()) return true;
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
}
