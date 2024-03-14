package me.couph.grizzlyenchants.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.couph.grizzlyenchants.Enchants.*;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.Enchant;
import me.couph.grizzlytools.GrizzlyTools;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

import java.util.List;
import java.util.Map;

public class EnchantHandler {
    private GrizzlyEnchants plugin;
    private GrizzlyTools grizzlyTools;
    public GrizzlyEnchants getPlugin() {
        return this.plugin;
    }
    private Map<Player, Enchant> activeEnchantMap = Maps.newHashMap();

    public Map<Player, Enchant> getActiveEnchantMap() {
        return this.activeEnchantMap;
    }
    private List<Enchant> grizzlyEnchants = Lists.newArrayList();
    private EnchantStatusManager enchantStatusManager;

    public List<Enchant> getGrizzlyEnchants() {
        return this.grizzlyEnchants;
    }

    public EnchantStatusManager getEnchantStatusManager() {
        return this.enchantStatusManager;
    }

    public void setGrizzlyTools(GrizzlyTools grizzlyTools) {
        this.grizzlyTools = grizzlyTools;
    }

    public EnchantHandler(GrizzlyEnchants plugin) {
        this.plugin = plugin;
        setGrizzlyTools(GrizzlyTools.getInstance());
        registerGrizzlyEnchants();
        this.enchantStatusManager = new EnchantStatusManager(this);
    }

    public void registerGrizzlyEnchants() {
        this.grizzlyEnchants.forEach(HandlerList::unregisterAll);
        this.grizzlyEnchants.clear();
        registerGrizzlyEnchant((Enchant)new Autosell());
        registerGrizzlyEnchant((Enchant)new Efficiency());
        registerGrizzlyEnchant((Enchant)new FortuneFrenzy());
        registerGrizzlyEnchant((Enchant)new DragonsBreath());
        registerGrizzlyEnchant((Enchant)new Thor());
        registerGrizzlyEnchant((Enchant)new XPBoost());
        registerGrizzlyEnchant((Enchant)new Drill());
        registerGrizzlyEnchant((Enchant)new Treasure(getPlugin().getConfig()));
        registerGrizzlyEnchant((Enchant)new Hallucination());
        registerGrizzlyEnchant((Enchant)new Laser());
        registerGrizzlyEnchant((Enchant)new TokenBoost());
        registerGrizzlyEnchant((Enchant)new Wither());
        registerGrizzlyEnchant((Enchant)new Bounty());
        registerGrizzlyEnchant((Enchant)new BlackHole());
        this.activeEnchantMap.keySet().forEach(player -> {
            Enchant grizzlyEnchant = getByName(((Enchant)this.activeEnchantMap.get(player)).getName());
            CouphUtil.sendMessage((LivingEntity)player, "&a&l(!)&7 Your " + grizzlyEnchant.getColorBold() + grizzlyEnchant.getName() + " Enchant&7 has been &a&nupdated&7.");
            this.activeEnchantMap.put(player, grizzlyEnchant);
        });
    }

    public void recalculateEnchantStatuses() {
        Maps.newHashMap(this.activeEnchantMap).forEach((player, enchant) -> {
            if (this.enchantStatusManager.isDisabled(enchant)) {
                enchant.unActivate(player);
                setActiveEnchant(player, null);
                String message = this.plugin.getConfig().getString("messages.set-unequipped");
            }
        });
    }


    public void registerGrizzlyEnchant(Enchant grizzlyEnchant) {
        this.grizzlyEnchants.add(grizzlyEnchant);
    }

