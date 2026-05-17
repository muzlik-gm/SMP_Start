package com.muzlik.smpstart.effects;

/**
 * Manages all audio-visual effects and animations
 */
public interface EffectSystem {
    
    /**
     * Play countdown tick sound and show visual effects
     * @param remaining remaining seconds in countdown
     */
    void playCountdownTick(int remaining);
    
    /**
     * Play the start sound when countdown completes
     */
    void playStartSound();
    
    /**
     * Play totem pop sound when cooldown expires
     */
    void playTotemPopSound();
    
    /**
     * Show countdown title with remaining time
     * @param remaining remaining seconds in countdown
     */
    void showCountdownTitle(int remaining);
    
    /**
     * Show start animation when countdown completes
     */
    void showStartAnimation();
    
    /**
     * Broadcast countdown message to all players
     * @param remaining remaining seconds in countdown
     */
    void broadcastCountdownMessage(int remaining);
    
    /**
     * Broadcast start message to all players
     */
    void broadcastStartMessage();
    
    /**
     * Update the countdown boss bar
     * @param remaining remaining seconds
     * @param totalSeconds total countdown duration in seconds
     */
    void updateCountdownBossBar(int remaining, int totalSeconds);
    
    /**
     * Clear the countdown boss bar
     */
    void clearCountdownBossBar();
}
