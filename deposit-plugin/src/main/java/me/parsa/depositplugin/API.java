package me.parsa.depositplugin;

import me.parsa.depositapi.DepositApi;
import me.parsa.depositplugin.Manager.ConfigMain;
import org.bukkit.World;

import java.util.List;

public class API implements DepositApi {

    ConfigManager configManager = new ConfigManager() {
        @Override
        public List<String> getArenaChests(World arena) {
            return ConfigMain.getArenaChests(arena);
        }

    };

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

}
