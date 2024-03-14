package me.couph.grizzlyenchants.Commands;

import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Tokens.PickaxeGUI;
import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlytools.GrizzlyTools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Enchanter implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player)sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().isGrizzlyPickaxe(item)) {
            PickaxeGUI pickaxeGUI = new PickaxeGUI(GrizzlyEnchants.getInstance());
            pickaxeGUI.openGUI(player, player.getInventory().getItemInMainHand());
        } else {
            player.sendMessage(CouphUtil.color("&c(!) Please hold the pickaxe you wish to enchant!"));
        }
        return true;
    }
}
