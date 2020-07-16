package me.cryptonic.plugins.realisticswimming.main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import me.cryptonic.plugins.realisticswimming.Config;
import me.cryptonic.plugins.realisticswimming.Utility;
import me.cryptonic.plugins.realisticswimming.events.PlayerStartSwimmingEvent;
import me.cryptonic.plugins.realisticswimming.Stamina.Stamina;

public class RSwimListener implements Listener {
    private Plugin plugin;

    RSwimListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        ItemStack elytra = p.getInventory().getChestplate();
        if (playerCanSwim(p)) {
            if (event.getTo().getY() <= event.getFrom().getY() || Config.enableSwimmingUp) {
                if (!p.hasMetadata("swimmingDisabled") && Utility.playerHasPermission(p, "rs.user.swim")) {
                    if (p.isSprinting()) {
                        p.setGliding(false);
                    } else {
                        p.setGliding(true);
                    }
                    startSwimming(p);
                }
                if (!Config.durabilityLoss && elytra != null && elytra.getType() == Material.ELYTRA && !elytra.getEnchantments().containsKey(Enchantment.DURABILITY)) {
                    ItemMeta meta = elytra.getItemMeta();
                    meta.addEnchant(Enchantment.DURABILITY, 100, true);
                    elytra.setItemMeta(meta);
                }
            } else if (event.getTo().getY() <= 62.0D) {
                p.setGliding(false);
            }
        } else if (!Config.durabilityLoss && elytra != null && elytra.getType() == Material.ELYTRA && elytra.getEnchantmentLevel(Enchantment.DURABILITY) == 100) {
            ItemMeta meta = elytra.getItemMeta();
            meta.removeEnchant(Enchantment.DURABILITY);
            elytra.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onEntityToggleGlideEvent(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            if (playerCanSwim(p) && !p.hasMetadata("swimmingDisabled"))
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        try {
            if (event.getCurrentItem().getType() == Material.ELYTRA && event.getInventory().getHolder() instanceof Player) {
                ItemStack elytra = event.getCurrentItem();
                if (!Config.durabilityLoss && elytra != null && elytra.getType() == Material.ELYTRA && elytra.getEnchantmentLevel(Enchantment.DURABILITY) == 100) {
                    ItemMeta meta = elytra.getItemMeta();
                    meta.removeEnchant(Enchantment.DURABILITY);
                    elytra.setItemMeta(meta);
                }
            }
        } catch (NullPointerException nullPointerException) {}
    }

    @EventHandler
    public void onPlayerToggleSprintEvent(PlayerToggleSprintEvent event) {
        Player p = event.getPlayer();
        if (p.isSwimming() && !event.isSprinting()) {
            p.setSwimming(false);
            p.setGliding(true);
        }
    }

    public void startSwimming(Player p) {
        if (!p.hasMetadata("swimming")) {
            startStaminaSystem(p);
            FixedMetadataValue m = new FixedMetadataValue(this.plugin, null);
            p.setMetadata("swimming", (MetadataValue)m);
            PlayerStartSwimmingEvent event = new PlayerStartSwimmingEvent(p);
            Bukkit.getServer().getPluginManager().callEvent((Event)event);
        }
    }

    public boolean playerCanSwim(Player p) {
        if (p.getLocation().getBlock().getType() == Material.WATER && p.getLocation().subtract(0.0D, Config.minWaterDepth, 0.0D).getBlock().getType() == Material.WATER && p.getVehicle() == null && !Utility.playerIsInCreativeMode(p) && !p.isFlying())
            return !isInWaterElevator(p);
        return false;
    }

    public void boost(Player p) {
        if (Utility.playerHasPermission(p, "rs.user.boost") && Config.enableBoost && p.isSprinting() && (p.getLocation().getDirection().getY() < -0.1D || !Config.ehmCompatibility))
            p.setVelocity(p.getLocation().getDirection().multiply(Config.sprintSpeed));
    }

    public void startStaminaSystem(Player p) {
        if (!Utility.playerHasPermission(p, "rs.bypass.stamina") || !Config.permsReq) {
            Stamina stamina = new Stamina(this.plugin, p, this);
            stamina.runTaskTimer(this.plugin, 0L, Config.staminaUpdateDelay);
        }
    }

    public static boolean isInWaterElevator(Player p) {
        if (!Config.disableSwimInWaterfall)
            return false;
        int width = Config.maxWaterfallDiameter;
        if (p.getLocation().add(width, 0.0D, 0.0D).getBlock().getType() != Material.WATER && p
                .getLocation().add(-width, 0.0D, 0.0D).getBlock().getType() != Material.WATER && p
                .getLocation().add(0.0D, 0.0D, width).getBlock().getType() != Material.WATER && p
                .getLocation().add(0.0D, 0.0D, -width).getBlock().getType() != Material.WATER)
            return true;
        return false;
    }

    @EventHandler
    public void blockRocketBoost(PlayerInteractEvent event) {
        if (event.hasItem() && event.getItem().getType() == Material.FIREWORK_ROCKET && event.getPlayer().hasMetadata("swimming"))
            event.setCancelled(true);
    }
}
