package me.couph.grizzlyenchants.util;

import com.google.common.collect.Sets;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Set;

public class SimpleConfigManager {
    private JavaPlugin plugin;

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    private Set<SimpleConfig> configs = Sets.newHashSet();

    public Set<SimpleConfig> getConfigs() {
        return this.configs;
    }

    public SimpleConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdir();
    }

    public SimpleConfig getConfig(String name) {
        return this.configs.stream().filter(config -> config.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public SimpleConfig newConfig(String name, boolean fromDefaults) {
        if (fromDefaults) {
            File file1 = new File(this.plugin.getDataFolder(), name + ".yml");
            SimpleConfig simpleConfig1 = new SimpleConfig(name, file1, YamlConfiguration.loadConfiguration(file1), this);
            simpleConfig1.saveDefaultConfig();
            return simpleConfig1;
        }
        File file = new File(this.plugin.getDataFolder(), name + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        SimpleConfig simpleConfig = null;
        try {
            simpleConfig = new SimpleConfig(name, file, config, this);
            simpleConfig.setup();
        } catch (Exception e) {
            CouphUtil.log("Error whilst creating the file '" + name + ".yml'", CouphUtil.LogLevel.ERROR);
            e.printStackTrace();
        }
        return simpleConfig;
    }
}
