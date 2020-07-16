package me.cryptonic.plugins.realisticswimming.events;


public class PlayerStartFallingEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    private Player p;

    private boolean cancelled;

    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public PlayerStartFallingEvent(Player player) {
        this.p = player;
    }

    public Player getPlayer() {
        return this.p;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
