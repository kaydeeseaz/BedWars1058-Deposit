package me.parsa.depositplugin.Listeners.bedwars2023;

import com.tomkeuper.bedwars.api.events.player.PlayerKillEvent;
import me.parsa.depositplugin.Configs.ArenasConfig;
import me.parsa.depositplugin.DepositPlugin;
import me.parsa.depositplugin.Listeners.GameStartListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class TomDeathListenera implements Listener {
    @EventHandler
    public void onDeath(PlayerKillEvent event){
        if (DepositPlugin.plugin.getConfig().getBoolean("disable-hologram-after-death")) {
            DepositPlugin.debug("PlayerKillEvent triggered");
            new BukkitRunnable() {
                @Override
                public void run() {
                    DepositPlugin.debug("Run for player death triggered");
                    Player player = event.getVictim();
                    DepositPlugin.debug("Initializing gamestartlistener");
                    GameStartListener gameStartListener = new GameStartListener(DepositPlugin.plugin, ArenasConfig.get());
                    DepositPlugin.debug("Deleting hd");
                    gameStartListener.deleteHolograms(player);
                    DepositPlugin.debug("Done");
                }
            }.runTaskLaterAsynchronously(DepositPlugin.plugin, 5L);

        }
    }
}
