package com.muzlik.smpstart.border;

/**
 * Handles world border operations
 */
public interface BorderManager {
    
    /**
     * Set the world border to pre-start size
     */
    void setPreStartBorder();
    
    /**
     * Transition the world border to final size smoothly
     */
    void transitionToFinalBorder();
    
    /**
     * Validate a border size value
     * @param size the size to validate
     * @return true if valid, false otherwise
     */
    boolean validateBorderSize(double size);
    
    /**
     * Get the current world border size
     * @return current border size in blocks
     */
    double getCurrentBorderSize();
    
    /**
     * Set the world border to a specific size immediately
     * @param size the border size in blocks
     */
    void setBorderSize(double size);
    
    /**
     * Initialize the world border on plugin startup
     * Sets the border to pre-start size if in IDLE state
     */
    void initializeBorder();
    
    /**
     * Set the world border to final size (for when SMP has already started)
     */
    void setFinalBorder();
}