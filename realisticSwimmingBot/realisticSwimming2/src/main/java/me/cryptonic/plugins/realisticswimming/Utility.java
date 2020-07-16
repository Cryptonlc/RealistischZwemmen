package me.cryptonic.plugins.realisticswimming;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Utility {
    public static boolean playerHasPermission(Player p, String perm) {
        if (!Config.permsReq)
            return true;
        if (p.hasPermission(perm))
            return true;
        return false;
    }

    public static boolean playerIsInCreativeMode(Player p) {
        if (Config.enabledInCreative)
            return false;
        if (p.getGameMode() == GameMode.CREATIVE)
            return true;
        return false;
    }

    public static boolean isElytraWeared(Player player) {
        if (player.getInventory().getChestplate() == null)
            return false;
        if (player.getInventory().getChestplate().getType() != Material.ELYTRA)
            return false;
        if (player.getInventory().getChestplate().getDurability() >= 431)
            return false;
        return true;
    }

    public static boolean hasElytraStorage(Player player) {
        PlayerInventory inv = player.getInventory();
        if (inv.getStorageContents() != null)
            for (ItemStack item : inv.getStorageContents()) {
                if (item != null &&
                        !item.getType().equals(Material.AIR) &&
                        item.getType().equals(Material.ELYTRA) &&
                        item.getDurability() <= 431)
                    return true;
            }
        return false;
    }
}