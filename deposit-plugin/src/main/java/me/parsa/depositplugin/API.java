package me.parsa.depositplugin;

import me.parsa.depositapi.DepositApi;
import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.Listeners.GameStartListener;
import me.parsa.depositplugin.Manager.ConfigMain;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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

    HologramUtil hologramUtil = new HologramUtil() {
        @Override
        public void reloadHolograms(Player player) {
            new GameStartListener(DepositPlugin.plugin, ArenasConfig.get()).createHolograms(player);
        }

        @Override
        public boolean isHologramsWorked() {
            GameStartListener gameStartListener = new GameStartListener(DepositPlugin.plugin, ArenasConfig.get());
            boolean success = false;
            if (gameStartListener.succesGameState &&  gameStartListener.successGameAssgin || gameStartListener.isReloaded) {
                success = true;
            }
            return success;
        }
    };

    @Override
    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public HologramUtil getHologramUtil() {
        return hologramUtil;
    }

}
