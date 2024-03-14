package me.couph.grizzlyenchants.Tokens;

import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.EnchantBlackscroll;
import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlyenchants.util.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class WithdrawTokens extends SimpleCommand implements Listener {
    public WithdrawTokens() {
        super("withdrawtokens", "tokens.withdraw", "Withdraw Tokens", "/withdrawtokens",
                Boolean.valueOf(false), Integer.valueOf(0), (JavaPlugin) GrizzlyEnchants.getInstance());
        Bukkit.getPluginManager().registerEvents(this, (Plugin) getPlugin());
    }

    public boolean execute(CommandSender sender, String[] args) {
        GrizzlyEnchants plugin = (GrizzlyEnchants) getPlugin();
        try {
            if (args.length == 0) {
                sender.sendMessage(CouphUtil.color("&c(!) Usage: /withdrawtokens <amount>"));
                return true;
            }
            Player player = (Player) sender;
            int amount = Integer.parseInt(args[0]);
            long tokenCount = plugin.tokenHandler.getTokens(player);
            if (amount < 1) return true;
            if (amount <= tokenCount) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tokens give shard " + player.getName() + " " + amount);
                plugin.tokenHandler.removeTokens(player, amount);
                player.sendMessage(CouphUtil.color("&c(!) &c&l-" + amount + " Tokens"));
            } else {
                player.sendMessage(CouphUtil.color("&c(!) &c&lYou do not have enough tokens!"));
            }
        } catch (Exception e) {
            sender.sendMessage(CouphUtil.color("&c(!) Usage: /withdrawtokens <amount>"));
            return true;
        }
        return true;
    }
}

