package me.cryptonic.plugins.realisticswimming.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import me.cryptonic.plugins.realisticswimming.Config;

public class PlayerOutOfStaminaEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private Player p;

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public PlayerOutOfStaminaEvent(Player player) {
        this.p = player;
    }

    public Player getPlayer() {
        return this.p;
    }

    public boolean isDrowningEnabled() {
        return Config.enableDrowning;
    }
}
