package com.muzlik.smpstart.data;

/**
 * Data model for plugin configuration
 */
public class PluginConfig {
    
    // Default configuration values
    private int countdownDuration = 10; // seconds
    private int cooldownDuration = 60; // seconds
    private double preStartBorderSize = 10.0; // blocks (diameter)
    private double finalBorderSize = 10000.0; // blocks (diameter)
    private boolean joinRemindersEnabled = true;
    private int reminderInterval = 60; // seconds
    private int pvpProtectionDuration = 30; // minutes
    private String worldName = ""; // empty means default world
    private int minOnlinePlayers = 1;
    private boolean countdownBossBarEnabled = true;
    private String borderCenterMode = "spawn";
    private double borderCenterX = 0.0;
    private double borderCenterZ = 0.0;
    private int borderTransitionSeconds = 10;
    private double preStartBorderDamageAmount = 0.0;
    private double preStartBorderDamageBuffer = 0.0;
    private double borderDamageAmount = 0.2;
    private double borderDamageBuffer = 5.0;
    private String startingDifficulty = "peaceful";
    private String startedDifficulty = "normal";
    private boolean startingDisableMobSpawning = true;
    private boolean startedDisableMobSpawning = false;
    private boolean startingDisableMobDamage = true;
    private boolean startedDisableMobDamage = false;
    private boolean preStartBlockProtectionEnabled = true;
    
    // Getters
    public int getCountdownDuration() {
        return countdownDuration;
    }
    
    public int getCooldownDuration() {
        return cooldownDuration;
    }
    
    public double getPreStartBorderSize() {
        return preStartBorderSize;
    }
    
    public double getFinalBorderSize() {
        return finalBorderSize;
    }
    
    public boolean isJoinRemindersEnabled() {
        return joinRemindersEnabled;
    }
    
    public int getReminderInterval() {
        return reminderInterval;
    }
    
    public int getPvpProtectionDuration() {
        return pvpProtectionDuration;
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public int getMinOnlinePlayers() {
        return minOnlinePlayers;
    }
    
    public boolean isCountdownBossBarEnabled() {
        return countdownBossBarEnabled;
    }
    
    public String getBorderCenterMode() {
        return borderCenterMode;
    }
    
    public double getBorderCenterX() {
        return borderCenterX;
    }
    
    public double getBorderCenterZ() {
        return borderCenterZ;
    }
    
    public int getBorderTransitionSeconds() {
        return borderTransitionSeconds;
    }
    
    public double getPreStartBorderDamageAmount() {
        return preStartBorderDamageAmount;
    }
    
    public double getPreStartBorderDamageBuffer() {
        return preStartBorderDamageBuffer;
    }
    
    public double getBorderDamageAmount() {
        return borderDamageAmount;
    }
    
    public double getBorderDamageBuffer() {
        return borderDamageBuffer;
    }
    
    public String getStartingDifficulty() {
        return startingDifficulty;
    }
    
    public String getStartedDifficulty() {
        return startedDifficulty;
    }
    
    public boolean isStartingDisableMobSpawning() {
        return startingDisableMobSpawning;
    }
    
    public boolean isStartedDisableMobSpawning() {
        return startedDisableMobSpawning;
    }
    
    public boolean isStartingDisableMobDamage() {
        return startingDisableMobDamage;
    }
    
    public boolean isStartedDisableMobDamage() {
        return startedDisableMobDamage;
    }

    public boolean isPreStartBlockProtectionEnabled() {
        return preStartBlockProtectionEnabled;
    }
    
    // Setters
    public void setCountdownDuration(int countdownDuration) {
        this.countdownDuration = countdownDuration;
    }
    
    public void setCooldownDuration(int countdownDuration) {
        this.cooldownDuration = countdownDuration;
    }
    
    public void setPreStartBorderSize(double preStartBorderSize) {
        this.preStartBorderSize = preStartBorderSize;
    }
    
    public void setFinalBorderSize(double finalBorderSize) {
        this.finalBorderSize = finalBorderSize;
    }
    
    public void setJoinRemindersEnabled(boolean joinRemindersEnabled) {
        this.joinRemindersEnabled = joinRemindersEnabled;
    }
    
    public void setReminderInterval(int reminderInterval) {
        this.reminderInterval = reminderInterval;
    }
    
    public void setPvpProtectionDuration(int pvpProtectionDuration) {
        this.pvpProtectionDuration = pvpProtectionDuration;
    }
    
    public void setWorldName(String worldName) {
        this.worldName = worldName != null ? worldName : "";
    }
    
    public void setMinOnlinePlayers(int minOnlinePlayers) {
        this.minOnlinePlayers = minOnlinePlayers;
    }
    
    public void setCountdownBossBarEnabled(boolean countdownBossBarEnabled) {
        this.countdownBossBarEnabled = countdownBossBarEnabled;
    }
    
    public void setBorderCenterMode(String borderCenterMode) {
        this.borderCenterMode = borderCenterMode != null ? borderCenterMode : "spawn";
    }
    
    public void setBorderCenterX(double borderCenterX) {
        this.borderCenterX = borderCenterX;
    }
    
    public void setBorderCenterZ(double borderCenterZ) {
        this.borderCenterZ = borderCenterZ;
    }
    
    public void setBorderTransitionSeconds(int borderTransitionSeconds) {
        this.borderTransitionSeconds = borderTransitionSeconds;
    }
    
    public void setPreStartBorderDamageAmount(double preStartBorderDamageAmount) {
        this.preStartBorderDamageAmount = preStartBorderDamageAmount;
    }
    
    public void setPreStartBorderDamageBuffer(double preStartBorderDamageBuffer) {
        this.preStartBorderDamageBuffer = preStartBorderDamageBuffer;
    }
    
    public void setBorderDamageAmount(double borderDamageAmount) {
        this.borderDamageAmount = borderDamageAmount;
    }
    
    public void setBorderDamageBuffer(double borderDamageBuffer) {
        this.borderDamageBuffer = borderDamageBuffer;
    }
    
    public void setStartingDifficulty(String startingDifficulty) {
        this.startingDifficulty = startingDifficulty != null ? startingDifficulty : "peaceful";
    }
    
    public void setStartedDifficulty(String startedDifficulty) {
        this.startedDifficulty = startedDifficulty != null ? startedDifficulty : "normal";
    }
    
    public void setStartingDisableMobSpawning(boolean startingDisableMobSpawning) {
        this.startingDisableMobSpawning = startingDisableMobSpawning;
    }
    
    public void setStartedDisableMobSpawning(boolean startedDisableMobSpawning) {
        this.startedDisableMobSpawning = startedDisableMobSpawning;
    }
    
    public void setStartingDisableMobDamage(boolean startingDisableMobDamage) {
        this.startingDisableMobDamage = startingDisableMobDamage;
    }
    
    public void setStartedDisableMobDamage(boolean startedDisableMobDamage) {
        this.startedDisableMobDamage = startedDisableMobDamage;
    }

    public void setPreStartBlockProtectionEnabled(boolean preStartBlockProtectionEnabled) {
        this.preStartBlockProtectionEnabled = preStartBlockProtectionEnabled;
    }
}
