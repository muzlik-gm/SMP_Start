package com.muzlik.smpstart.state;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.data.StateData;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
    private int countdownTotalSeconds;
    
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
        
        if (stateData.isSmpStarted()) {
            plugin.getLogger().info("Cannot start countdown - SMP already started");
            return;
        }
        
        // Stop reminders once countdown begins
        if (plugin.getReminderSystem() != null) {
            plugin.getReminderSystem().stopReminders();
        }
        
        int duration = plugin.getConfigManager().getCountdownDuration();
        countdownTotalSeconds = duration;
        stateData.setCurrentState(PluginState.COUNTDOWN);
        stateData.setCountdownStartTime(System.currentTimeMillis());
        stateData.setRemainingCountdown(duration);
        
        // Set pre-start border
        if (plugin.getBorderManager() != null) {
            plugin.getBorderManager().setPreStartBorder();
        }
        
        // Start countdown timer
        scheduleCountdownTask();
        persistState("Countdown started");
        
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
            plugin.getEffectSystem().updateCountdownBossBar(remaining, countdownTotalSeconds);
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
        
        if (plugin.getEffectSystem() != null) {
            plugin.getEffectSystem().clearCountdownBossBar();
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
        persistState("Countdown completed");
        applyPhaseSettings(true);
        
        plugin.getLogger().info("SMP countdown completed! Starting cooldown period.");
    }
    
    @Override
    public void startCooldown() {
        int duration = plugin.getConfigManager().getCooldownDuration();
        stateData.setCurrentState(PluginState.COOLDOWN);
        stateData.setCooldownStartTime(System.currentTimeMillis());
        stateData.setRemainingCooldown(duration);
        
        // Start cooldown timer
        scheduleCooldownTask();
        persistState("Cooldown started");
    }
    
    @Override
    public boolean cancelCountdown() {
        if (getCurrentState() != PluginState.COUNTDOWN) {
            return false;
        }
        
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        
        if (plugin.getEffectSystem() != null) {
            plugin.getEffectSystem().clearCountdownBossBar();
        }
        
        stateData.setCurrentState(PluginState.IDLE);
        stateData.setRemainingCountdown(0);
        stateData.setCountdownStartTime(0);
        
        if (plugin.getBorderManager() != null) {
            plugin.getBorderManager().setPreStartBorder();
        }
        
        if (plugin.getBlockProtectionManager() != null) {
            plugin.getBlockProtectionManager().enableBlockProtection();
        }
        
        if (!stateData.isSmpStarted() && plugin.getReminderSystem() != null && plugin.getConfigManager().areJoinRemindersEnabled()) {
            plugin.getReminderSystem().startReminders();
        }
        
        persistState("Countdown cancelled");
        plugin.getLogger().info("SMP countdown cancelled");
        return true;
    }
    
    @Override
    public boolean resetSmp() {
        // Cancel any running tasks
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        if (cooldownTask != null) {
            cooldownTask.cancel();
            cooldownTask = null;
        }
        
        if (plugin.getEffectSystem() != null) {
            plugin.getEffectSystem().clearCountdownBossBar();
        }
        
        // Stop PvP protection if active
        if (plugin.getPvPManager() != null) {
            plugin.getPvPManager().stopPvPProtection();
        }
        
        // Reset state data
        stateData.setCurrentState(PluginState.IDLE);
        stateData.setCountdownStartTime(0);
        stateData.setCooldownStartTime(0);
        stateData.setRemainingCountdown(0);
        stateData.setRemainingCooldown(0);
        stateData.setSmpStartTime(0);
        stateData.setSmpStarted(false);
        stateData.setPvpProtectionActive(false);
        
        // Reset border and protections
        if (plugin.getBorderManager() != null) {
            plugin.getBorderManager().setPreStartBorder();
        }
        if (plugin.getBlockProtectionManager() != null) {
            plugin.getBlockProtectionManager().enableBlockProtection();
        }
        applyPhaseSettings(false);
        
        // Teleport all online players to a safe spawn location
        World targetWorld = getTeleportWorld();
        if (targetWorld != null) {
            Location spawn = getSafeTeleportLocation(targetWorld);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(spawn);
            }
        }
        
        // Restart reminders if enabled
        if (plugin.getReminderSystem() != null && plugin.getConfigManager().areJoinRemindersEnabled()) {
            plugin.getReminderSystem().stopReminders();
            plugin.getReminderSystem().startReminders();
        }
        
        // Clear saved state file and persist reset
        if (plugin.getDataManager() != null) {
            plugin.getDataManager().clearSavedState();
        }
        persistState("SMP reset");
        
        plugin.getLogger().info("SMP reset completed");
        return true;
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
        persistState("Cooldown completed");
        
        plugin.getLogger().info("Cooldown period completed. /smpstart is now available again.");
    }
    
    @Override
    public boolean canExecuteStart() {
        return getCurrentState() == PluginState.IDLE && !stateData.isSmpStarted();
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
     * Resume countdown or cooldown after a server restart.
     * This prevents the plugin from getting stuck in COUNTDOWN/COOLDOWN without timers.
     */
    public void resumeStateAfterLoad() {
        if (stateData.getCurrentState() == PluginState.COUNTDOWN) {
            resumeCountdownAfterLoad();
        } else if (stateData.getCurrentState() == PluginState.COOLDOWN) {
            resumeCooldownAfterLoad();
        }
        
        applyPhaseSettings(stateData.isSmpStarted());
    }
    
    private void resumeCountdownAfterLoad() {
        int durationSeconds = plugin.getConfigManager().getCountdownDuration();
        int remaining = calculateRemainingSeconds(
            stateData.getCountdownStartTime(),
            durationSeconds,
            stateData.getRemainingCountdown()
        );
        
        if (remaining <= 0) {
            handleCountdownExpiredDuringDowntime(durationSeconds);
            return;
        }
        
        countdownTotalSeconds = durationSeconds;
        stateData.setCurrentState(PluginState.COUNTDOWN);
        if (stateData.getCountdownStartTime() <= 0) {
            long startTime = System.currentTimeMillis() - (durationSeconds - remaining) * 1000L;
            stateData.setCountdownStartTime(startTime);
        }
        
        stateData.setRemainingCountdown(remaining);
        
        // Ensure pre-start border is set while countdown resumes
        if (plugin.getBorderManager() != null) {
            plugin.getBorderManager().setPreStartBorder();
        }
        
        scheduleCountdownTask();
        plugin.getLogger().info("Resumed SMP countdown with " + remaining + " seconds remaining");
    }
    
    private void resumeCooldownAfterLoad() {
        int durationSeconds = plugin.getConfigManager().getCooldownDuration();
        int remaining = calculateRemainingSeconds(
            stateData.getCooldownStartTime(),
            durationSeconds,
            stateData.getRemainingCooldown()
        );
        
        if (remaining <= 0) {
            stateData.setCurrentState(PluginState.IDLE);
            stateData.setRemainingCooldown(0);
            persistState("Cooldown expired during downtime");
            plugin.getLogger().info("Cooldown expired during downtime. /smpstart is now available again.");
            return;
        }
        
        stateData.setCurrentState(PluginState.COOLDOWN);
        if (stateData.getCooldownStartTime() <= 0) {
            long startTime = System.currentTimeMillis() - (durationSeconds - remaining) * 1000L;
            stateData.setCooldownStartTime(startTime);
        }
        
        stateData.setRemainingCooldown(remaining);
        scheduleCooldownTask();
        plugin.getLogger().info("Resumed SMP cooldown with " + remaining + " seconds remaining");
    }
    
    private void handleCountdownExpiredDuringDowntime(int countdownDurationSeconds) {
        long now = System.currentTimeMillis();
        long countdownStart = stateData.getCountdownStartTime();
        long smpStartTime = countdownStart > 0
            ? countdownStart + countdownDurationSeconds * 1000L
            : now;
        
        stateData.setSmpStartTime(smpStartTime);
        stateData.setSmpStarted(true);
        stateData.setRemainingCountdown(0);
        
        if (plugin.getReminderSystem() != null) {
            plugin.getReminderSystem().stopReminders();
        }
        
        if (plugin.getBorderManager() != null) {
            plugin.getBorderManager().setFinalBorder();
        }
        
        if (plugin.getBlockProtectionManager() != null) {
            plugin.getBlockProtectionManager().disableBlockProtection();
        }
        
        // Resume PvP protection if it should still be active
        if (plugin.getPvPManager() != null) {
            resumePvPProtection(smpStartTime);
        }
        
        applyPhaseSettings(true);
        
        // Determine cooldown status
        int cooldownDurationSeconds = plugin.getConfigManager().getCooldownDuration();
        long cooldownStart = stateData.getCooldownStartTime();
        if (cooldownStart <= 0) {
            cooldownStart = smpStartTime;
            stateData.setCooldownStartTime(cooldownStart);
        }
        
        int remainingCooldown = calculateRemainingSeconds(
            cooldownStart,
            cooldownDurationSeconds,
            stateData.getRemainingCooldown()
        );
        
        if (remainingCooldown > 0) {
            stateData.setCurrentState(PluginState.COOLDOWN);
            stateData.setRemainingCooldown(remainingCooldown);
            scheduleCooldownTask();
            plugin.getLogger().info("Countdown finished during downtime. Resumed cooldown with " + remainingCooldown + " seconds remaining");
        } else {
            stateData.setCurrentState(PluginState.IDLE);
            stateData.setRemainingCooldown(0);
            plugin.getLogger().info("Countdown and cooldown finished during downtime. /smpstart is available.");
        }
        
        persistState("Countdown expired during downtime");
    }
    
    private int calculateRemainingSeconds(long startTimeMs, int durationSeconds, int fallbackRemaining) {
        if (startTimeMs <= 0) {
            return Math.max(0, fallbackRemaining);
        }
        long elapsedSeconds = (System.currentTimeMillis() - startTimeMs) / 1000;
        return (int) Math.max(0, durationSeconds - elapsedSeconds);
    }
    
    private void scheduleCountdownTask() {
        if (countdownTask != null) {
            countdownTask.cancel();
        }
        
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                handleCountdownTick();
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second (20 ticks)
    }
    
    private void scheduleCooldownTask() {
        if (cooldownTask != null) {
            cooldownTask.cancel();
        }
        
        cooldownTask = new BukkitRunnable() {
            @Override
            public void run() {
                handleCooldownTick();
            }
        }.runTaskTimer(plugin, 20L, 20L); // Run every second starting after 1 second
    }
    
    private void persistState(String context) {
        if (plugin.getDataManager() != null) {
            plugin.getDataManager().saveState(stateData);
            plugin.getLogger().info("State persisted: " + context);
        }
    }
    
    private void applyPhaseSettings(boolean started) {
        World world = getTeleportWorld();
        if (world == null) {
            return;
        }
        
        // Apply difficulty
        String difficultyName = started
            ? plugin.getConfigManager().getStartedDifficulty()
            : plugin.getConfigManager().getStartingDifficulty();
        Difficulty difficulty = parseDifficulty(difficultyName, started ? Difficulty.NORMAL : Difficulty.PEACEFUL);
        world.setDifficulty(difficulty);
        
        // Apply mob spawning gamerule
        boolean disableMobSpawning = started
            ? plugin.getConfigManager().isStartedDisableMobSpawning()
            : plugin.getConfigManager().isStartingDisableMobSpawning();
        world.setGameRule(GameRule.DO_MOB_SPAWNING, !disableMobSpawning);
    }
    
    private Difficulty parseDifficulty(String name, Difficulty fallback) {
        if (name == null) {
            return fallback;
        }
        try {
            return Difficulty.valueOf(name.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }
    
    private World getTeleportWorld() {
        String targetWorldName = plugin.getConfigManager().getWorldName();
        if (targetWorldName != null && !targetWorldName.trim().isEmpty()) {
            World world = Bukkit.getWorld(targetWorldName.trim());
            if (world != null) {
                return world;
            }
        }
        
        if (!Bukkit.getWorlds().isEmpty()) {
            return Bukkit.getWorlds().get(0);
        }
        
        return null;
    }
    
    private Location getSafeTeleportLocation(World world) {
        Location spawn = world.getSpawnLocation();
        try {
            if (world.getWorldBorder().isInside(spawn)) {
                return spawn;
            }
        } catch (Exception e) {
            return spawn;
        }
        
        Location center = world.getWorldBorder().getCenter();
        int x = center.getBlockX();
        int z = center.getBlockZ();
        int y = world.getHighestBlockYAt(x, z) + 1;
        if (y < world.getMinHeight()) {
            y = world.getMinHeight();
        }
        return new Location(world, x + 0.5, y, z + 0.5);
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
        if (plugin.getEffectSystem() != null) {
            plugin.getEffectSystem().clearCountdownBossBar();
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
                stateData.setPvpProtectionActive(true);
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
