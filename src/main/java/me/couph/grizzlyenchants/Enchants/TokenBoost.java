package me.couph.grizzlyenchants.Enchants;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class TokenBoost extends Enchant implements Listener {

    // register ench
    // if space in name add twice in ench handler

    public TokenBoost() {
        super("Token Boost", ChatColor.DARK_AQUA);
        setAbility("Token Boost");
        setAbilityDescription("Chance to receive 1-3 extra tokens when breaking blocks.");
        setLevelUpInfo("increase the chance of gaining extra tokens.");
        setMaxLevel(500);
        setRequiredLevel(3);
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
                Random rand = new Random();
                int level = this.getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), this);
                int proc = rand.nextInt(8);
                int bound = level/8;
                if (bound < 1) {
                    bound = 1;
                }
                int amount = rand.nextInt(bound)+1;
                if (proc == 1) {
                    this.getPlugin().tokenHandler.addTokens(event.getPlayer(), (long)amount);
                }
            }
        }
    }

    public Boolean hasAffinity(Player player) {
        return Boolean.valueOf(PlaceholderAPI.setPlaceholders(player, "%AFF%"));
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