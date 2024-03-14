package me.couph.grizzlyenchants.Enchants;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.PlaceholderAPI;
import me.couph.grizzlybackpacks.util.CouphUtil;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Treasure extends Enchant implements Listener {

    private FileConfiguration config;

    public Treasure(FileConfiguration config) {
        super("Treasure", ChatColor.DARK_GREEN);
        setAbility("Treasure");
        setAbilityDescription("Chance to find bonus loot whilst mining.");
        setLevelUpInfo("increase the chance of finding loot.");
        setMaxLevel(100);
        this.config = config;
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
                int level = this.getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), this);
                String command = getRandomLoot(event.getPlayer());
                command = command.replace("{player}", event.getPlayer().getName());
                int num = getOdds(event.getPlayer(), level);
                if (num==1) {
                    if (hasMsgsEnabled(event.getPlayer())) {
                        event.getPlayer().sendMessage(CouphUtil.color("&a&ki&r &2&lTreasure: &aYou found some loot! &a&ki&r"));
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
        }
    }

    public int getOdds(Player player, int enchLvl) {
        Random rand = new Random();
        try {
            String aff = PlaceholderAPI.setPlaceholders(player, "%AFF%");
            if (aff.equalsIgnoreCase("true") && CouphUtil.colorAndStrip(player.getInventory().getItemInMainHand().getItemMeta().getDisplayName()).contains("Explosive")) {
                return (rand.nextInt(500 - enchLvl));
            }
        } catch (Exception ignored) {}
        return rand.nextInt(300 - enchLvl*2);
    }

    public Boolean hasMsgsEnabled(Player player) {
        return Boolean.valueOf(PlaceholderAPI.setPlaceholders(player, "%prefs-enchMsgs%"));
    }

    public String getRandomLoot(Player player) {
        HashMap<Integer, String> rewardPool = new HashMap<>();
        ConfigurationSection treasureConfig = this.config.getConfigurationSection("treasure");
        if (treasureConfig == null) return null;
        int count=0;

        Random rand = new Random();
        int num = rand.nextInt(100);

        String MM = PlaceholderAPI.setPlaceholders(player, "%PIR%");
        if (MM.equalsIgnoreCase("true")) {
            String petLevel = PlaceholderAPI.setPlaceholders(player, "%PIRLevel%");
            if (num<=69) {
                if (petLevel.equalsIgnoreCase("0")) {
                    num += rand.nextInt(10);
                }
                if (petLevel.equalsIgnoreCase("1")) {
                    num += rand.nextInt(13);
                }
                if (petLevel.equalsIgnoreCase("2")) {
                    num += rand.nextInt(15);
                }
                if (petLevel.equalsIgnoreCase("3")) {
                    num += rand.nextInt(20);
                }
                if (petLevel.equalsIgnoreCase("4")) {
                    num += rand.nextInt(25);
                }
                if (petLevel.equalsIgnoreCase("5")) {
                    num += rand.nextInt(30);
                }
            }
        }


        for (String key : treasureConfig.getKeys(false)) {
            ConfigurationSection currReward = treasureConfig.getConfigurationSection(key);
            if (currReward == null) return null;

            String name = currReward.getName();
            String command = currReward.getString("command");
            Integer chance = currReward.getInt("chance");
            for (int x=0; x<chance; x++) {
                rewardPool.put(count, command);
                count++;
            }
        }
        return rewardPool.get(num);
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