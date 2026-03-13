package com.muzlik.smpstart.data;

import com.muzlik.smpstart.state.StateManager.PluginState;

/**
 * Data model for plugin state information
 */
public class StateData {
    
    private PluginState currentState = PluginState.IDLE;
    private long countdownStartTime;
    private long cooldownStartTime;
    private int remainingCountdown;
    private int remainingCooldown;
    private long smpStartTime; // When SMP officially started
    private boolean pvpProtectionActive = false;
    private boolean smpStarted = false; // Whether SMP has officially started
    
    // Getters
    public PluginState getCurrentState() {
        return currentState;
    }
    
    public long getCountdownStartTime() {
        return countdownStartTime;
    }
    
    public long getCooldownStartTime() {
        return cooldownStartTime;
    }
    
    public int getRemainingCountdown() {
        return remainingCountdown;
    }
    
    public int getRemainingCooldown() {
        return remainingCooldown;
    }
    
    public long getSmpStartTime() {
        return smpStartTime;
    }
    
    public boolean isPvpProtectionActive() {
        return pvpProtectionActive;
    }
    
    public boolean isSmpStarted() {
        return smpStarted;
    }
    
    // Setters
    public void setCurrentState(PluginState currentState) {
        this.currentState = currentState;
    }
    
    public void setCountdownStartTime(long countdownStartTime) {
        this.countdownStartTime = countdownStartTime;
    }
    
    public void setCooldownStartTime(long cooldownStartTime) {
        this.cooldownStartTime = cooldownStartTime;
    }
    
    public void setRemainingCountdown(int remainingCountdown) {
        this.remainingCountdown = remainingCountdown;
    }
    
    public void setRemainingCooldown(int remainingCooldown) {
        this.remainingCooldown = remainingCooldown;
    }
    
    public void setSmpStartTime(long smpStartTime) {
        this.smpStartTime = smpStartTime;
    }
    
    public void setPvpProtectionActive(boolean pvpProtectionActive) {
        this.pvpProtectionActive = pvpProtectionActive;
    }
    
    public void setSmpStarted(boolean smpStarted) {
        this.smpStarted = smpStarted;
    }
}