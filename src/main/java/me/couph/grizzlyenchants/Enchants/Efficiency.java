package me.couph.grizzlyenchants.Enchants;

import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.Enchant;
import org.bukkit.ChatColor;

public class Efficiency extends Enchant {
    public Efficiency() {
        super("Efficiency", ChatColor.GRAY);
        setAbility("Efficiency");
        setAbilityDescription("Increase the speed you break blocks.");
        setLevelUpInfo("increase the speed in which you break blocks.");
        setMaxLevel(200);
        setRequiredLevel(1);
    }

    public Enchant getEnchant() {
        return this;
    }


}
