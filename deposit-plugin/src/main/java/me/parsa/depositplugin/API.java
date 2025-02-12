package me.parsa.depositplugin;

import me.parsa.depositapi.DepositApi;
import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.Manager.ConfigMain;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class API implements DepositApi {

    ConfigManager configManager = new ConfigManager() {
        @Override
        public List<String> getArenaChests(World arena) {
            return ConfigMain.getArenaChests(arena);
        }

        @Override
        public FileConfiguration getArenasConfig() {
            return ArenasConfig.get();
        }

    };

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

}
