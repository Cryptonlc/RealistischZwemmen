package me.cryptonic.plugins.realisticswimming.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import me.cryptonic.plugins.realisticswimming.main.RSMain;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {
    private RSMain main;

    public Reload(RSMain rsMain) {
        this.main = rsMain;
    }

    public boolean onCommand(CommandSender sender, Command arg1, String label, String[] arg3) {
        if ((sender instanceof org.bukkit.entity.Player || sender instanceof org.bukkit.command.ConsoleCommandSender) && arg3.length > 0 && arg3[0].equalsIgnoreCase("reload")) {
            this.main.reloadConfig();
            this.main.loadConfig(;)
            sender.sendMessage(ChatColor.AQUA + "[Realistic Swimming] " + ChatColor.GREEN + "Configuration reloaded!");
            return true;
        }
        sender.sendMessage("Commands:");
        sender.sendMessage("/rs reload - reloads the config");
        return true;
    }

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
}