package me.couph.grizzlyenchants.util;

import me.couph.grizzlyenchants.Items.Enchant;

import java.util.List;
import java.util.stream.Collectors;

public class PlaceHoldersUtil {
    public static String translatePlaceholders(String message, Enchant enchant) {
        return getDefaultProperties().format(message, enchant);
    }

    public static String translatePlaceholders(String message, Enchant enchant, PlaceholderProperties properties) {
        return properties.format(message, enchant);
    }

    public static List<String> translatePlaceholders(List<String> messages, Enchant enchant) {
        return (List<String>)messages.stream().map(msg -> translatePlaceholders(msg, enchant)).collect(Collectors.toList());
    }

    public static List<String> translatePlaceholders(List<String> messages, Enchant enchant, PlaceholderProperties properties) {
        return (List<String>)messages.stream().map(msg -> translatePlaceholders(msg, enchant, properties)).collect(Collectors.toList());
    }

    public static PlaceholderProperties getDefaultProperties() {
        PlaceholderProperties properties = new PlaceholderProperties();
        properties.put("%color%", enchant -> CouphUtil.color(String.valueOf(enchant.getColor())));
        properties.put("%colorB%", enchant -> enchant.getColorBold());
        properties.put("%grizzlyenchant%", enchant -> enchant.getName());
        properties.put("%grizzlyenchantU%", enchant -> enchant.getName().toUpperCase());
        properties.put("%grizzlyenchantL%", enchant -> enchant.getName().toLowerCase());
        properties.put("%identifier%", enchant -> enchant.getEnchantIdentifier());
        properties.put("%displayname%", enchant -> (enchant.getDisplayName() == null) ? enchant.getName() : enchant.getDisplayName());
        properties.put("%displaynameU%", enchant -> (enchant.getDisplayName() == null) ? enchant.getName().toUpperCase() : enchant.getDisplayName().toUpperCase());
        return properties;
    }
}
