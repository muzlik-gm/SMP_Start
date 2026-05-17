package com.muzlik.smpstart.pvp;

import com.muzlik.smpstart.SMPStartPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Implementation of PvPManager that handles PvP protection
 */
public class PvPManagerImpl implements PvPManager, Listener {
    
    private final SMPStartPlugin plugin;
    private boolean pvpProtectionActive = false;
    private long protectionStartTime;
    private BukkitTask protectionTask;
    private BossBar pvpBossBar;
    private BukkitTask bossBarUpdateTask;
    
    public PvPManagerImpl(SMPStartPlugin plugin) {
        this.plugin = plugin;
        // Register event listener
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public void startPvPProtection() {
        startPvPProtection(System.currentTimeMillis(), false);
    }
    
    /**
     * Start PvP protection with a specific start time (for resuming after restart)
     * @param startTime when the protection originally started
     */
    public void startPvPProtection(long startTime) {
        startPvPProtection(startTime, true);
    }
    
    private void startPvPProtection(long startTime, boolean resumed) {
        int durationMinutes = plugin.getConfigManager().getPvpProtectionDuration();
        
        if (durationMinutes <= 0) {
            plugin.getLogger().info("PvP protection is disabled in config");
            return;
        }
        
        // Prevent duplicate boss bars or timers if protection is already active
        if (pvpProtectionActive) {
            if (protectionTask != null) {
                protectionTask.cancel();
                protectionTask = null;
            }
            removePvPBossBar();
        }
        
        pvpProtectionActive = true;
        protectionStartTime = startTime;
        
        // Check if protection should still be active
        long elapsedMs = System.currentTimeMillis() - startTime;
        long elapsedMinutes = elapsedMs / (60 * 1000);
        
        if (elapsedMinutes >= durationMinutes) {
            // Protection period has already expired
            plugin.getLogger().info("PvP protection period has already expired");
            stopPvPProtection();
            return;
        }
        
        int remainingMinutes = (int) (durationMinutes - elapsedMinutes);
        
        // Disable PvP in target worlds
        for (World world : getTargetWorlds()) {
            world.setPVP(false);
        }
        
        // Broadcast protection start/resume
        String message;
        if (!resumed) {
            // New protection
            message = ChatColor.YELLOW + "[MSS] " + ChatColor.GREEN + "PvP protection active for " +
                     durationMinutes + " minutes. Players cannot damage each other!";
        } else {
            // Resumed protection
            message = ChatColor.YELLOW + "[MSS] " + ChatColor.GREEN + "PvP protection resumed — " +
                     remainingMinutes + " minutes remaining.";
        }
        Bukkit.broadcastMessage(message);
        
        // Create and show boss bar
        createPvPBossBar(durationMinutes);
        
        // Schedule protection end based on remaining time
        long remainingTicks = remainingMinutes * 60 * 20L; // Convert minutes to ticks
        protectionTask = new BukkitRunnable() {
            @Override
            public void run() {
                stopPvPProtection();
            }
        }.runTaskLater(plugin, remainingTicks);
        
        // Schedule warning messages based on remaining time
        scheduleWarningMessages(remainingMinutes);
        
        plugin.getLogger().info("Started PvP protection for " + remainingMinutes + " minutes (total: " + durationMinutes + ")");
    }
    
    @Override
    public void stopPvPProtection() {
        if (!pvpProtectionActive) {
            return;
        }
        
        pvpProtectionActive = false;
        
        // Cancel protection task if running
        if (protectionTask != null) {
            protectionTask.cancel();
            protectionTask = null;
        }
        
        // Remove boss bar
        removePvPBossBar();
        
        // Enable PvP in target worlds
        for (World world : getTargetWorlds()) {
            world.setPVP(true);
        }
        
        // Broadcast protection end
        String message = ChatColor.YELLOW + "[MSS] " + ChatColor.RED + "PvP protection has ended! " +
                        ChatColor.YELLOW + "Watch your back!";
        Bukkit.broadcastMessage(message);
        
        plugin.getLogger().info("PvP protection ended - PvP is now enabled");
    }
    
    @Override
    public boolean isPvPProtectionActive() {
        return pvpProtectionActive;
    }
    
    @Override
    public int getRemainingProtectionTime() {
        if (!pvpProtectionActive) {
            return 0;
        }
        
        int durationMinutes = plugin.getConfigManager().getPvpProtectionDuration();
        long elapsedMs = System.currentTimeMillis() - protectionStartTime;
        long elapsedMinutes = elapsedMs / (60 * 1000);
        
        return Math.max(0, durationMinutes - (int) elapsedMinutes);
    }
    
    /**
     * Schedule warning messages before PvP protection ends
     * @param durationMinutes total protection duration
     */
    private void scheduleWarningMessages(int durationMinutes) {
        // Warning at 5 minutes remaining
        if (durationMinutes > 5) {
            long delay = (durationMinutes - 5) * 60 * 20L;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (pvpProtectionActive) {
                    String msg = ChatColor.YELLOW + "[MSS] " + ChatColor.GOLD +
                                "PvP protection ends in 5 minutes!";
                    Bukkit.broadcastMessage(msg);
                }
            }, delay);
        }
        
