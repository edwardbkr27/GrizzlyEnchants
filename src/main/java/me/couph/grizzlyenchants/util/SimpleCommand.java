package me.couph.grizzlyenchants.util;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public abstract class SimpleCommand implements CommandExecutor, TabCompleter {
    public static List<SimpleCommand> getCommands() {
        return commands;
    }

    private static final List<SimpleCommand> commands = Lists.newArrayList();

    private String name;

    private String permission;

    private String description;

    private String usage;

    private Integer minimumArgsRequired;

    private Boolean playersOnly;

    public String getName() {
        return this.name;
    }

    public String getPermission() {
        return this.permission;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUsage() {
        return this.usage;
    }

    public Integer getMinimumArgsRequired() {
        return this.minimumArgsRequired;
    }

    public Boolean getPlayersOnly() {
        return this.playersOnly;
    }

    private Map<Integer, List<String>> tabComplete = new HashMap<>();

    private JavaPlugin plugin;

    public Map<Integer, List<String>> getTabComplete() {
        return this.tabComplete;
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    public SimpleCommand(String name, String permission, String description, String usage, Boolean playersOnly, Integer minimumArgsRequired, JavaPlugin plugin) {
        this.name = name;
        this.permission = permission;
        this.description = description;
        this.usage = usage;
        this.playersOnly = playersOnly;
        this.minimumArgsRequired = minimumArgsRequired;
        this.plugin = plugin;
        try {
            getPlugin().getCommand(name).setExecutor(this);
        } catch (Exception e) {
            CouphUtil.log("Could not register command '" + name + "' since it isn't in the plugin.yml for " +
                    getPlugin() + ".", CouphUtil.LogLevel.ERROR);
        }
        commands.add(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (this.permission != null && !sender.hasPermission(this.permission)) {
            sender.sendMessage(CouphUtil.color("&c&l(!)&c You don't have permission to execute this command."));
            return true;
        }
        if (this.playersOnly.booleanValue() && !(sender instanceof org.bukkit.entity.Player)) {
            sender.sendMessage(CouphUtil.color("&cOnly players are allowed to use this command."));
            return true;
        }
        if (args.length < this.minimumArgsRequired.intValue()) {
            sendUsageFormatted(sender);
            return true;
        }
        execute(sender, args);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (hasPermission() && !sender.hasPermission(this.permission))
            return null;
        if (this.playersOnly.booleanValue() && !(sender instanceof org.bukkit.entity.Player))
            return null;
        for (Iterator<Integer> iterator = this.tabComplete.keySet().iterator(); iterator.hasNext(); ) {
            int i = ((Integer)iterator.next()).intValue();
            if (args.length == i + 1) {
                if (args[i].equals(""))
                    return this.tabComplete.get(Integer.valueOf(i));
                ArrayList<String> results = new ArrayList<>();
                for (String result : this.tabComplete.get(Integer.valueOf(i))) {
                    if (result.toLowerCase().startsWith(args[i]))
                        results.add(result);
                }
                Collections.sort(results);
                if (!results.isEmpty())
                    return results;
            }
        }
        return null;
    }

    private void sendUsageFormatted(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + this.usage);
        sender.sendMessage(ChatColor.RED + "/" + getName() + ChatColor.GRAY + " - " + this.description);
    }

    private boolean hasPermission() {
        return (this.permission != null);
    }

    protected void setAutoComplete(int arg, List<String> results) {
        this.tabComplete.put(Integer.valueOf(arg), results);
    }

    public abstract boolean execute(CommandSender paramCommandSender, String[] paramArrayOfString);
}
