package com.muzlik.smpstart.config;

/**
 * Handles all configurable values and persistence
 */
public interface ConfigManager {
    
    /**
     * Get the countdown duration in seconds
     * @return countdown duration
     */
    int getCountdownDuration();
    
    /**
     * Set the countdown duration in seconds
     * @param duration countdown duration
     */
    void setCountdownDuration(int duration);
    
    /**
     * Get the cooldown duration in seconds
     * @return cooldown duration
     */
    int getCooldownDuration();
    
    /**
     * Set the cooldown duration in seconds
     * @param duration cooldown duration
     */
    void setCooldownDuration(int duration);
    
    /**
     * Get the pre-start world border size
     * @return border size in blocks
     */
    double getPreStartBorderSize();
    
    /**
     * Set the pre-start world border size
     * @param size border size in blocks
     */
    void setPreStartBorderSize(double size);
    
    /**
     * Get the final world border size
     * @return border size in blocks
     */
    double getFinalBorderSize();
    
    /**
     * Set the final world border size
     * @param size border size in blocks
     */
    void setFinalBorderSize(double size);
    
    /**
     * Check if join reminders are enabled
     * @return true if enabled, false otherwise
     */
    boolean areJoinRemindersEnabled();
    
    /**
     * Enable or disable join reminders
     * @param enabled true to enable, false to disable
     */
    void setJoinRemindersEnabled(boolean enabled);
    
    /**
     * Get the reminder interval in seconds
     * @return reminder interval
     */
    int getReminderInterval();
    
    /**
     * Set the reminder interval in seconds
     * @param interval reminder interval
     */
    void setReminderInterval(int interval);
    
    /**
     * Get the configured world name for border/PvP effects
     * @return world name, or empty string for default
     */
    String getWorldName();
    
    /**
     * Set the configured world name for border/PvP effects
     * @param worldName world name, or empty to use default
     */
    void setWorldName(String worldName);
    
    /**
     * Get the minimum online players required to start
     * @return minimum player count
     */
    int getMinOnlinePlayers();
    
    /**
     * Set the minimum online players required to start
     * @param minPlayers minimum player count (0 to disable)
     */
    void setMinOnlinePlayers(int minPlayers);
    
    /**
     * Check if the countdown boss bar is enabled
     * @return true if enabled
     */
    boolean isCountdownBossBarEnabled();
    
    /**
     * Enable or disable the countdown boss bar
     * @param enabled true to enable
     */
    void setCountdownBossBarEnabled(boolean enabled);
    
    /**
     * Get border center mode (spawn or fixed)
     * @return center mode
     */
    String getBorderCenterMode();
    
    /**
     * Set border center mode (spawn or fixed)
     * @param mode center mode
     */
    void setBorderCenterMode(String mode);
    
    /**
     * Get fixed border center X coordinate
     * @return X coordinate
     */
    double getBorderCenterX();
    
    /**
     * Get fixed border center Z coordinate
     * @return Z coordinate
     */
    double getBorderCenterZ();
    
    /**
     * Set fixed border center coordinates
     * @param x X coordinate
     * @param z Z coordinate
     */
    void setBorderCenterPosition(double x, double z);
    
    /**
     * Get border transition duration in seconds
     * @return seconds
     */
    int getBorderTransitionSeconds();
    
    /**
     * Set border transition duration in seconds
     * @param seconds seconds
     */
    void setBorderTransitionSeconds(int seconds);
    
    /**
     * Get pre-start border damage amount
     * @return damage amount
     */
    double getPreStartBorderDamageAmount();
    
    /**
     * Get pre-start border damage buffer
     * @return damage buffer
     */
    double getPreStartBorderDamageBuffer();
    
    /**
     * Get border damage amount after start
     * @return damage amount
     */
    double getBorderDamageAmount();
    
    /**
     * Get border damage buffer after start
     * @return damage buffer
     */
    double getBorderDamageBuffer();
    
    /**
     * Set border damage values for pre-start and post-start
     * @param preStartAmount pre-start damage amount
     * @param preStartBuffer pre-start damage buffer
     * @param amount post-start damage amount
     * @param buffer post-start damage buffer
     */
    void setBorderDamageValues(double preStartAmount, double preStartBuffer, double amount, double buffer);
    
    /**
     * Get difficulty name for pre-start phase
     * @return difficulty name
     */
    String getStartingDifficulty();
    
    /**
     * Get difficulty name for post-start phase
     * @return difficulty name
     */
    String getStartedDifficulty();
    
    /**
     * Set difficulty names for phases
     * @param startingDifficulty starting difficulty
     * @param startedDifficulty started difficulty
     */
    void setPhaseDifficulties(String startingDifficulty, String startedDifficulty);
    
    /**
     * Check if mob spawning is disabled before start
     * @return true if disabled
     */
    boolean isStartingDisableMobSpawning();
    
    /**
     * Check if mob spawning is disabled after start
     * @return true if disabled
     */
    boolean isStartedDisableMobSpawning();
    
    /**
     * Check if mob damage is disabled before start
     * @return true if disabled
     */
    boolean isStartingDisableMobDamage();
    
    /**
     * Check if mob damage is disabled after start
     * @return true if disabled
     */
    boolean isStartedDisableMobDamage();
    
    /**
     * Set mob protection toggles for phases
     * @param startingSpawnDisable disable mob spawning before start
     * @param startedSpawnDisable disable mob spawning after start
     * @param startingDamageDisable disable mob damage before start
     * @param startedDamageDisable disable mob damage after start
     */
    void setPhaseMobProtection(boolean startingSpawnDisable, boolean startedSpawnDisable, boolean startingDamageDisable, boolean startedDamageDisable);
    
    /**
     * Save configuration to file
     */
    void saveConfig();
    
    /**
     * Reload configuration from file
     */
    void reloadConfig();
    
    /**
     * Validate a border size value
     * @param size the size to validate
     * @return true if valid (positive), false otherwise
     */
    boolean isValidBorderSize(double size);
    
    /**
     * Get the PvP protection duration in minutes
     * @return protection duration
     */
    int getPvpProtectionDuration();
    
    /**
     * Set the PvP protection duration in minutes
     * @param duration protection duration (0 to disable)
     */
    void setPvpProtectionDuration(int duration);
}
