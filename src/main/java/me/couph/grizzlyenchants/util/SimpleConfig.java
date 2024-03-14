package me.couph.grizzlyenchants.util;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class SimpleConfig {
    private String name;

    private File file;

    private YamlConfiguration config;

    private SimpleConfigManager manager;

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return this.file;
    }

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public SimpleConfigManager getManager() {
        return this.manager;
    }

    public SimpleConfig(String name, File file, YamlConfiguration config, SimpleConfigManager manager) {
        this.name = name;
        this.file = file;
        this.config = config;
        this.manager = manager;
        manager.getConfigs().add(this);
    }

    public void setup() {
        try {
            if (!this.file.exists())
                this.file.createNewFile();
            this.config = YamlConfiguration.loadConfiguration(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (Exception exception) {}
    }

    public void saveDefaultConfig() {
        if (this.file == null)
            this.file = new File(this.manager.getPlugin().getDataFolder(), this.name + ".yml");
        if (!this.file.exists())
            this.manager.getPlugin().saveResource(this.name + ".yml", false);
    }
}

