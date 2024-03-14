package me.couph.grizzlyenchants.util;

import org.bukkit.plugin.java.JavaPlugin;

public class SimpleCommandManager {
    private JavaPlugin plugin;

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public SimpleCommandManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {}
}
