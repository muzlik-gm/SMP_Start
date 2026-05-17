package com.muzlik.smpstart.state;

import com.muzlik.smpstart.SMPStartPlugin;

/**
 * Manages the plugin's operational state and coordinates between systems
 */
public interface StateManager {
    
    /**
     * Plugin operational states
     */
    enum PluginState {
        IDLE,       // No active countdown or cooldown
        COUNTDOWN,  // Active countdown sequence in progress
        COOLDOWN    // Post-start cooldown period active
    }
    
    /**
     * Start the countdown sequence
     */
    void startCountdown();
    
    /**
     * Handle a single countdown tick (called every second)
     */
    void handleCountdownTick();
    
    /**
     * Complete the countdown and transition to cooldown
     */
    void completeCountdown();
    
    /**
     * Cancel the countdown if active
     * @return true if countdown was cancelled, false otherwise
     */
    boolean cancelCountdown();
    
    /**
     * Reset the SMP state back to pre-start
     * @return true if reset completed
     */
    boolean resetSmp();
    
    /**
     * Start the cooldown period
     */
    void startCooldown();
    
    /**
     * Check if the start command can be executed
     * @return true if command can be executed, false if in cooldown
     */
    boolean canExecuteStart();
    
    /**
     * Get the current plugin state
     * @return current PluginState
     */
    PluginState getCurrentState();
    
    /**
     * Get remaining countdown time in seconds
     * @return remaining seconds, or 0 if not in countdown
     */
    int getRemainingCountdown();
    
    /**
     * Get remaining cooldown time in seconds
     * @return remaining seconds, or 0 if not in cooldown
     */
    int getRemainingCooldown();
}
