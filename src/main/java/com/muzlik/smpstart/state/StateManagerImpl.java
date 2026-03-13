package com.muzlik.smpstart.state;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.data.StateData;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Implementation of StateManager that handles plugin state and timers
 */
public class StateManagerImpl implements StateManager {
    
    private final SMPStartPlugin plugin;
    private final StateData stateData;
    private BukkitTask countdownTask;
    private BukkitTask cooldownTask;
    
    public StateManagerImpl(SMPStartPlugin plugin) {
        this.plugin = plugin;
        this.stateData = new StateData();
    }
    
    /**
     * Get the current state data for persistence
     * @return current state data
     */
    public StateData getStateData() {
        return stateData;
    }
    
    /**
     * Load state data from persistence
     * @param loadedStateData the state data to load
     */
    public void loadStateData(StateData loadedStateData) {
        if (loadedStateData != null) {
            this.stateData.setCurrentState(loadedStateData.getCurrentState());
            this.stateData.setCountdownStartTime(loadedStateData.getCountdownStartTime());
            this.stateData.setCooldownStartTime(loadedStateData.getCooldownStartTime());
            this.stateData.setRemainingCountdown(loadedStateData.getRemainingCountdown());
            this.stateData.setRemainingCooldown(loadedStateData.getRemainingCooldown());
            this.stateData.setSmpStartTime(loadedStateData.getSmpStartTime());
            this.stateData.setPvpProtectionActive(loadedStateData.isPvpProtectionActive());
            this.stateData.setSmpStarted(loadedStateData.isSmpStarted());
            
            plugin.getLogger().info("Loaded state: " + stateData.getCurrentState() + ", SMP Started: " + stateData.isSmpStarted());
            
            // Handle block protection based on SMP started status
            if (loadedStateData.isSmpStarted() && plugin.getBlockProtectionManager() != null) {
                plugin.getBlockProtectionManager().disableBlockProtection();
            }
            
            // Resume PvP protection if it was active
            if (loadedStateData.isPvpProtectionActive() && loadedStateData.getSmpStartTime() > 0) {
                resumePvPProtection(loadedStateData.getSmpStartTime());
            }
        }
    }
    
    @Override
    public void startCountdown() {
        if (getCurrentState() != PluginState.IDLE) {
            return; // Can only start countdown from IDLE state
        }
        
        int duration = plugin.getConfigManager().getCountdownDuration();
        stateData.setCurrentState(PluginState.COUNTDOWN);
        stateData.setCountdownStartTime(System.currentTimeMillis());
        stateData.setRemainingCountdown(duration);
        
        // Set pre-start border
        if (plugin.getBorderManager() != null) {
            plugin.getBorderManager().setPreStartBorder();
        }
        
        // Start countdown timer
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                handleCountdownTick();
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second (20 ticks)
        
        plugin.getLogger().info("SMP countdown started for " + duration + " seconds");
    }
    
    @Override
    public void handleCountdownTick() {
        if (getCurrentState() != PluginState.COUNTDOWN) {
            return;
        }
        
        int remaining = stateData.getRemainingCountdown();
        
        // Play effects for this tick
        if (plugin.getEffectSystem() != null) {
            plugin.getEffectSystem().playCountdownTick(remaining);
            plugin.getEffectSystem().showCountdownTitle(remaining);
            plugin.getEffectSystem().broadcastCountdownMessage(remaining);
        }
        
        // Decrease remaining time
        remaining--;
        stateData.setRemainingCountdown(remaining);
        
        // Check if countdown is complete
        if (remaining <= 0) {
            completeCountdown();
        }
    }
    
