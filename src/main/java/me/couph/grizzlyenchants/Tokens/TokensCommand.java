package me.couph.grizzlyenchants.Tokens;

import me.couph.grizzlybackpacks.util.CouphUtil;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.util.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class TokensCommand extends SimpleCommand implements Listener {
    public TokensCommand() {
        super("token", "tokens.admin", "Tokens Command.", "/token",
                Boolean.valueOf(false), Integer.valueOf(0), (JavaPlugin) GrizzlyEnchants.getInstance());
        Bukkit.getPluginManager().registerEvents(this, (Plugin) getPlugin());
    }

    public boolean execute(CommandSender sender, String[] args) {
        GrizzlyEnchants plugin = (GrizzlyEnchants) getPlugin();
        try {
            if (args[0].equalsIgnoreCase("give")) {
                if (args[1].equalsIgnoreCase("shard")) {
                    Player player = Bukkit.getPlayer(args[2]);
                    long amount = Long.parseLong(args[3]);
                    TokenShard tokenShard = new TokenShard(plugin);
                    ItemStack item = tokenShard.createItem(amount);
                    player.getInventory().addItem(item);
                    player.sendMessage(CouphUtil.color("&a(!) You were given a token shard!"));
                } else {
                    Player player = Bukkit.getPlayer(args[1]);
                    long amount = Long.parseLong(args[2]);
                    if (amount == 0) return true;
                    plugin.tokenHandler.addTokens(player, amount);
                    sender.sendMessage(CouphUtil.color("&aYou gave " + player.getName() + " " + amount + " tokens."));
                    player.sendMessage(CouphUtil.color("&a(!) &a&l+" + amount + " Tokens"));
                }
            }
        } catch (Exception e) {
            sender.sendMessage(CouphUtil.color("&cUsage: /tokens give (shard(optional)) <player> <amount>"));
            e.printStackTrace();
            return false;

        }
        return false;
    }
}
