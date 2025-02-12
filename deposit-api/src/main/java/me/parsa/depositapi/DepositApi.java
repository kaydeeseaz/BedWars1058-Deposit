package me.parsa.depositapi;


import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public interface DepositApi {

    /**
     * Get the config manager for bedwars deposit
     * @return ConfigManager
     */
    ConfigManager getConfigManager();

    interface ConfigManager {

        /**
         * Get a list for arena chects and ender chests
         * @param arena
         * @return All chest locations (String list)
         */
        List<String> getArenaChests(World arena);

        /**
         * Get config for deposit arenas chestLocations.yml
         * @return FileConfiguration
         */
        FileConfiguration getArenasConfig();

    }

}


