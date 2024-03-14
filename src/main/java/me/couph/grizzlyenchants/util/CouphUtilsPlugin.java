package me.couph.grizzlyenchants.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class CouphUtilsPlugin extends JavaPlugin {
    private SimpleConfigManager configManager;

    private SimpleCommandManager commandManager;

    public SimpleConfigManager getConfigManager() {
        return this.configManager;
    }

    public SimpleCommandManager getCommandManager() {
        return this.commandManager;
    }

    public void onEnable() {
        this.configManager = new SimpleConfigManager(this);
        this.commandManager = new SimpleCommandManager(this);
        getConfigManager().newConfig("info", false).save();
        getCommandManager().registerCommands();
        new SimpleInventoryListener(this);
        String[] messages = { "&a--------------------------------------", "        &a&lENABLED&7 " + this, "&a--------------------------------------" };
        Bukkit.getConsoleSender().sendMessage((String[])CouphUtil.color(Arrays.asList(messages)).toArray((Object[])new String[messages.length]));
    }

    public void onDisable() {
        SimpleConfig config = getConfigManager().getConfig("info");
        config.getConfig().set("plugin-disables." + (System.currentTimeMillis() / 10000L) + ".time", Long.valueOf(System.currentTimeMillis()));
        config.getConfig().set("plugin-disables." + (System.currentTimeMillis() / 10000L) + ".reason", "unknown");
        config.save();
    }
}
