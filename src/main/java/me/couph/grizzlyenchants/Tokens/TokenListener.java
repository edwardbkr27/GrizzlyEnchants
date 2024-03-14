package me.couph.grizzlyenchants.Tokens;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.util.CouphUtil;
import me.couph.grizzlytools.util.PickaxeHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class TokenListener implements Listener {
    private GrizzlyEnchants plugin;
    private PickaxeHandler grizzlyPickaxeHandler;

    public TokenListener(GrizzlyEnchants plugin) {
        this.plugin = plugin;
        this.grizzlyPickaxeHandler = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        try {
            Random rand = new Random();
            int num = rand.nextInt(30);
            if (hasAffinity(event.getPlayer())) num = rand.nextInt(5);
            if (event.getPlayer().getInventory().getItemInMainHand().hasItemMeta()) {
                if (CouphUtil.colorAndStrip(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName()).contains("Booster"))
                    num = num * 3;
            }
            if (canBreakBlock(event.getPlayer(), event.getBlock())) {
                this.plugin.tokenHandler.addTokens(event.getPlayer(), (long)num * getTokenBoost(event.getPlayer()));
            }
        } catch (Exception e) {
            return;
        }
    }

    public Boolean hasAffinity(Player player) {
        return Boolean.valueOf(PlaceholderAPI.setPlaceholders(player, "%AFF%"));
    }

    public int getTokenBoost(Player player) {
        String rank = PlaceholderAPI.setPlaceholders(player, "%luckperms_highest_group_by_weight%");
        if (rank.equalsIgnoreCase("default")) return 1;
        if (rank.equalsIgnoreCase("hustler")) return 1;
        if (rank.equalsIgnoreCase("officer")) return 1;
        if (rank.equalsIgnoreCase("vigilante")) return 2;
        if (rank.equalsIgnoreCase("kingpin")) return 2;
        if (rank.equalsIgnoreCase("grizzly")) return 3;
        if (rank.equalsIgnoreCase("grizzlyplus")) return 3;
        if (rank.equalsIgnoreCase("warden")) return 5;
        if (rank.equalsIgnoreCase("partner")) return 5;
        if (rank.equalsIgnoreCase("helper")) return 5;
        if (rank.equalsIgnoreCase("jrmod")) return 5;
        if (rank.equalsIgnoreCase("mod")) return 5;
        if (rank.equalsIgnoreCase("srmod")) return 5;
        if (rank.equalsIgnoreCase("jradmin")) return 5;
        if (rank.equalsIgnoreCase("admin")) return 5;
        if (rank.equalsIgnoreCase("owner")) return 5;
        return 1;
    }


    @EventHandler
    public void shopClick(InventoryClickEvent event) {
        // get id
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