        // Warning at 1 minute remaining
        if (durationMinutes > 1) {
            long delay = (durationMinutes - 1) * 60 * 20L;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (pvpProtectionActive) {
                    String msg = ChatColor.YELLOW + "[MSS] " + ChatColor.GOLD +
                                "PvP protection ends in 1 minute!";
                    Bukkit.broadcastMessage(msg);
                }
            }, delay);
        }
        
        // Warning at 30 seconds remaining
        long delay30s = (durationMinutes * 60 - 30) * 20L;
        if (delay30s > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (pvpProtectionActive) {
                    String msg = ChatColor.YELLOW + "[MSS] " + ChatColor.RED +
                                "PvP protection ends in 30 seconds!";
                    Bukkit.broadcastMessage(msg);
                }
            }, delay30s);
        }
    }
    
    /**
     * Event handler to prevent PvP during protection
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!pvpProtectionActive) {
            return;
        }
        
        // Check if this is player vs player damage
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();
            
            // Cancel the damage
            event.setCancelled(true);
            
            // Notify the attacker
            int remaining = getRemainingProtectionTime();
            String message = ChatColor.RED + "PvP is disabled for " +
                           remaining + " more minute" + (remaining != 1 ? "s" : "") + ".";
            attacker.sendMessage(message);
        }
    }
    
    /**
     * Create and display the PvP protection boss bar
     * @param durationMinutes total protection duration in minutes
     */
    private void createPvPBossBar(int durationMinutes) {
        // Create boss bar
        pvpBossBar = Bukkit.createBossBar(
            ChatColor.GREEN + "PvP Protection: " + durationMinutes + " min remaining",
            BarColor.GREEN,
            BarStyle.SOLID
        );
        
        // Add all online players to boss bar
        for (Player player : Bukkit.getOnlinePlayers()) {
            pvpBossBar.addPlayer(player);
        }
        
        // Set initial progress to full
        pvpBossBar.setProgress(1.0);
        pvpBossBar.setVisible(true);
        
        // Start boss bar update task (update every second)
        bossBarUpdateTask = new BukkitRunnable() {
            @Override
            public void run() {
                updatePvPBossBar();
            }
        }.runTaskTimer(plugin, 20L, 20L); // Start after 1 second, repeat every second
        
        plugin.getLogger().info("PvP protection boss bar created and displayed");
    }
    
    /**
     * Update the boss bar with current protection status
     */
    private void updatePvPBossBar() {
        if (pvpBossBar == null || !pvpProtectionActive) {
            return;
        }
        
        int totalMinutes = plugin.getConfigManager().getPvpProtectionDuration();
        long elapsedMs = System.currentTimeMillis() - protectionStartTime;
        long totalMs = totalMinutes * 60 * 1000L;
        long remainingMs = totalMs - elapsedMs;
        
        if (remainingMs <= 0) {
            // Protection should end
            stopPvPProtection();
            return;
        }
        
        // Calculate precise progress
        double progress = (double) remainingMs / totalMs;
        pvpBossBar.setProgress(Math.max(0.0, Math.min(1.0, progress)));
        
        // Calculate remaining time
        long remainingSeconds = remainingMs / 1000;
        long remainingMinutes = remainingSeconds / 60;
        long remainingSecondsOnly = remainingSeconds % 60;
        
        // Update title with remaining time
        String title;
        BarColor color;
        
        if (remainingMinutes > 5) {
            color = BarColor.GREEN;
            title = ChatColor.GREEN + "PvP Protection: " + remainingMinutes + " minutes remaining";
        } else if (remainingMinutes > 1) {
            color = BarColor.YELLOW;
            title = ChatColor.YELLOW + "PvP Protection: " + remainingMinutes + " minutes remaining";
        } else if (remainingMinutes == 1) {
            color = BarColor.YELLOW;
            title = ChatColor.YELLOW + "PvP Protection: 1 minute " + remainingSecondsOnly + " seconds remaining";
        } else {
            // Less than 1 minute - show seconds only
            color = BarColor.RED;
            title = ChatColor.RED + "PvP Protection: " + remainingSeconds + " seconds remaining";
        }
        
        pvpBossBar.setColor(color);
        pvpBossBar.setTitle(title);
        
        // Add any new players who joined
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!pvpBossBar.getPlayers().contains(player)) {
                pvpBossBar.addPlayer(player);
            }
        }
    }
    
    /**
     * Remove the PvP protection boss bar
     */
    private void removePvPBossBar() {
        // Cancel update task
        if (bossBarUpdateTask != null) {
            bossBarUpdateTask.cancel();
            bossBarUpdateTask = null;
        }
        
        // Remove and hide boss bar
        if (pvpBossBar != null) {
            pvpBossBar.removeAll();
            pvpBossBar.setVisible(false);
            pvpBossBar = null;
            plugin.getLogger().info("PvP protection boss bar removed");
        }
    }
    
    private Iterable<World> getTargetWorlds() {
        String configuredWorld = plugin.getConfigManager().getWorldName();
        if (configuredWorld != null && !configuredWorld.trim().isEmpty()) {
            World world = Bukkit.getWorld(configuredWorld.trim());
            if (world != null) {
                return java.util.Collections.singletonList(world);
            }
            plugin.getLogger().warning("Configured world not found: " + configuredWorld + ". Applying PvP to all worlds.");
        }
        return Bukkit.getWorlds();
    }
    
    /**
     * Cleanup method
     */
    public void cleanup() {
        if (protectionTask != null) {
            protectionTask.cancel();
            protectionTask = null;
        }
        removePvPBossBar();
    }
}