    public Enchant getByName(String name) {
        return this.grizzlyEnchants.stream().filter(grizzlyEnchant -> grizzlyEnchant.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean isEnchantEnabled(Enchant grizzlyEnchant) {
        return this.enchantStatusManager.isEnabled(grizzlyEnchant);
    }

    public boolean isEnchantDisabled(Enchant grizzlyEnchant) {
        return !isEnchantEnabled(grizzlyEnchant);
    }

    public Enchant getByItem(ItemStack item) {
        return this.grizzlyEnchants.stream().filter(grizzlyEnchant -> grizzlyEnchant.isGrizzlyEnchant(item)).findFirst().orElse(null);
    }
    public Enchant getByClass(Class<? extends Enchant> clazz) {
        return this.grizzlyEnchants.stream().filter(grizzlyEnchant -> grizzlyEnchant.getClass().equals(clazz)).findFirst().orElse(null);
    }
    public Enchant getActiveEnchant(Player player) {
//        if (player.hasMetadata("atype")
//            return null;

        CouphUtil.sendMessage(player, String.valueOf(this.activeEnchantMap.size()));
        return this.activeEnchantMap.getOrDefault(player, null);
    }

    public boolean hasActiveEnchant(Player player) {
        return this.activeEnchantMap.containsKey(player);
    }

    public boolean isGrizzlyEnchant(ItemStack item) {
        return (getByItem(item) != null);
    }

    public boolean isGrizzlyEnchant(String name) {
        return (getByName(name) != null);
    }

    public void setActiveEnchant(Player player, Enchant grizzlyEnchant) {
        if (grizzlyEnchant == null) {
            this.activeEnchantMap.remove(player);
        } else {
            this.activeEnchantMap.put(player, grizzlyEnchant);
        }
    }

    public ItemMeta applyEnchant(Player player, ItemStack applyTo, Enchant enchant) {
        ItemMeta meta = applyTo.getItemMeta();
        if (enchant.getName().equalsIgnoreCase("Efficiency")) {
            if (getEffLvl(applyTo) < 200) {
                return applyEfficiency(applyTo, enchant);
            } else {
                return meta;
            }
        }
        List<String> lore = meta.getLore();
        if ((applyTo == null) || (enchant == null)) return meta;
        int currLevel = 0;
        String currLevelStr = "";
        int currIndex = 0;
        boolean hasEnchantment = false;
        // if enchant already there, increment level
        // if enchant not there, set to level 1
        for (String loreLine : lore) {
            if (loreLine.contains("This is a")) break;
            if (loreLine.contains(enchant.getName())) {
                currIndex = lore.indexOf(loreLine);
                loreLine = CouphUtil.colorAndStrip(loreLine);
                int whitespaceIndex = loreLine.indexOf(" ");
                if (Objects.equals(enchant.getName(), "Dragons Breath") || Objects.equals(enchant.getName(), "Fortune Frenzy") || Objects.equals(enchant.getName(), "XP Boost") || Objects.equals(enchant.getName(), "Token Boost") || Objects.equals(enchant.getName(), "Black Hole")){
                    whitespaceIndex = loreLine.indexOf(" ", whitespaceIndex+1);
                }
                currLevel = Integer.parseInt(loreLine.substring(whitespaceIndex + 1));
                hasEnchantment = true;
            }
        }
        if (hasEnchantment) lore.remove(currIndex);
        if (currLevel+1 == enchant.getMaxLevel()) {
            lore.add(0, CouphUtil.color(enchant.getColor() + enchant.getName() + " " + "MAX"));
        } else {
            lore.add(0, CouphUtil.color(enchant.getColor() + enchant.getName() + " " + (currLevel + 1)));
        }
        // add (if)s for names with spaces
        meta.setLore(lore);
        return meta;
    }

    public ItemMeta applyEnchantPlayerless(ItemStack applyTo, Enchant enchant) {
        ItemMeta meta = applyTo.getItemMeta();
        if (enchant.getName().equalsIgnoreCase("Efficiency")) {
            if (getEffLvl(applyTo) < 200) {
                return applyEfficiency(applyTo, enchant);
            } else {
                return meta;
            }
        }
        List<String> lore = meta.getLore();
        if ((applyTo == null) || (enchant == null)) return meta;
        int currLevel = 0;
        String currLevelStr = "";
        int currIndex = 0;
        boolean hasEnchantment = false;
        // if enchant already there, increment level
        // if enchant not there, set to level 1
        for (String loreLine : lore) {
            if (loreLine.contains("This is a")) break;
            if (loreLine.contains(enchant.getName())) {
                currIndex = lore.indexOf(loreLine);
                loreLine = CouphUtil.colorAndStrip(loreLine);
                int whitespaceIndex = loreLine.indexOf(" ");
                if (Objects.equals(enchant.getName(), "Dragons Breath") || Objects.equals(enchant.getName(), "Fortune Frenzy") || Objects.equals(enchant.getName(), "XP Boost") || Objects.equals(enchant.getName(), "Token Boost") || Objects.equals(enchant.getName(), "Black Hole")){
                    whitespaceIndex = loreLine.indexOf(" ", whitespaceIndex+1);
                }
                currLevel = Integer.parseInt(loreLine.substring(whitespaceIndex + 1));
                hasEnchantment = true;
            }
        }
        if (hasEnchantment) lore.remove(currIndex);
        if (currLevel+1 == enchant.getMaxLevel()) {
            lore.add(0, CouphUtil.color(enchant.getColor() + enchant.getName() + " " + "MAX"));
        } else {
            lore.add(0, CouphUtil.color(enchant.getColor() + enchant.getName() + " " + (currLevel + 1)));
        }
        // add (if)s for names with spaces
        meta.setLore(lore);
        return meta;
    }



    public ItemMeta decrementEnchant(ItemStack item, int index) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        String enchantLine = CouphUtil.colorAndStrip(lore.get(index));
        int whitespaceIndex = enchantLine.indexOf(" ");
        String enchName = CouphUtil.colorAndStrip(lore.get(index)).substring(0, whitespaceIndex);
        if (enchantLine.contains("Dragons") || enchantLine.contains("Token") || enchantLine.contains("XP") || enchantLine.contains("Fortune") || enchantLine.contains("Black")) {
            whitespaceIndex = enchantLine.indexOf(" ", whitespaceIndex+1);
            enchName = enchantLine.substring(0, whitespaceIndex);
        }
        Enchant enchant = getByName(enchName);
        String currLevelStr = enchantLine.substring(whitespaceIndex + 1);
        int currLevel = 0;
        if (currLevelStr.equalsIgnoreCase("MAX")) {
            currLevel = enchant.getMaxLevel();
        } else {
            currLevel = Integer.parseInt(currLevelStr);
        }
        if (currLevel != 1) {
            currLevel--;
            lore.remove(index);
            lore.add(index, CouphUtil.color(enchant.getColor() + enchant.getName() + " " + currLevel));
        }
        else {
            lore.remove(index);
            }
        meta.setLore(lore);
        return meta;

    }

    public Integer getEffLvl(ItemStack item) {
        return item.getEnchantmentLevel(Enchantment.DIG_SPEED);
    }

    public ItemMeta applyEfficiency(ItemStack applyTo, Enchant enchant) {
        Map<Enchantment, Integer> enchants = applyTo.getEnchantments();
        int effLvl = enchants.get(Enchantment.DIG_SPEED);
        effLvl++;
        applyTo.removeEnchantment(Enchantment.DIG_SPEED);
        applyTo.addUnsafeEnchantment(Enchantment.DIG_SPEED, effLvl);
        return applyTo.getItemMeta();
    }

    public boolean itemHasEnchant(ItemStack item, Enchant enchant) {
        if (Objects.equals(enchant.getName(), "Efficiency") && (item.getEnchantments().containsKey(Enchantment.DIG_SPEED))) return true;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        String enchantName = enchant.getName();
        for (String loreLine : lore) {
            if (CouphUtil.colorAndStrip(loreLine).contains(enchant.getName())) {
                return true;
            }
            if (CouphUtil.colorAndStrip(loreLine).contains("This is a")) {
                break;
            }
        }
        return false;
    }

    public Integer getNumberOfEnchants(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        int numEnchants = 0;
        for (String loreLine : lore) {
            if (CouphUtil.colorAndStrip(loreLine).contains("This is a")) {
                break;
            } else {
                numEnchants++;
            }
        }
        return numEnchants;
    }

//    public Integer maxPickaxeEnchants(Integer level) {
//        int maxEnchants = 0;
//        if (level >= 1) {
//            maxEnchants = 1;
//        }
//        if (level >= 3) {
//            maxEnchants = 2;
//        }
//        if (level >= 7) {
//            maxEnchants = 3;
//        }
//        if (level >= 15) {
//            maxEnchants = 4;
//        }
//        if (level >= 25) {
//            maxEnchants = 5;
//        }
//        if (level >= 50) {
//            maxEnchants = 6;
//        }
//        if (level >= 75) {
//            maxEnchants = 7;
//        }
//        if (level >= 100) {
//            maxEnchants = 8;
//        }
//        return maxEnchants;
//    }

//    public int getNextLvlForApply(Integer currLvl) {
//        int nextLvl = 1;
//        if (currLvl >= 1) {
//            nextLvl = 3;
//        }
//        if (currLvl >= 3) {
//            nextLvl = 7;
//        }
//        if (currLvl >= 7) {
//            nextLvl = 15;
//        }
//        if (currLvl >= 15) {
//            nextLvl = 25;
//        }
//        if (currLvl >= 25) {
//            nextLvl = 50;
//        }
//        if (currLvl >= 50) {
//            nextLvl = 75;
//        }
//        if (currLvl >= 75) {
//            nextLvl = 100;
//        }
//        if (currLvl >= 100) {
//            nextLvl = -1;
//        }
//        return nextLvl;
//    }

    public Integer getReqLevel(Enchant enchant) {
        return enchant.getRequiredLevel();
    }


    public boolean canApplyEnchant(Player player, ItemStack item, Enchant enchant, Integer level) {
        if (enchant.getName().equalsIgnoreCase("Efficiency")) {
            if (getEffLvl(item) >= 200) {
                player.sendMessage(CouphUtil.color("&c(!) You already have the maximum level of this enchantment!"));
                return false;
            }
        }
        if (enchant.getMaxLevel() == getEnchantLevel(item, enchant)) {
            player.sendMessage(CouphUtil.color("&c(!) You already have the maximum level of this enchantment!"));
            return false;
        }
        if (grizzlyTools.getGrizzlyPickaxeHandler().getByItem(item) == null) {
            player.sendMessage(CouphUtil.color("&c(!) You can't apply an enchantment to this item!"));
            return false;
        }
        if (level < enchant.getRequiredLevel()) {
            player.sendMessage(CouphUtil.color("&c(!) You need pickaxe level &c&n" + enchant.getRequiredLevel().toString() + "&c to apply this enchant!"));
            return false;
        }
        if (enchant.getName().equalsIgnoreCase("Fortune Frenzy")) {
            if (!itemHasEnchant(item, getByName("Autosell"))) {
                player.sendMessage(CouphUtil.color("&c(!) Please apply the autosell enchant to your pickaxe before applying this enchant!"));
                return false;
            }
        }
        return true;
    }

    public boolean canApplyEnchantPlayerless(ItemStack item, Enchant enchant, Integer level) {
        if (enchant.getName().equalsIgnoreCase("Efficiency")) {
            if (getEffLvl(item) >= 200) {
                return false;
            }
        }
        if (enchant.getMaxLevel() == getEnchantLevel(item, enchant)) {
            return false;
        }
        if (grizzlyTools.getGrizzlyPickaxeHandler().getByItem(item) == null) {
            return false;
        }
        if (level < enchant.getRequiredLevel()) {
            return false;
        }
        if (enchant.getName().equalsIgnoreCase("Fortune Frenzy")) {
            if (!itemHasEnchant(item, getByName("Autosell"))) {
                return false;
            }
        }
        return true;
    }

    public Integer getEnchantLevel(ItemStack item, Enchant enchant) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        int level = 0;
        String levelStr;
        for (String loreLine : lore) {
            if (CouphUtil.colorAndStrip(loreLine).contains(enchant.getName())) {
                int whitespaceIndex = loreLine.indexOf(" ");
                if (Objects.equals(enchant.getName(), "Dragons Breath") || Objects.equals(enchant.getName(), "Fortune Frenzy") || Objects.equals(enchant.getName(), "XP Boost") || Objects.equals(enchant.getName(), "Token Boost") || Objects.equals(enchant.getName(), "Black Hole")){
                    whitespaceIndex = loreLine.indexOf(" ", whitespaceIndex+1);
                }
                levelStr = loreLine.substring(whitespaceIndex + 1);
                if (levelStr.contains("MAX")) {
                    level = enchant.getMaxLevel();
                } else {
                    level = Integer.parseInt(levelStr);
                }
            }
            if (CouphUtil.colorAndStrip(loreLine).contains("This is a")) {
                break;
            }
        }
        return level;
    }

    public void getGui(Player player) {
        List<ItemStack> enchants = new ArrayList<>();
        for (Enchant e : getGrizzlyEnchants()) {
            enchants.add(e.createEnchant(Material.BOOK));
        }
        GuiUtil.openGui(player, enchants);
        player.sendMessage(CouphUtil.color("&a(!) You have opened the Enchantments GUI."));
    }

}

