package me.couph.grizzlyenchants.Commands;

import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.util.Tinkerer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class TinkererCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Tinkerer tinkerer = new Tinkerer((Player)sender);
        Bukkit.getPluginManager().registerEvents(tinkerer, GrizzlyEnchants.getInstance());
        GrizzlyEnchants.getInstance().activeTinkerers.put((Player)sender, tinkerer);
        GrizzlyEnchants.getInstance().activeTinkerers.get((Player)sender).openGui((Player)sender);
        return true;
    }
}
