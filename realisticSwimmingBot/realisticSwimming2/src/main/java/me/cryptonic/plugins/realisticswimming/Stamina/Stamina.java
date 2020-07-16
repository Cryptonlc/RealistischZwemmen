package me.cryptonic.plugins.realisticswimming.Stamina;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;
import me.cryptonic.plugins.realisticswimming.Config;
import me.cryptonic.plugins.realisticswimming.Language;
import me.cryptonic.plugins.realisticswimming.events.PlayerOutOfStaminaEvent;
import me.cryptonic.plugins.realisticswimming.events.PlayerStaminaRefreshEvent;
import me.cryptonic.plugins.realisticswimming.events.PlayerStopSwimmingEvent;

import me.cryptonic.plugins.realisticswimming.main.RSwimListener;


public class Stamina extends BukkitRunnable {
    private Plugin plugin;

    private RSwimListener sl;

    private Player p;

    private float stamina = 1000.0F;

    private int staminaResetTimer;

    private float staminaRefillStepSize;

    private Scoreboard scoreboard;

    private Objective staminaObjective;

    private String oldObjectiveName = "";

    private StaminaBar staminaBar;

    private WeightManager weightManager;

    public Stamina(Plugin pl, Player player, RSwimListener swimListener) {
        this.plugin = pl;
        this.p = player;
        this.sl = swimListener;
        this.weightManager = new WeightManager(this.p);
    }

    public void run() {
        if (this.sl.playerCanSwim(this.p) && this.p.isOnline()) {
            this.staminaResetTimer = Config.normalStaminaResetTimeSeconds * 20 / Config.staminaUpdateDelay;
            if (Config.enableStamina)
                displayStamina();
            adjustSpeedToWeight();
            if (this.stamina > 0.0F) {
                if (this.p.isSprinting()) {
                    this.stamina -= Config.sprintStaminaUsage / 20.0F * Config.staminaUpdateDelay;
                } else {
                    this.stamina -= Config.swimStaminaUsage / 20.0F * Config.staminaUpdateDelay;
                }
                if (Config.enableArmorWeight)
                    this.stamina -= this.weightManager.getWeight();
            } else if (Config.enableStamina) {
                drown(this.p);
                PlayerOutOfStaminaEvent event = new PlayerOutOfStaminaEvent(this.p);
                Bukkit.getServer().getPluginManager().callEvent((Event)event);
            }
            this.staminaRefillStepSize = (1000.0F - this.stamina) / this.staminaResetTimer;
        } else if (this.staminaResetTimer == 0 || !this.p.isOnline()) {
            if (Config.enableStamina) {
                hideStaminaBar();
                PlayerStaminaRefreshEvent event = new PlayerStaminaRefreshEvent(this.p);
                Bukkit.getServer().getPluginManager().callEvent((Event)event);
            }
            if (this.p.hasMetadata("swimming")) {
                PlayerStopSwimmingEvent event = new PlayerStopSwimmingEvent(this.p);
                Bukkit.getServer().getPluginManager().callEvent((Event)event);
            }
            this.p.removeMetadata("swimming", this.plugin);
            cancel();
        } else {
            this.staminaResetTimer--;
            this.stamina = Math.min(this.stamina + this.staminaRefillStepSize, 1000.0F);
        }
        if (Config.enableStamina)
            displayStamina();
    }

    private void adjustSpeedToWeight() {
        if (this.weightManager.getWeight() > Config.maxSprintingWeight && Config.enableArmorWeight) {
            if (this.p.isSprinting() && Config.enableToHeavyToSprintWarning) {
                this.p.sendMessage(ChatColor.RED + ChatColor.translateAlternateColorCodes('&', Language.tooHeavyToSprint));
                this.p.sendMessage(ChatColor.GOLD + ChatColor.translateAlternateColorCodes('&', Language.currentArmorWeight) + " " + ChatColor.RED + this.weightManager.getWeight() + " " + ChatColor.GOLD + ChatColor.translateAlternateColorCodes('&', Language.maximumSprintingWeightIs) + " " + ChatColor.AQUA + Config.maxSprintingWeight);
            }
            this.p.setSprinting(false);
        }
    }

    private void displayStamina() {
        String part1 = "";
        String part2 = "";
        int i;
        for (i = 0; i < this.stamina / 100.0F; i++)
            part1 = part1 + "#";
        for (i = 0; i < 10 - part1.length(); i++)
            part2 = part2 + "#";
        String staminaBar = part1 + ChatColor.GRAY + part2;
        if (this.stamina > 700.0F) {
            staminaBar = ChatColor.GREEN + staminaBar;
        } else if (this.stamina > 400.0F) {
            staminaBar = ChatColor.GOLD + staminaBar;
        } else if (this.stamina > 200.0F) {
            staminaBar = ChatColor.RED + staminaBar;
        } else {
            staminaBar = ChatColor.DARK_RED + staminaBar;
        }
        staminaBar = ChatColor.translateAlternateColorCodes('&', Language.stamina) + ChatColor.RESET + ": " + staminaBar;
        updateStaminaBar(staminaBar);
    }

    private void drown(Player p) {
        p.setSprinting(false);
        if (Config.enableDrowning) {
            p.setVelocity(new Vector(0, -1, 0));
            this.staminaResetTimer = Config.drownedStaminaResetTimeSeconds * 20 / Config.staminaUpdateDelay;
        }
    }

    private void updateStaminaBar(String title) {
        if (Config.enableBossBar) {
            if (this.staminaBar == null) {
                initializeBossBar();
            } else {
                this.staminaBar.updateBar(this.stamina);
            }
        } else {
            updateScoreboard(title);
        }
    }

    private void updateScoreboard(String title) {
        try {
            this.scoreboard = this.p.getScoreboard();
            this.staminaObjective = this.scoreboard.getObjective(DisplaySlot.SIDEBAR);
            this.scoreboard.resetScores(this.oldObjectiveName);
            this.staminaObjective.getScore(title).setScore((int)this.stamina);
            this.oldObjectiveName = title;
        } catch (NullPointerException e) {
            initializeScoreboard();
        }
    }

    private void initializeScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.p.setScoreboard(this.scoreboard);
        if (ChatColor.translateAlternateColorCodes('&', Language.stamina).length() > 16) {
            this.staminaObjective = this.scoreboard.registerNewObjective(ChatColor.translateAlternateColorCodes('&', Language.stamina).substring(0, 15), "dummy");
        } else {
            this.staminaObjective = this.scoreboard.registerNewObjective(ChatColor.translateAlternateColorCodes('&', Language.stamina), "dummy");
        }
        this.staminaObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private void initializeBossBar() {
        this.staminaBar = StaminaBar.getNewStaminaBar(this.p);
    }

    private void hideStaminaBar() {
        try {
            if (Config.enableBossBar) {
                this.staminaBar.removeStaminaBar();
            } else {
                this.scoreboard.resetScores(this.oldObjectiveName);
            }
        } catch (NullPointerException nullPointerException) {}
    }
}
