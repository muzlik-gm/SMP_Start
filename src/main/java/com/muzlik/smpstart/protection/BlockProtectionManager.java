package com.muzlik.smpstart.protection;

/**
 * Manages block protection before SMP starts
 */
public interface BlockProtectionManager {
    
    /**
     * Enable block protection (prevent breaking/placing)
     */
    void enableBlockProtection();
    
    /**
     * Disable block protection (allow breaking/placing)
     */
    void disableBlockProtection();
    
    /**
     * Check if block protection is currently active
     * @return true if blocks are protected, false otherwise
     */
    boolean isBlockProtectionActive();
}