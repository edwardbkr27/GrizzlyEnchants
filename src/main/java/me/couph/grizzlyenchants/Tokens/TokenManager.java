package me.couph.grizzlyenchants.Tokens;

import me.couph.grizzlybackpacks.GrizzlyBackpacks;
import me.couph.grizzlyenchants.GrizzlyEnchants;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenManager {
    private GrizzlyEnchants plugin;
    private HashMap<UUID, Long> tokenMap;

    private String dataFile;
    private String backupFile;

    public TokenManager(GrizzlyEnchants plugin) {
        this.plugin = plugin;
        this.dataFile = GrizzlyEnchants.getInstance().getDataFolder() + File.separator + "tokendata.txt";
        this.backupFile = GrizzlyEnchants.getInstance().getDataFolder() + File.separator + "tokendatabackup.txt";
        this.tokenMap = loadTokenMap();
    }

    public static HashMap<UUID, Long> loadTokenMap() {
        HashMap<UUID, Long> tokenMap = new HashMap<>();
        try {
            FileReader fileReader = new FileReader(GrizzlyEnchants.getInstance().getDataFolder() + File.separator + "tokendata.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(":");
                UUID uuid = UUID.fromString(parts[0].trim());
                long number = Integer.parseInt(parts[1].trim());
                tokenMap.put(uuid, number);
            }
            bufferedReader.close();
        } catch (Exception e) {
            return null;
        }
        return tokenMap;
    }

    public void saveTokenMap() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile))) {
            for (Map.Entry<UUID, Long> entry : tokenMap.entrySet()) {
                UUID uuid = entry.getKey();
                Long num = entry.getValue();
                writer.write(uuid.toString() + ":" + num);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTokenMapBackup() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(backupFile))) {
            for (Map.Entry<UUID, Long> entry : tokenMap.entrySet()) {
                UUID uuid = entry.getKey();
                Long num = entry.getValue();
                writer.write(uuid.toString() + ":" + num);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getTokens(Player player) {
        return tokenMap.getOrDefault(player.getUniqueId(), (long)0);
    }

    public void addTokens(Player player, Long amount) {
        long currAmount = 0;
        if (tokenMap.containsKey(player.getUniqueId())) {
            currAmount = tokenMap.get(player.getUniqueId());
        }
        currAmount = currAmount + amount;
        tokenMap.put(player.getUniqueId(), currAmount);
    }

    public void removeTokens(Player player, long amount) {
        long currAmount = tokenMap.get(player.getUniqueId());
        currAmount = currAmount - amount;
        tokenMap.put(player.getUniqueId(), currAmount);
    }

    public boolean canAfford(Player player, long amount) {
        long currAmount = tokenMap.get(player.getUniqueId());
        return currAmount > amount;
    }

    public boolean isOnMap(Player player) {
        return tokenMap.containsKey(player.getUniqueId());
    }

    public void addToMap(Player player) {
        if (!tokenMap.containsKey(player.getUniqueId())) {
            tokenMap.put(player.getUniqueId(), (long)0);
        }
    }
}

