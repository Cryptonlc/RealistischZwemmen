package me.cryptonic.plugins.realisticswimming.main;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import me.cryptonic.plugins.realisticswimming.Config;

public class RSneakListener implements Listener {
    RSneakListener(Plugin p) {}

    @EventHandler
    public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
        Player p = event.getPlayer();
        if (!p.isSneaking() && Config.enableSneak)
            p.setGliding(true);
    }

    @EventHandler
    public void onEntityToggleGlideEvent(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            if (p.isSneaking() && Config.enableSneak)
                event.setCancelled(true);
        }
    }
}
