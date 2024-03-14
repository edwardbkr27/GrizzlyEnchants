package me.couph.grizzlyenchants.Tokens;

import me.couph.grizzlybackpacks.util.CouphUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BuyableItem {

    private Material material;
    private String name;
    private String displayName;
    private String command;
    private int price;
    private int ID;

    public int getPrice() {
        return price;
    }

    public String getCommand() {
        return command;
    }

    public int level;
    public String color;
    public String description;
    public int maxlevel;

    public BuyableItem(String name, String displayName, String command, int price, Material material, int id, int level, String color, String description, int maxlevel) {
        this.name = name;
        this.displayName = displayName;
        this.command = command;
        this.price = price;
        this.material = material;
        this.ID = id;
        this.level = level;
        this.color = color;
        this.description = description;
        this.maxlevel = maxlevel;
    }

    public ItemStack createItem() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CouphUtil.color(this.displayName));
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(CouphUtil.color("&3» Price: &b" + this.price));
        lore.add(CouphUtil.color("&7» ID: " + this.ID));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createEnchantItem() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(CouphUtil.color(this.displayName));
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(CouphUtil.color(this.color + "» Price: &f" + this.price));
        lore.add("");
        lore.add(CouphUtil.color(this.color + "» " + this.description));
        lore.add("");
        lore.add(CouphUtil.color(this.color + "» Requires Pickaxe Level: &f" + this.level));
        lore.add(CouphUtil.color(this.color + "» Maximum Enchant Level: &f" + this.maxlevel));
        lore.add("");
        lore.add(CouphUtil.color("&7» Press Q (drop) to apply as many levels as you can afford"));
        lore.add("");
        lore.add(CouphUtil.color("&7» ID: " + this.ID));
        meta.setLore(lore);
        item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}
