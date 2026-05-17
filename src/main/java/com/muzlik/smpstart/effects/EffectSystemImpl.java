package com.muzlik.smpstart.effects;

import com.muzlik.smpstart.SMPStartPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

/**
 * Implementation of EffectSystem that handles audio-visual effects
 */
public class EffectSystemImpl implements EffectSystem {
    
    private final SMPStartPlugin plugin;
    private BossBar countdownBossBar;
    
    public EffectSystemImpl(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void playCountdownTick(int remaining) {
        try {
            // Play different sounds based on remaining time
            Sound sound;
            float pitch;
            
            if (remaining <= 3) {
                // High-pitched urgent sound for final countdown
                sound = Sound.BLOCK_NOTE_BLOCK_PLING;
                pitch = 2.0f;
            } else if (remaining <= 5) {
                // Medium-pitched sound for approaching end
                sound = Sound.BLOCK_NOTE_BLOCK_PLING;
                pitch = 1.5f;
            } else {
                // Normal countdown sound
                sound = Sound.BLOCK_NOTE_BLOCK_PLING;
                pitch = 1.0f;
            }
            
            // Play sound to all players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), sound, 1.0f, pitch);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to play countdown tick sound: " + e.getMessage());
        }
    }
    
    @Override
    public void playStartSound() {
        try {
            // Play totem pop sound when SMP starts
            Sound sound = Sound.ITEM_TOTEM_USE;
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to play start sound: " + e.getMessage());
        }
    }
    
    @Override
    public void playTotemPopSound() {
        try {
            // Play totem pop sound when cooldown expires
            Sound sound = Sound.ITEM_TOTEM_USE;
            
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to play totem pop sound: " + e.getMessage());
        }
    }
    
    @Override
    public void showCountdownTitle(int remaining) {
        try {
            String title;
            String subtitle;
            ChatColor color;
            
            if (remaining <= 3) {
                color = ChatColor.RED;
                title = color + "" + ChatColor.BOLD + remaining;
                subtitle = ChatColor.YELLOW + "GET READY!";
            } else if (remaining <= 5) {
                color = ChatColor.YELLOW;
                title = color + "" + ChatColor.BOLD + remaining;
                subtitle = ChatColor.WHITE + "SMP Starting Soon...";
            } else {
                color = ChatColor.GREEN;
                title = color + "" + ChatColor.BOLD + remaining;
                subtitle = ChatColor.GRAY + "SMP Starting in...";
            }
            
            // Show title to all players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(title, subtitle, 5, 15, 5); // fadeIn, stay, fadeOut in ticks
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to show countdown title: " + e.getMessage());
        }
    }
    
    @Override
    public void showStartAnimation() {
        try {
            String title = ChatColor.GOLD + "" + ChatColor.BOLD + "SMP STARTED!";
            String subtitle = ChatColor.GREEN + "Good luck, have fun!";
            
            // Show start animation to all players
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle(title, subtitle, 10, 40, 10); // Longer display for start
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to show start animation: " + e.getMessage());
        }
    }
    
    @Override
    public void broadcastCountdownMessage(int remaining) {
        try {
            String message;
            ChatColor color;
            
            if (remaining <= 3) {
                color = ChatColor.RED;
                message = color + "" + ChatColor.BOLD + "[MSS] " + remaining + "s!";
            } else if (remaining <= 10) {
                color = ChatColor.YELLOW;
                message = color + "[MSS] Starting in " + remaining + "s...";
            } else {
                color = ChatColor.GREEN;
                message = color + "[MSS] Starting in " + remaining + "s";
            }
            
            // Broadcast to all players
            Bukkit.broadcastMessage(message);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to broadcast countdown message: " + e.getMessage());
        }
    }
    
    @Override
    public void broadcastStartMessage() {
        try {
            String message = ChatColor.GOLD + "" + ChatColor.BOLD + "[MSS] " +
                           ChatColor.GREEN + "The SMP has officially started! Good luck everyone!";
            
            // Broadcast to all players
            Bukkit.broadcastMessage(message);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to broadcast start message: " + e.getMessage());
        }
    }
    
    @Override
    public void updateCountdownBossBar(int remaining, int totalSeconds) {
        if (!plugin.getConfigManager().isCountdownBossBarEnabled()) {
            clearCountdownBossBar();
            return;
        }
        
        if (totalSeconds <= 0) {
            return;
        }
        
        if (countdownBossBar == null) {
            countdownBossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
            countdownBossBar.setVisible(true);
        }
        
        double progress = Math.max(0.0, Math.min(1.0, (double) remaining / totalSeconds));
        countdownBossBar.setProgress(progress);
        
        BarColor color;
        String title;
        if (remaining <= 3) {
            color = BarColor.RED;
            title = ChatColor.RED + "" + ChatColor.BOLD + "Starting in " + remaining + "s";
        } else if (remaining <= 5) {
            color = BarColor.YELLOW;
            title = ChatColor.YELLOW + "Starting in " + remaining + "s";
        } else {
            color = BarColor.GREEN;
            title = ChatColor.GREEN + "Starting in " + remaining + "s";
        }
        
        countdownBossBar.setColor(color);
        countdownBossBar.setTitle(title);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!countdownBossBar.getPlayers().contains(player)) {
                countdownBossBar.addPlayer(player);
            }
        }
    }
    
    @Override
    public void clearCountdownBossBar() {
        if (countdownBossBar != null) {
            countdownBossBar.removeAll();
            countdownBossBar.setVisible(false);
            countdownBossBar = null;
        }
    }
}
