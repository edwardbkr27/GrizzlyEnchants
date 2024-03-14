package me.couph.grizzlyenchants.Commands;

import me.couph.grizzlyenchants.Items.EnchantBlackscroll;
import me.couph.grizzlytools.GrizzlyTools;
import me.couph.grizzlytools.Items.GrizzlyPickaxe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlyenchants.util.MoreUtil;
import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlyenchants.util.SimpleCommand;

public class EnchantCommand extends SimpleCommand implements Listener {
        public EnchantCommand() {
            super("ge", "grizzlyenchants.admin", "Grizzly enchants command.", "/ge",
                    Boolean.valueOf(false), Integer.valueOf(0), (JavaPlugin) GrizzlyEnchants.getInstance());
            Bukkit.getPluginManager().registerEvents(this, (Plugin) getPlugin());
        }

        public boolean execute(CommandSender sender, String[] args) {
            GrizzlyEnchants plugin = (GrizzlyEnchants) getPlugin();
            EnchantBlackscroll blackscroll = new EnchantBlackscroll(plugin);
            if (args.length == 0) {
                sendHelp(sender);
                return false;
            }
            if (args[0].equalsIgnoreCase("config")) {
                if (args.length >= 2 && args[1].equalsIgnoreCase("reload")) {
                    //plugin.getConfigManager().reload();
                    MoreUtil.sendMessage(sender, "&a&l(&f!&a&l) &aReloaded configuration.");
                    return false;
                }
                if (sender instanceof Player)
                    //plugin.getConfigManager().getGUI().open((Player)sender);
                    return false;
            } if (args[0].equalsIgnoreCase("add")) {
                try {
                    int level = Integer.parseInt(args[2]);
                    Player player = sender.getServer().getPlayer(sender.getName());
                    ItemStack item = player.getInventory().getItemInMainHand();
                    GrizzlyPickaxe pickaxe = GrizzlyTools.getInstance().getGrizzlyPickaxeHandler().getByItem(item);
                    if (pickaxe == null) {
                        throw new Exception("Item is not special pickaxe.");
                    }
                    if (args[1].contains("-")) args[1] = args[1].replace("-", " ");
                    Enchant enchant = plugin.getGrizzlyEnchantHandler().getByName(args[1]);
                    for (int x = 0; x < level; x++) item.setItemMeta(plugin.getGrizzlyEnchantHandler().applyEnchant(player, item, enchant));
                    return true;
                } catch (Exception e) {
                    sender.sendMessage(CouphUtil.color("&c(!) Usage: /ge add <enchant> <level> whilst holding a pickaxe."));
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("give")) {
                try {
                    if (args.length < 2) {
                        sender.sendMessage(CouphUtil.color("&c(!) Usage: /ge give <player> <enchant/blackscroll>"));
                        return false;
                    }
                    Player player = Bukkit.getPlayer(args[1]);
                    if (args[2].equalsIgnoreCase("blackscroll")) {
                        ItemStack item = blackscroll.createBlackscroll();
                        if (!(CouphUtil.isInventoryFull(player))) {
                            player.getInventory().addItem(item);
                            sender.sendMessage(CouphUtil.color("&a&l(!)&a You have given a " + args[2] + " to " + player.getName()));
                            player.sendMessage(CouphUtil.color("&a&l(!)&a You have been given a " + args[2] + " by " + sender.getName()));
                            return false;
                        } else {
                            player.sendMessage(CouphUtil.color("&c(!) Players inventory is full!"));
                            return false;
                        }
                    }
                    String enchName = args[2];
                    if (enchName.contains("-")) enchName = args[2].replace("-", " ");
                    Enchant grizzlyEnchant = plugin.getGrizzlyEnchantHandler().getByName(enchName);
                    if (player == null) {
                        sender.sendMessage(CouphUtil.color("&c&l(!)&c Player not found, &7[" + args[1] + "]"));
                        return false;
                    }
                    if (grizzlyEnchant == null) {
                        sender.sendMessage(CouphUtil.color("&c&l(!)&c Enchant not found, &7[" + args[2] + "]"));
                        return false;
                    }
                    ItemStack item = grizzlyEnchant.createEnchant(Material.BOOK);
                    if (!CouphUtil.isInventoryFull(player)) {
                        player.getInventory().addItem(item);
                        sender.sendMessage(CouphUtil.color("&a&l(!)&a You have given a " + args[2] + " enchant to " + player.getName()));
                        player.sendMessage(CouphUtil.color("&a&l(!)&a You have been given a " + args[2] + " enchant by " + sender.getName()));
                        return false;
                    } else {
                        sender.sendMessage(CouphUtil.color("&c&l(!)&c Players inventory is full!"));
                        return false;
                    }
                } catch (Exception e) {
                    sender.sendMessage(CouphUtil.color("&c(!) Usage: /ge give <player> <enchant/blackscroll>"));
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("list")) {
                plugin.getGrizzlyEnchantHandler().getEnchantStatusManager().getListDescription().forEach(message -> MoreUtil.sendMessage(sender, message));
                return false;
            }
            if (args[0].equalsIgnoreCase("enable")) {
                if (args.length < 2) {
                    MoreUtil.sendMessage(sender, "&c&l(!) &cUsage: /enchant enable <enchant>");
                    return false;
                }
                String enchantName = args[1];
                if (enchantName.equalsIgnoreCase("*")) {
                    GrizzlyEnchants.getInstance().getGrizzlyEnchantHandler().getEnchantStatusManager().enableAll();
                    MoreUtil.sendMessage(sender, "&a&l(&f!&a&l) &aEnabled all enchants.");
                    return false;
                }
                Enchant grizzlyEnchant = GrizzlyEnchants.getInstance().getGrizzlyEnchantHandler().getByName(enchantName);
                if (grizzlyEnchant == null) {
                    MoreUtil.sendMessage(sender, "&c&l(!)&c Enchant not found, &7[" + enchantName + "]");
                    return false;
                }
                GrizzlyEnchants.getInstance().getGrizzlyEnchantHandler().getEnchantStatusManager().enable(grizzlyEnchant);
                MoreUtil.sendMessage(sender, "&a&l(&f!&a&l) &aEnabled enchant &e" + grizzlyEnchant.getName());
                return false;
            }
            if (args[0].equalsIgnoreCase("disable")) {
                if (args.length < 2) {
                    MoreUtil.sendMessage(sender, "&c&l(!) &cUsage: /enchant disable <set>");
                    return false;
                }
                String enchantName = args[1];
                if (enchantName.equalsIgnoreCase("*")) {
                    GrizzlyEnchants.getInstance().getGrizzlyEnchantHandler().getEnchantStatusManager().disableAll();
                    MoreUtil.sendMessage(sender, "&a&l(&f!&a&l) &cDisabled &aall enchants.");
                    GrizzlyEnchants.getInstance().getGrizzlyEnchantHandler().recalculateEnchantStatuses();
                    return false;
                }
                Enchant grizzlyEnchant = GrizzlyEnchants.getInstance().getGrizzlyEnchantHandler().getByName(enchantName);
                if (grizzlyEnchant == null) {
                    MoreUtil.sendMessage(sender, "&c&l(!)&c Enchant not found, &7[" + enchantName + "]");
                    return false;
                }
                GrizzlyEnchants.getInstance().getGrizzlyEnchantHandler().getEnchantStatusManager().disable(grizzlyEnchant);
                MoreUtil.sendMessage(sender, "&a&l(&f!&a&l) &cDisabled &aenchant &e" + grizzlyEnchant.getName());
                GrizzlyEnchants.getInstance().getGrizzlyEnchantHandler().recalculateEnchantStatuses();
                return false;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadAll();
                sender.sendMessage(CouphUtil.color("&e&l(!)&e You have reloaded the Enchants."));
                return false;
            }
            sendHelp(sender);
            return false;
        }

        public void sendHelp(CommandSender sender) {
            MoreUtil.sendMessage(sender, Arrays.asList(new String[]{"&3&m-----------------&b&lGrizzly&3&lEnchants&3&m-----------------", "&b/ge &8- &fMain command.", "&b/ge list &8- &fView all enchants and their statuses.", "&b/ge give <player> <enchant> &8- &fGive an enchant.", "&b/enchants &8- &fView available enchants in GUI", "&b/ge add <enchant> <level> &8- &fAdd levels to item you are holding", "&3&m----------------------------------------------------"}));
        }
}
