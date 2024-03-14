package me.couph.grizzlyenchants.Commands;

import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import me.couph.grizzlyenchants.util.CouphUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Top implements CommandExecutor {

    private GrizzlyEnchants plugin;

    public Top(GrizzlyEnchants plugin) {
        this.plugin = plugin;
    }
    private HashMap<String, String> topCoords;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (regionIsMine((Player) sender)) {
            String regionName = getRegionName((Player)sender);
            if (regionName != null) {
                initialiseTopCoords();
                String coords = this.topCoords.get(regionName);
                if (coords != null) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tp " + sender.getName() + " " + coords);
                    return true;
                }
            }
        }
        sender.sendMessage(CouphUtil.color("&c(!) You need to be in a mine to use that command!"));
        return true;
    }

    public void initialiseTopCoords() {
        this.topCoords = new HashMap<>();
        this.topCoords.put("a1mine", "-191 105 546");
        this.topCoords.put("bmine", "-800 110 4");
        this.topCoords.put("cmine", "-1200 110 3");
        this.topCoords.put("dmine", "-1600 110 4");
        this.topCoords.put("emine", "-2000 110 4");
        this.topCoords.put("fmine", "-2400 92 17");
        this.topCoords.put("gmine", "-2801 106 -20");
        this.topCoords.put("hmine", "-3200 110 21");
        this.topCoords.put("imine", "-3600 110 -18");
        this.topCoords.put("jmine", "-4018 145 0");
        this.topCoords.put("kmine", "-4417 114 0");
        this.topCoords.put("lmine", "-4790 110 5");
        this.topCoords.put("mmine", "384 110 -8");
        this.topCoords.put("nmine", "783 86 -11");
        this.topCoords.put("omine", "1124 110 61");
        this.topCoords.put("pmine", "1597 110 -7");
        this.topCoords.put("qmine", "1982 109 1");
        this.topCoords.put("rmine", "2418 109 0");
        this.topCoords.put("smine", "2777 109 0");
        this.topCoords.put("tmine", "3177 109 1");
        this.topCoords.put("umine", "3598 146 -33");
        this.topCoords.put("vmine", "4204 171 250");
        this.topCoords.put("wmine", "5283 172 233");
        this.topCoords.put("xmine", "6242 189 283");
        this.topCoords.put("ymine", "7027 110 58");
        this.topCoords.put("zmine", "8220 176 229");
        this.topCoords.put("z2mine", "12029 110 0");
        this.topCoords.put("hustlermine", "-213 59 -11");
        this.topCoords.put("officermine", "1081 111 -54");
        this.topCoords.put("vigilantemine", "2052 111 -28");
        this.topCoords.put("kingpinmine", "3052 119 -83");
        this.topCoords.put("grizzlymine", "4246 176 214");
        this.topCoords.put("grizzly+mine", "8242 179 281");
        this.topCoords.put("grizzly+mine2", "9776 194 282");
        this.topCoords.put("wardenmine", "12017 110 62");
        this.topCoords.put("pvpmine1", "3089 127 81");
        this.topCoords.put("pvpmine2", "3046 127 75");
    }

    public boolean regionIsMine(Player player) {
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());

        for (ProtectedRegion region : regions) {
            return region.getId().contains("mine");
        }
        return false;
    }

    public String getRegionName(Player player) {
        ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());

        for (ProtectedRegion region : regions) {
            if (region.getId().contains("mine")) {
                return region.getId();
            }
        }
        return null;
    }

}
