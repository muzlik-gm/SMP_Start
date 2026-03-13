package com.muzlik.smpstart.data;

/**
 * Data model for plugin configuration
 */
public class PluginConfig {
    
    // Default configuration values
    private int countdownDuration = 10; // seconds
    private int cooldownDuration = 10; // seconds
    private double preStartBorderSize = 10.0; // blocks
    private double finalBorderSize = 10000.0; // blocks
    private boolean joinRemindersEnabled = true;
    private int reminderInterval = 30; // seconds
    private int pvpProtectionDuration = 30; // minutes
    
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
    
    // Setters
    public void setCountdownDuration(int countdownDuration) {
        this.countdownDuration = countdownDuration;
    }
    
    public void setCooldownDuration(int cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
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
}