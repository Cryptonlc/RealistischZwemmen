package me.cryptonic.plugins.realisticswimming.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import me.cryptonic.plugins.realisticswimming.Config;
import me.cryptonic.plugins.realisticswimming.Utility;
import me.cryptonic.plugins.realisticswimming.events.PlayerStartFallingEvent;

import java.awt.*;

public class RFallListener implements Listener {
    private Plugin plugin;

    public RFallListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        if (playerCanFall(p)) {
            p.setGliding(true);
            FixedMetadataValue m = new FixedMetadataValue(this.plugin, null);
            p.setMetadata("falling", (MetadataValue)m);
        } else if (p.hasMetadata("falling")) {
            p.removeMetadata("falling", this.plugin);
        }
    }

    @EventHandler
    public void onEntityToggleGlideEvent(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            if (playerCanFall(p) && p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() != Material.WATER) {
                PlayerStartFallingEvent e = new PlayerStartFallingEvent(p);
                Bukkit.getServer().getPluginManager().callEvent((Event)e);
                if (!e.isCancelled()) {
                    p.setVelocity(new Vector(p.getLocation().getDirection().getX() * Config.fallGlideSpeed, Config.fallDownwardSpeed * -1.0D, p.getLocation().getDirection().getZ() * Config.fallGlideSpeed));
                    event.setCancelled(true);
                } else {
                    p.setGliding(false);
                }
            }
        }
    }

    public boolean playerCanFall(Player p) {
        if (!p.hasMetadata("fallingDisabled") && Utility.playerHasPermission(p, "rs.user.fall") && p.getFallDistance() > Config.minFallDistance && Config.enableFall && p.getLocation().getBlock().getType() != Material.WATER && p.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.AIR) {
            if (isElytraDeploying(p))
                return false;
            return true;
        }
        return false;
    }

    public boolean isElytraDeploying(Player p) {
        if (Bukkit.getPluginManager().isPluginEnabled("Elytra") &&
                p.hasPermission("elytra.auto")) {
            if (Utility.isElytraWeared(p))
                return true;
            if (p.hasPermission("elytra.auto-equip") && Utility.hasElytraStorage(p))
                return true;
        }
        return false;
    }
}
