package com.muzlik.smpstart.pvp;

/**
 * Manages PvP protection during SMP start
 */
public interface PvPManager {
    
    /**
     * Start PvP protection for the configured duration
     */
    void startPvPProtection();
    
    /**
     * Stop PvP protection and enable PvP
     */
    void stopPvPProtection();
    
    /**
     * Check if PvP protection is currently active
     * @return true if PvP is disabled, false if enabled
     */
    boolean isPvPProtectionActive();
    
    /**
     * Get remaining PvP protection time in minutes
     * @return remaining minutes, or 0 if not active
     */
    int getRemainingProtectionTime();
}