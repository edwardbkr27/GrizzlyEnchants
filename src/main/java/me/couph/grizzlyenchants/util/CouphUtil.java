package me.couph.grizzlyenchants.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CouphUtil {

    private static final char COLOR_CHAR = '&';
    private static final String LOGGER_PREFIX = color("[LOGGER] %level%:");

    public static ItemStack createItem(Material material, String name, int amount, int data, String... lore) {
        ItemStack item = new ItemStack(material, amount, (short)data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(color(name));
        meta.setLore(color(Arrays.asList(lore)));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String name, String... lore) {
        return createItem(material, name, 1, 0, lore);
    }

    public static ItemStack createItem(Material material, String name, int amount, String... lore) {
        return createItem(material, name, amount, 1, lore);
    }

    public static ItemStack createItem(Material material, String name, int amount, int data, List<String> lore) {
        return createItem(material, name, amount, data, lore.<String>toArray(new String[lore.size()]));
    }

    public static ItemStack createItem(Material material, String name, List<String> lore) {
        return createItem(material, name, lore.<String>toArray(new String[lore.size()]));
    }

    public static String colorAndStrip(String message) {
        return ChatColor.stripColor(color(message));
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static List<String> color(List<String> messages) {
        return (List<String>)messages.stream().map(CouphUtil::color).collect(Collectors.toList());
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
    }

    public static void playSound(Player player, Sound sound, Float volume) {
        player.playSound(player.getLocation(), sound, volume, 1.0F);
    }

    public static void playSound(Player player, Sound sound, Float volume, Float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static void sendMessage(LivingEntity entity, String message) {
        entity.sendMessage(color(message));
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    public static void sendMessage(CommandSender sender, List<String> messages) {
        sender.sendMessage(color(messages).<String>toArray(new String[messages.size()]));
    }

    public static void fillInventory(Inventory inventory, ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null)
                inventory.setItem(i, item);
        }
    }
    public static List<ItemStack> removeNulls(List<ItemStack> itemStacks) {
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack item : itemStacks) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            list.add(item);
        }
        return list;
    }


    public static void log(String message, LogLevel level) {
        Bukkit.getConsoleSender().sendMessage(color(level.getColor() + LOGGER_PREFIX.replace("%level%", level.name()) + " " + message));
    }

    public static boolean isInventoryFull(Player p)
    {
        return p.getInventory().firstEmpty() == -1;
    }

    public enum LogLevel {
        INFO(ChatColor.WHITE),
        ERROR(ChatColor.RED),
        SUCCESS(ChatColor.GREEN);

        private ChatColor color;

        public ChatColor getColor() {
            return this.color;
        }

        LogLevel(ChatColor color) {
            this.color = color;
        }
    }

    private static final String[] ROMAN_NUMERALS = {
            "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"
    };

    private static final int[] VALUES = {
            100, 90, 50, 40, 10, 9, 5, 4, 1
    };

    public static String convertToRoman(int number) {
        if (number < 1 || number > 100) {
            throw new IllegalArgumentException("Number must be between 1 and 100.");
        }

        StringBuilder roman = new StringBuilder();
        int remaining = number;

        for (int i = 0; i < VALUES.length; i++) {
            while (remaining >= VALUES[i]) {
                roman.append(ROMAN_NUMERALS[i]);
                remaining -= VALUES[i];
            }
        }

        return roman.toString();
    }

    public static int convertFromRoman(String romanNumeral) {
        if (romanNumeral == null || romanNumeral.isEmpty()) {
            throw new IllegalArgumentException("Invalid Roman numeral.");
        }

        int result = 0;
        int index = 0;

        for (int i = 0; i < VALUES.length; i++) {
            while (romanNumeral.startsWith(ROMAN_NUMERALS[i], index)) {
                result += VALUES[i];
                index += ROMAN_NUMERALS[i].length();
            }
        }

        return result;
    }
}
