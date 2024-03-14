package me.couph.grizzlyenchants.Enchants;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class Bounty extends Enchant implements Listener {

    // register ench
    // if space in name add twice in ench handler

    public Bounty() {
        super("Bounty", ChatColor.BLUE);
        setAbility("Bounty");
        setAbilityDescription("Earn extra money from selling your backpack.");
        setLevelUpInfo("increase the amount of extra money you gain.");
        setMaxLevel(1000);
        setRequiredLevel(50);
    }

    public Enchant getEnchant() {
        return this;
    }


    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
    }
}