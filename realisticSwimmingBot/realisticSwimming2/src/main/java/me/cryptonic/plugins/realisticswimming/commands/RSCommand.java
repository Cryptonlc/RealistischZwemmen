package me.cryptonic.plugins.realisticswimming.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class RSCommand implements CommandExecutor {
    public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        return false;
    }

    public void sendMessage(Player p, String message) {
        if (!message.equalsIgnoreCase(""))
            p.sendMessage(message);
    }
}
