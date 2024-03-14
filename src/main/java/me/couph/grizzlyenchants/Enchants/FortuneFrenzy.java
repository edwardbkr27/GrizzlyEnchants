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
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.Random;

public class FortuneFrenzy extends Enchant implements Listener {

    private static String TIMER_ID = "sf";
    public FortuneFrenzy() {
        super("Fortune Frenzy", ChatColor.GREEN);
        setAbility("Fortune Frenzy");
        setAbilityDescription("Chance to double your autosell earnings for a short period of time.");
        setLevelUpInfo("increase the proc rate.");
        setMaxLevel(10);
        setRequiredLevel(20);
    }

    public Enchant getEnchant() {
        return this;
    }


    @EventHandler
    public void blockBreakEvent(BlockBreakEvent event) {
        if (!(canBreakBlock(event.getPlayer(), event.getBlock()))) return;
        if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(event.getPlayer().getInventory().getItemInMainHand()) == null) return;
        if (!this.getPlugin().getGrizzlyEnchantHandler().itemHasEnchant(event.getPlayer().getInventory().getItemInMainHand(), this.getPlugin().getGrizzlyEnchantHandler().getByName("Autosell"))) return;
        if (this.getPlugin().getGrizzlyEnchantHandler().itemHasEnchant(event.getPlayer().getInventory().getItemInMainHand(), this)) {
            Random rand = new Random();
            int level = this.getPlugin().getGrizzlyEnchantHandler().getEnchantLevel(event.getPlayer().getInventory().getItemInMainHand(), this);
            int num = rand.nextInt(450-(level*10));
            if (num == 1) {
                if (Autosell.getTimerMap().isOnTimer(event.getPlayer(), TIMER_ID)) {
                    return;
                }
                Autosell.getTimerMap().add(event.getPlayer(), 10000L, TIMER_ID);
                CouphUtil.playSound(event.getPlayer(), Sound.ENTITY_BLAZE_HURT, 0.3F);
                if (hasMsgsEnabled(event.getPlayer())) {
                    event.getPlayer().sendMessage(CouphUtil.color("&2&kii&r &a&lFORTUNE FRENZY: &2&l2x BLOCKS FOR &a&l10s&r &2&kii&r"));
                }
            }
        } else {
            if (Autosell.getTimerMap().isOnTimer(event.getPlayer(), TIMER_ID)) {
                Autosell.getTimerMap().remove(event.getPlayer(), TIMER_ID);
                if (hasMsgsEnabled(event.getPlayer())) {
                    event.getPlayer().sendMessage(CouphUtil.color("&2&kii&r &a&lFORTUNE FRENZY: &2&lCANCELLED&r &2&kii&r"));
                }
            }
        }
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
}
