package me.couph.grizzlyenchants.Commands;

import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.util.SimpleCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantGuiCommand extends SimpleCommand implements Listener {
    public EnchantGuiCommand() {
        super("enchants", "grizzlyenchants.gui", "Grizzly enchants gui command.", "/enchants",
                Boolean.valueOf(false), Integer.valueOf(0), (JavaPlugin) GrizzlyEnchants.getInstance());
        Bukkit.getPluginManager().registerEvents(this, (Plugin) getPlugin());
    }

    public boolean execute(CommandSender sender, String[] args) {
        GrizzlyEnchants plugin = (GrizzlyEnchants) getPlugin();
        if (!(sender instanceof Player)) return false;
        ((GrizzlyEnchants) getPlugin()).getGrizzlyEnchantHandler().getGui((Player) sender);
        return true;
    }
}
