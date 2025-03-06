package me.parsa.depositplugin.Listeners;

import com.andrei1058.bedwars.api.arena.GameState;
import com.andrei1058.bedwars.api.events.gameplay.GameEndEvent;
import com.andrei1058.bedwars.api.events.gameplay.GameStateChangeEvent;
import me.parsa.depositplugin.DepositPlugin;
import me.parsa.depositplugin.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameListener implements Listener {


    @EventHandler
    public void onGameStart(GameStateChangeEvent e) {
        if(e.getNewState() == GameState.playing) {
            Bukkit.getScheduler().runTask(DepositPlugin.plugin, () -> {
                Utils.spawnHolograms(e.getArena());
            });
        }
    }

    @EventHandler
    public void onGameEnd(GameEndEvent e) {
        Utils.deleteHolograms(e.getArena());
    }
    @EventHandler
    public void onGameEnd(GameStateChangeEvent e) {
        if(e.getNewState() == GameState.restarting) {
            Utils.deleteHolograms(e.getArena());
        }
    }


}