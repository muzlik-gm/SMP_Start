package com.muzlik.smpstart.persistence;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.data.StateData;
import com.muzlik.smpstart.state.StateManager.PluginState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Implementation of DataManager that handles state persistence
 */
public class DataManagerImpl implements DataManager {
    
    private final SMPStartPlugin plugin;
    private final File stateFile;
    
    public DataManagerImpl(SMPStartPlugin plugin) {
        this.plugin = plugin;
        this.stateFile = new File(plugin.getDataFolder(), "state.yml");
    }
    
    @Override
    public void saveState(StateData stateData) {
        try {
            // Ensure data folder exists
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }
            
            FileConfiguration config = new YamlConfiguration();
            
            // Save state data
            config.set("current-state", stateData.getCurrentState().name());
            config.set("countdown-start-time", stateData.getCountdownStartTime());
            config.set("cooldown-start-time", stateData.getCooldownStartTime());
            config.set("remaining-countdown", stateData.getRemainingCountdown());
            config.set("remaining-cooldown", stateData.getRemainingCooldown());
            config.set("smp-start-time", stateData.getSmpStartTime());
            config.set("pvp-protection-active", stateData.isPvpProtectionActive());
            config.set("smp-started", stateData.isSmpStarted());
            config.set("save-timestamp", System.currentTimeMillis());
            
            config.save(stateFile);
            plugin.getLogger().info("Plugin state saved to file");
            
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save plugin state: " + e.getMessage());
        }
    }
    
    @Override
    public StateData loadState() {
        if (!stateFile.exists()) {
            return null;
        }
        
        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(stateFile);
            StateData stateData = new StateData();
            
            // Load state data
            String stateName = config.getString("current-state", "IDLE");
            try {
                PluginState state = PluginState.valueOf(stateName);
                stateData.setCurrentState(state);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid state in saved data: " + stateName + ", defaulting to IDLE");
                stateData.setCurrentState(PluginState.IDLE);
            }
            
            stateData.setCountdownStartTime(config.getLong("countdown-start-time", 0));
            stateData.setCooldownStartTime(config.getLong("cooldown-start-time", 0));
            stateData.setRemainingCountdown(config.getInt("remaining-countdown", 0));
            stateData.setRemainingCooldown(config.getInt("remaining-cooldown", 0));
            stateData.setSmpStartTime(config.getLong("smp-start-time", 0));
            stateData.setPvpProtectionActive(config.getBoolean("pvp-protection-active", false));
            stateData.setSmpStarted(config.getBoolean("smp-started", false));
            
            long saveTimestamp = config.getLong("save-timestamp", 0);
            plugin.getLogger().info("Plugin state loaded from file (saved at: " + new java.util.Date(saveTimestamp) + ")");
            
            return stateData;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load plugin state: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public void clearSavedState() {
        if (stateFile.exists()) {
            if (stateFile.delete()) {
                plugin.getLogger().info("Saved state file deleted");
            } else {
                plugin.getLogger().warning("Failed to delete saved state file");
            }
        }
    }
    
    @Override
    public boolean hasSavedState() {
        return stateFile.exists();
    }
}