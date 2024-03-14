package me.couph.grizzlyenchants.Items;

import me.couph.grizzlyenchants.util.CouphUtil;
import me.couph.grizzlyenchants.util.PlaceHoldersUtil;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.List;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public abstract class Enchant implements Listener {
    private String name;
    private String displayName;
    private ChatColor color;
    private String enchantIdentifier;
    private Boolean enabled;
    private String ability;
    private String abilityDescription;
    private Material material;
    private Integer maxLevel;
    private String levelUpInfo;

    private Integer requiredLevel;
    public void setName(String name) {
        this.name = name;
    }
    public void setLevelUpInfo(String levelUpInfo) {
        this.levelUpInfo = levelUpInfo;
    }
    public void setMaxLevel(Integer maxLevel) {
        this.maxLevel = maxLevel;
    }
    public void setMaterial(Material material) {
        this.material = material;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public void setAbilityDescription(String abilityDescription) {
        this.abilityDescription = abilityDescription;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void setAbility(String ability) {
        this.ability = ability;
    }

    public void setEnchantIdentifier(String identifier) {
        this.enchantIdentifier = identifier;
    }

    public void setRequiredLevel(Integer level) {
        this.requiredLevel = level;
    }

    public Material getMaterial() {
        return this.material;
    }

    public Integer getRequiredLevel() {
        return requiredLevel;
    }

    public String getLevelUpInfo() {
        return this.levelUpInfo;
    }
    public Integer getMaxLevel() {
        return this.maxLevel;
    }

    public String getName() {
        return this.name;
    }

    public String getAbilityDescription() {
        return this.abilityDescription;
    }

    public String getEnchantIdentifier() {
        return this.enchantIdentifier;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public String getAbility() {
        return this.ability;
    }

    public Enchant(String name, ChatColor color) {
        this.name = name;
        this.color = color;
        this.enabled = Boolean.TRUE;
        this.enchantIdentifier = PlaceHoldersUtil.translatePlaceholders(getPlugin().getConfig().getString("enchant-identifier"), this);
        getPlugin().getServer().getPluginManager().registerEvents(this, (Plugin)getPlugin());
    }

    public ItemStack createEnchant(Material material) {
        String itemName, name = (this.displayName == null) ? this.name : this.displayName;
        itemName = CouphUtil.color(getColor() + "&kii&r " + getColorBold() + this.name + "&r " + "&7(Enchantment Upgrade) " + getColor() + "&kii&r");
        List<String> enchantLore = PlaceHoldersUtil.translatePlaceholders(CouphUtil.color(getPlugin().getConfig().getStringList("lore")), this);
        if (enchantLore.contains("%LIST_BONUS%")) {
            int index = enchantLore.indexOf("%LIST_BONUS%");
            enchantLore.set(index++, CouphUtil.color(getColor() + "&l* &7&l" + getName().toUpperCase() + " ENCHANT DESCRIPTION " + getColor() + "&l*"));
            enchantLore.add(index++, CouphUtil.color("&7&l" + "» " + getColor() + getAbilityDescription()));
            enchantLore.add(index++, CouphUtil.color("&7&l" + "» " + getColor() + "Max Level: &7" + String.valueOf(getMaxLevel())));
            enchantLore.add(index++, CouphUtil.color("&7&l" + "» " + getColor() + "Requires Pickaxe Level: &7" + String.valueOf(getRequiredLevel())));
            if (getMaxLevel() > 1) {
                enchantLore.add(index++, CouphUtil.color("&7&l" + "» " + getColor() + "Level up this enchant to &7" + getLevelUpInfo()));
            }
            enchantLore.add(index++, CouphUtil.color("&7&l" + "» " + getColor() + "Drag and drop onto your pickaxe to apply."));
            enchantLore.add(index++, "");
            if (getName().contains("Frenzy") || getName().contains("Bounty")) {
                enchantLore.add(index++, CouphUtil.color(getColor() + "(!) Requires autosell enchantment (!)"));
                enchantLore.add(index++, "");
            }
        }
        ItemStack item = CouphUtil.createItem(material, itemName, enchantLore);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(CouphUtil.color(enchantLore));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isGrizzlyEnchant(ItemStack item) {
        if (item == null || item.getType() != Material.BOOK || !item.hasItemMeta() || !item.getItemMeta().hasLore())
            return false;
        return item.getItemMeta().getLore().contains(CouphUtil.color(getEnchantIdentifier()));
    }

    public void reInitialise(Player player, Enchant enchant) {
        setName(enchant.getName());
        setMaxLevel(enchant.getMaxLevel());
        setAbility(enchant.getAbility());
        setAbilityDescription(enchant.getAbilityDescription());
    }

    public FileConfiguration getConfig() {
        return getPlugin().getConfig();
    }

    public void Activate(Player enchantUser) {}

    public void unActivate(Player enchantUser) {

    }

    public void applyEnchant(ItemStack item) {

    }


    public void onBlockBreak(Player player, ItemStack item, Collection<ItemStack> drops, Block block) {

    }

    public String getColorBold() {
        return getColor().toString() + ChatColor.BOLD;
    }

    public GrizzlyEnchants getPlugin() {
        return GrizzlyEnchants.getInstance();
    }
}
