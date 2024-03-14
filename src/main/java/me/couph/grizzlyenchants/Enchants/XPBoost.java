package me.couph.grizzlyenchants.Enchants;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Random;

public class XPBoost extends Enchant implements Listener {

    private ArrayList<Integer> notAllowed;

    public XPBoost() {
        super("XP Boost", ChatColor.YELLOW);
        setAbility("XP Boost");
        setAbilityDescription("Chance to receive 1-3 extra pickaxe EXP when breaking blocks.");
        setLevelUpInfo("increase the chance of gaining extra EXP.");
        setMaxLevel(100);
        setRequiredLevel(3);
        notAllowed = generateNonAllowed();
    }

    public Enchant getEnchant() {
        return this;
    }


    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        if (!(canBreakBlock(event.getPlayer(), event.getBlock()))) return;
        if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(event.getPlayer().getInventory().getItemInMainHand()) == null)
            return;
        GrizzlyPickaxe pickaxe = (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(event.getPlayer().getInventory().getItemInMainHand()));
        if (this.getPlugin().getGrizzlyEnchantHandler().itemHasEnchant(event.getPlayer().getInventory().getItemInMainHand(), this)) {
            if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(event.getPlayer().getInventory().getItemInMainHand())) {
                if (CouphUtil.colorAndStrip(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).contains("Explosive")) return;
                Random rand = new Random();
                int level = this.getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), this);
                int procChance = 105;
                int proc = rand.nextInt(procChance - level);
                int XP = rand.nextInt(3)+1;
                if (proc == 1) {
                    try {
                        int pickaxeExp = pickaxe.getEXP();
                        if (!(notAllowed.contains(pickaxeExp))) {
                            for (int x = 0; x < XP; x++) {
                                ItemMeta newMeta = pickaxe.incrementEXP(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
                                event.getPlayer().getInventory().getItemInMainHand().setItemMeta(newMeta);
                            }
                        }
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        }
    }

    public static ArrayList<Integer> generateNonAllowed() {
        ArrayList<Integer> notAllowed = new ArrayList<>();
        notAllowed.add(997);
        notAllowed.add(998);
        notAllowed.add(999);
        notAllowed.add(1000);
        notAllowed.add(1997);
        notAllowed.add(1998);
        notAllowed.add(1999);
        notAllowed.add(2000);
        notAllowed.add(3997);
        notAllowed.add(3998);
        notAllowed.add(3999);
        notAllowed.add(4000);
        notAllowed.add(7497);
        notAllowed.add(7498);
        notAllowed.add(7499);
        notAllowed.add(7500);
        notAllowed.add(9997);
        notAllowed.add(9998);
        notAllowed.add(9999);
        notAllowed.add(10000);
        return notAllowed;

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
}