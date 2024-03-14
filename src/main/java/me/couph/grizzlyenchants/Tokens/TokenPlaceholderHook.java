package me.couph.grizzlyenchants.Tokens;

import me.clip.placeholderapi.PlaceholderHook;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TokenPlaceholderHook extends PlaceholderExpansion {

    private GrizzlyEnchants plugin;

    public TokenPlaceholderHook(GrizzlyEnchants plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tokencount";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Couph";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player==null) return "";
        return String.valueOf(plugin.tokenHandler.getTokens(player));
    }
}
