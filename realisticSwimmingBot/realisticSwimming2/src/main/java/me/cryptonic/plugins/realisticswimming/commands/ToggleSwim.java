package me.cryptonic.plugins.realisticswimming.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import me.cryptonic.plugins.realisticswimming.Language;
import me.cryptonic.plugins.realisticswimming.events.PlayerDisableSwimmingEvent;
import me.cryptonic.plugins.realisticswimming.events.PlayerEnableSwimmingEvent;
public class ToggleSwim extends RSCommand {
    private Plugin plugin;

    private FixedMetadataValue meta;

    public ToggleSwim(Plugin pl) {
        this.plugin = pl;
        this.meta = new FixedMetadataValue(this.plugin, null);
    }

    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] arg3) {
        if (sender instanceof Player && arg3.length > 0) {
            Player p = (Player)sender;
            if (arg3[0].equalsIgnoreCase("on")) {
                p.removeMetadata("swimmingDisabled", this.plugin);
                sendMessage(p, ChatColor.translateAlternateColorCodes('&', Language.swimmingEnabled));
                PlayerEnableSwimmingEvent event = new PlayerEnableSwimmingEvent(p);
                Bukkit.getServer().getPluginManager().callEvent((Event)event);
                return true;
            }
            if (arg3[0].equalsIgnoreCase("off")) {
                p.setMetadata("swimmingDisabled", (MetadataValue)this.meta);
                sendMessage(p, ChatColor.translateAlternateColorCodes('&', Language.swimmingDisabled));
                PlayerDisableSwimmingEvent event = new PlayerDisableSwimmingEvent(p);
                Bukkit.getServer().getPluginManager().callEvent((Event)event);
                return true;
            }
        }
        return false;
    }
}
