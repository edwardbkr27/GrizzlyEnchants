package me.couph.grizzlyenchants.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import me.couph.grizzlybackpacks.GrizzlyBackpacks;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.Items.Enchant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class EnchantStatusManager {
    private final Map<String, Boolean> enabledMap;

    public EnchantStatusManager(EnchantHandler grizzlyEnchantHandler) {
        this.enabledMap = Maps.newHashMap();
        grizzlyEnchantHandler.getGrizzlyEnchants().forEach(enchant -> this.enabledMap.put(enchant.getName(), true));
        load();
    }

    public void enable(Enchant enchant) {
        this.enabledMap.put(enchant.getName(), Boolean.valueOf(true));
    }

    public void disable(Enchant enchant) {
        this.enabledMap.put(enchant.getName(), Boolean.valueOf(false));
    }

    public void enableAll() {
        this.enabledMap.replaceAll((key, value) -> true);
    }

    public void disableAll() {
        this.enabledMap.replaceAll((key, value) -> false);
    }

    public boolean isEnabled(Enchant enchant) {
        return ((Boolean)this.enabledMap.get(enchant.getName())).booleanValue();
    }

    public boolean isDisabled(Enchant enchant) {
        return !((Boolean)this.enabledMap.get(enchant.getName())).booleanValue();
    }

    public List<String> getListDescription() {
        List<String> list = Lists.newArrayList();
        this.enabledMap.forEach((enchantName, bool) -> {
            Enchant enchant = GrizzlyEnchants.getInstance().getGrizzlyEnchantHandler().getByName(enchantName);
            list.add(((enchant.getDisplayName() == null) ? (enchant.getColorBold() + enchant.getName()) : enchant.getDisplayName()) + "&r &f&l(&f" + enchantName + "&f&l) &8- " + (isEnabled(enchant) ? "&a&lENABLED" : "&c&lDISABLED"));
        });
        return MoreUtil.color(list);
    }

    public void load() {
        try {
            Gson gson = new Gson();
            Type type = (new TypeToken<Map<String, Boolean>>() {

            }).getType();
            FileReader fileReader = new FileReader(getFile());
            Map<String, Boolean> enabledMap = (Map<String, Boolean>)gson.fromJson(fileReader, type);
            this.enabledMap.putAll(enabledMap);
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        try {
            File file = new File(GrizzlyEnchants.getInstance().getDataFolder() + File.separator + "enchantstatus.json");
            if (!file.exists())
                file.createNewFile();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void save() {
        try {
            (new FileWriter(getFile(), false)).close();
            FileWriter fileWriter = new FileWriter(getFile());
            Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(gson.toJson(this.enabledMap));
            fileWriter.write(gson.toJson(jsonElement));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



