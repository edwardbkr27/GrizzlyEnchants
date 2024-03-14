package me.couph.grizzlyenchants.util;

import me.couph.grizzlyenchants.GrizzlyEnchants;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

@Deprecated
public class PlayerTimerMap implements Serializable {
    private long defaultTimer;

    private HashMap<String, Long> map;

    public void setDefaultTimer(long defaultTimer) {
        this.defaultTimer = defaultTimer;
    }

    public long getDefaultTimer() {
        return this.defaultTimer;
    }

    public HashMap<String, Long> getMap() {
        return this.map;
    }

    public PlayerTimerMap(long defaultTimer) {
        this.defaultTimer = defaultTimer;
        this.map = new HashMap<>();
        (new BukkitRunnable() {
            public void run() {
                for (String s : Lists.newArrayList(PlayerTimerMap.this.map.keySet())) {
                    long time = ((Long)PlayerTimerMap.this.map.get(s)).longValue() - System.currentTimeMillis();
                    if (time < 1L)
                        PlayerTimerMap.this.map.remove(s);
                }
                return;
            }
        }).runTaskTimer((Plugin)GrizzlyEnchants.getInstance(), 0L, 600L);
    }

    public void add(Player player, String timerId) {
        this.map.put(player.getName() + timerId, Long.valueOf(System.currentTimeMillis() + this.defaultTimer));
    }

    public void add(Player player, long timer, String timerId) {
        this.map.put(player.getName() + timerId, Long.valueOf(System.currentTimeMillis() + timer));
    }

    public void remove(Player player, String timerId) {
        this.map.remove(player.getName() + timerId);
    }

    public boolean isOnTimer(Player player, String timerId) {
        return (getRemainingTime(player, timerId) > 1L);
    }

    public long getRemainingTime(Player player, String timerId) {
        if (!this.map.containsKey(player.getName() + timerId))
            return 0L;
        return ((Long)this.map.get(player.getName() + timerId)).longValue() - System.currentTimeMillis();
    }

    public void error(Player player, String timerId) {
        long nextUse = getRemainingTime(player, timerId);
        int seconds = (int)nextUse / 1000;
    }
}

