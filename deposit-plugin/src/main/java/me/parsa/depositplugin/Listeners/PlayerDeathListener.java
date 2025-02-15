package me.parsa.depositplugin.Listeners;

import com.andrei1058.bedwars.api.events.player.PlayerKillEvent;
import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.DepositPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;


public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerKillEvent event){
        if (DepositPlugin.plugin.getConfig().getBoolean("disable-hologram-after-death")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = event.getVictim();
                    GameStartListener gameStartListener = new GameStartListener(DepositPlugin.plugin, ArenasConfig.get());
                    gameStartListener.deleteHolograms(player);
                }
            }.runTaskLaterAsynchronously(DepositPlugin.plugin, 5L);

        }
    }

}
