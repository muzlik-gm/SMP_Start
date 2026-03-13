package com.muzlik.smpstart.persistence;

import com.muzlik.smpstart.data.StateData;

/**
 * Manages data persistence for plugin state
 */
public interface DataManager {
    
    /**
     * Save the current plugin state to file
     * @param stateData the state data to save
     */
    void saveState(StateData stateData);
    
    /**
     * Load plugin state from file
     * @return loaded state data, or null if no saved state exists
     */
    StateData loadState();
    
    /**
     * Delete the saved state file
     */
    void clearSavedState();
    
    /**
     * Check if a saved state file exists
     * @return true if saved state exists, false otherwise
     */
    boolean hasSavedState();
}