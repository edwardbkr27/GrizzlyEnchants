package me.couph.grizzlyenchants.Tokens;

import me.couph.grizzlybackpacks.util.CouphUtil;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.EnchantBlackscroll;
import me.couph.grizzlyenchants.util.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class TokenShopCommand extends SimpleCommand implements Listener {
    public TokenShopCommand() {
        super("tokenshop", "tokenshop.use", "Open Token Shop.", "/tokenshop",
                Boolean.valueOf(false), Integer.valueOf(0), (JavaPlugin) GrizzlyEnchants.getInstance());
        Bukkit.getPluginManager().registerEvents(this, (Plugin) getPlugin());
    }

    public boolean execute(CommandSender sender, String[] args) {
        GrizzlyEnchants plugin = (GrizzlyEnchants) getPlugin();
            if (!(sender instanceof Player)) {
                sender.sendMessage(CouphUtil.color("&c(!) Only players can use this command!"));
            }
            TokenShop tokenShop = new TokenShop(plugin);
            tokenShop.openGui((Player) sender);
            return true;
    }
}