    @Override
    public void completeCountdown() {
        if (getCurrentState() != PluginState.COUNTDOWN) {
            return;
        }
        
        // Cancel countdown timer
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        
        // Transition world border to final size
        if (plugin.getBorderManager() != null) {
            plugin.getBorderManager().transitionToFinalBorder();
        }
        
        // Play completion effects
        if (plugin.getEffectSystem() != null) {
            plugin.getEffectSystem().playStartSound();
            plugin.getEffectSystem().showStartAnimation();
            plugin.getEffectSystem().broadcastStartMessage();
        }
        
        // Stop join reminders
        if (plugin.getReminderSystem() != null) {
            plugin.getReminderSystem().stopReminders();
        }
        
        // Record SMP start time and mark as started
        stateData.setSmpStartTime(System.currentTimeMillis());
        stateData.setSmpStarted(true);
        
        // Start PvP protection
        if (plugin.getPvPManager() != null) {
            plugin.getPvPManager().startPvPProtection();
            stateData.setPvpProtectionActive(true);
        }
        
        // Disable block protection - SMP has officially started
        if (plugin.getBlockProtectionManager() != null) {
            plugin.getBlockProtectionManager().disableBlockProtection();
        }
        
        // Start cooldown
        startCooldown();
        
        plugin.getLogger().info("SMP countdown completed! Starting cooldown period.");
    }
    
    @Override
    public void startCooldown() {
        int duration = plugin.getConfigManager().getCooldownDuration();
        stateData.setCurrentState(PluginState.COOLDOWN);
        stateData.setCooldownStartTime(System.currentTimeMillis());
        stateData.setRemainingCooldown(duration);
        
        // Start cooldown timer
        cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                handleCooldownTick();
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second starting after 1 second
    }
    
    private void handleCooldownTick() {
        if (getCurrentState() != PluginState.COOLDOWN) {
            return;
        }
        
        int remaining = stateData.getRemainingCooldown() - 1;
        stateData.setRemainingCooldown(remaining);
        
        // Check if cooldown is complete
        if (remaining <= 0) {
            completeCooldown();
        }
    }
    
    private void completeCooldown() {
        // Cancel cooldown timer
        if (cooldownTask != null) {
            cooldownTask.cancel();
            cooldownTask = null;
        }
        
        // Play totem pop sound
        if (plugin.getEffectSystem() != null) {
            plugin.getEffectSystem().playTotemPopSound();
        }
        
        // Return to IDLE state
        stateData.setCurrentState(PluginState.IDLE);
        stateData.setRemainingCooldown(0);
        
        plugin.getLogger().info("Cooldown period completed. /smpstart is now available again.");
    }
    
    @Override
    public boolean canExecuteStart() {
        return getCurrentState() == PluginState.IDLE;
    }
    
    @Override
    public PluginState getCurrentState() {
        return stateData.getCurrentState();
    }
    
    @Override
    public int getRemainingCountdown() {
        return stateData.getRemainingCountdown();
    }
    
    @Override
    public int getRemainingCooldown() {
        return stateData.getRemainingCooldown();
    }
    
    /**
     * Cleanup method to cancel any running tasks
     */
    public void cleanup() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        if (cooldownTask != null) {
            cooldownTask.cancel();
            cooldownTask = null;
        }
    }
    
    /**
     * Resume PvP protection after server restart
     * @param smpStartTime when the SMP originally started
     */
    private void resumePvPProtection(long smpStartTime) {
        try {
            int protectionDurationMinutes = plugin.getConfigManager().getPvpProtectionDuration();
            long elapsedMs = System.currentTimeMillis() - smpStartTime;
            long elapsedMinutes = elapsedMs / (60 * 1000);
            
            if (elapsedMinutes < protectionDurationMinutes) {
                // Protection should still be active
                plugin.getLogger().info("Resuming PvP protection (" + (protectionDurationMinutes - elapsedMinutes) + " minutes remaining)");
                if (plugin.getPvPManager() instanceof com.muzlik.smpstart.pvp.PvPManagerImpl) {
                    ((com.muzlik.smpstart.pvp.PvPManagerImpl) plugin.getPvPManager()).startPvPProtection(smpStartTime);
                }
            } else {
                // Protection period has expired
                plugin.getLogger().info("PvP protection period has expired during server downtime");
                stateData.setPvpProtectionActive(false);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to resume PvP protection: " + e.getMessage());
        }
    }
}