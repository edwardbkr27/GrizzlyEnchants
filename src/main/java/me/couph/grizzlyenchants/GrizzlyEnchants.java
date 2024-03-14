package me.couph.grizzlyenchants;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import jdk.nashorn.internal.parser.Token;
import me.couph.grizzlybackpacks.GrizzlyBackpacks;
import me.couph.grizzlyenchants.Commands.*;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlyenchants.Items.EnchantBlackscroll;
import me.couph.grizzlyenchants.Listeners.EnchantListener;
import me.couph.grizzlyenchants.Tokens.*;
import me.couph.grizzlyenchants.util.EnchantHandler;
import me.couph.grizzlyenchants.util.GuiUtil;
import me.couph.grizzlyenchants.util.RecipeHandler;
import me.couph.grizzlyenchants.util.Tinkerer;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import me.couph.grizzlytools.Listeners.GrizzlyPickaxeListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public final class GrizzlyEnchants extends JavaPlugin implements Listener {


    private static GrizzlyEnchants instance;
    public static GrizzlyEnchants getInstance() {
        return instance;
    }
    private EnchantHandler grizzlyEnchantHandler;
    public EnchantHandler getGrizzlyEnchantHandler() {
        return this.grizzlyEnchantHandler;
    }

    private static Economy economy = null;

    public static Economy getEcon() {
        return economy;
    }

    public static WorldGuardPlugin worldGuard;

    public GrizzlyBackpacks grizzlyBackpacks;

    public TokenManager tokenHandler;

    public HashMap<Player, Tinkerer> activeTinkerers;

    public HashMap<Player, Listener> tinkerListeners;
    public static WorldEditPlugin worldEdit;

    public void onEnable() {
        instance = this;
        this.activeTinkerers = new HashMap<>();
        this.tinkerListeners = new HashMap<>();
        new EnchantCommand();
        new EnchantGuiCommand();
        new TokenShopCommand();
        new TokensCommand();
        new WithdrawTokens();
        this.grizzlyEnchantHandler = new EnchantHandler(this);
        this.tokenHandler = new TokenManager(this);
        new TokenPlaceholderHook(this).register();
        this.tokenHandler.saveTokenMapBackup();
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        //getServer().getPluginManager().registerEvents((Listener)new Tinkerer(null), this);
        getServer().getPluginManager().registerEvents((Listener)new EnchantListener(this), (Plugin)this);
        getServer().getPluginManager().registerEvents(this, (Plugin)this);
        getServer().getPluginManager().registerEvents((Listener)new EnchantBlackscroll(this), (Plugin)this);
        getServer().getPluginManager().registerEvents((Listener)new GuiUtil(this), (Plugin)this);
        getServer().getPluginManager().registerEvents((Listener)new TokenListener(this), (Plugin)this);
        getServer().getPluginManager().registerEvents((Listener)new TokenShop(this), (Plugin)this);
        getServer().getPluginManager().registerEvents((Listener)new PickaxeGUI(this), (Plugin)this);
        getServer().getPluginManager().registerEvents((Listener)new TokenShard(this), (Plugin)this);
        getServer().getPluginManager().registerEvents((Listener)new RecipeHandler(this), (Plugin)this);
        getCommand("Top").setExecutor(new Top(this));
        getCommand("Tinkerer").setExecutor(new TinkererCmd());
        getCommand("Enchanter").setExecutor(new Enchanter());
        // Needs modifying
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Enchant grizzlyEnchant : getGrizzlyEnchantHandler().getGrizzlyEnchants()) {
                if (grizzlyEnchant.isGrizzlyEnchant(player.getInventory().getItemInMainHand()))
                    getGrizzlyEnchantHandler().setActiveEnchant(player, grizzlyEnchant);
            }
        }
        worldGuard = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null) {
            getLogger().severe("WorldGuard plugin not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        grizzlyBackpacks = (GrizzlyBackpacks) getServer().getPluginManager().getPlugin("GrizzlyBackpacks");
        if (grizzlyBackpacks == null) {
            getLogger().severe("GrizzlyBackpacks plugin not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            getLogger().severe("WorldEdit plugin not found! Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (!setupEconomy() ) {
            System.out.println("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    public void addTinkererListener(Player sender) {
    }

    public static Economy getEconomy() {
        return economy;
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }


    public void reloadAll() {
        reloadConfig();
        this.grizzlyEnchantHandler.registerGrizzlyEnchants();
    }

    public void onDisable() {
        this.grizzlyEnchantHandler.getEnchantStatusManager().save();
        this.tokenHandler.saveTokenMap();
    }
}
