package me.cryptonic.plugins.realisticswimming.main;

import org.bukkit.plugin.java.JavaPlugin;

public final class RSMain extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getConsoleSender().sendMessage("Plugin is aan");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
