package me.parsa.depositplugin.Utils;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Cache {
    public static File file = new File("plugins/Deposit/cache.yml");
    public static YamlConfiguration cache;
    public static void loadCache() {
        if(!file.exists()) {
            File folder = new File("plugins/Deposit");
            if(!folder.exists()) folder.mkdir();
            try {
                file.createNewFile();
            } catch (Exception ex) {
                ex.printStackTrace();;
            }
        }
        cache = YamlConfiguration.loadConfiguration(file);
    }
    public static void save() {
        try {
            cache.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
