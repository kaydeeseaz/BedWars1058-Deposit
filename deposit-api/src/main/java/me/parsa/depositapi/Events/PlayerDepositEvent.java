package me.parsa.depositapi.Events;

import me.parsa.depositapi.Types.DepositType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDepositEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;

    Player player;

    DepositType depositType;

    public PlayerDepositEvent(Player player, DepositType depositType) {
        this.player = player;
        this.depositType = depositType;
    }

    public DepositType getDepositType() {
        return depositType;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;

    }
}
