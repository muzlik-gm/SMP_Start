package com.muzlik.smpstart;

import com.muzlik.smpstart.config.ConfigManager;
import com.muzlik.smpstart.config.ConfigManagerImpl;
import com.muzlik.smpstart.data.StateData;
import com.muzlik.smpstart.effects.EffectSystem;
import com.muzlik.smpstart.effects.EffectSystemImpl;
import com.muzlik.smpstart.reminders.ReminderSystem;
import com.muzlik.smpstart.reminders.ReminderSystemImpl;
import com.muzlik.smpstart.state.StateManager;
import com.muzlik.smpstart.state.StateManagerImpl;
import com.muzlik.smpstart.state.StateManager.PluginState;
import com.muzlik.smpstart.border.BorderManager;
import com.muzlik.smpstart.border.BorderManagerImpl;
import com.muzlik.smpstart.commands.CommandManager;
import com.muzlik.smpstart.commands.CommandManagerImpl;
import com.muzlik.smpstart.pvp.PvPManager;
import com.muzlik.smpstart.pvp.PvPManagerImpl;
import com.muzlik.smpstart.persistence.DataManager;
import com.muzlik.smpstart.persistence.DataManagerImpl;
import com.muzlik.smpstart.menu.ConfigMenuManager;
import com.muzlik.smpstart.protection.BlockProtectionManager;
import com.muzlik.smpstart.protection.BlockProtectionManagerImpl;
import com.muzlik.smpstart.utils.ErrorHandler;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for Muzlik's SMP Starter.
 * Provides a coordinated launch system for Minecraft SMP servers.
 */
public class SMPStartPlugin extends JavaPlugin {
    
    private StateManager stateManager;
    private ConfigManager configManager;
    private EffectSystem effectSystem;
    private ReminderSystem reminderSystem;
    private BorderManager borderManager;
    private CommandManager commandManager;
    private PvPManager pvpManager;
    private DataManager dataManager;
    private BlockProtectionManager blockProtectionManager;
    private ConfigMenuManager configMenuManager;
    private ErrorHandler errorHandler;
    
    @Override
    public void onEnable() {
        getLogger().info("Muzlik's SMP Starter is enabling...");
        
        try {
            // Initialize error handler first
            errorHandler = new ErrorHandler(this);
            errorHandler.logInfo("Startup", "Initializing Muzlik's SMP Starter...");
            
            // Initialize managers in dependency order
            errorHandler.logInfo("Startup", "Initializing configuration manager...");
            configManager = new ConfigManagerImpl(this);
            
            errorHandler.logInfo("Startup", "Initializing state manager...");
            stateManager = new StateManagerImpl(this);
            
            errorHandler.logInfo("Startup", "Initializing border manager...");
            borderManager = new BorderManagerImpl(this);
            
            errorHandler.logInfo("Startup", "Initializing effect system...");
            effectSystem = new EffectSystemImpl(this);
            
            errorHandler.logInfo("Startup", "Initializing reminder system...");
            reminderSystem = new ReminderSystemImpl(this);
            
            errorHandler.logInfo("Startup", "Initializing command manager...");
            commandManager = new CommandManagerImpl(this);
            
            errorHandler.logInfo("Startup", "Initializing PvP manager...");
            pvpManager = new PvPManagerImpl(this);
            
            errorHandler.logInfo("Startup", "Initializing data manager...");
            dataManager = new DataManagerImpl(this);
            
            errorHandler.logInfo("Startup", "Initializing block protection manager...");
            blockProtectionManager = new BlockProtectionManagerImpl(this);
            
            errorHandler.logInfo("Startup", "Initializing config menu manager...");
            configMenuManager = new ConfigMenuManager(this);
            
            // Register commands
            errorHandler.logInfo("Startup", "Registering commands...");
            commandManager.registerCommands();
            
            // Validate all systems are initialized
            errorHandler.requireNonNull(configManager, "ConfigManager");
            errorHandler.requireNonNull(stateManager, "StateManager");
            errorHandler.requireNonNull(borderManager, "BorderManager");
            errorHandler.requireNonNull(effectSystem, "EffectSystem");
            errorHandler.requireNonNull(reminderSystem, "ReminderSystem");
            errorHandler.requireNonNull(commandManager, "CommandManager");
            errorHandler.requireNonNull(pvpManager, "PvPManager");
            errorHandler.requireNonNull(dataManager, "DataManager");
            errorHandler.requireNonNull(blockProtectionManager, "BlockProtectionManager");
            errorHandler.requireNonNull(configMenuManager, "ConfigMenuManager");
            
            // Load saved state if exists
            errorHandler.logInfo("Startup", "Loading saved state...");
            loadSavedState();
            
            // Resume countdown/cooldown timers if needed
            if (stateManager instanceof StateManagerImpl) {
                ((StateManagerImpl) stateManager).resumeStateAfterLoad();
            }
            
            // Initialize world border to pre-start size on plugin startup
            errorHandler.logInfo("Startup", "Setting initial world border...");
            initializeWorldBorder();
            
            // Start join reminders if appropriate (SMP not started and idle)
            startJoinRemindersIfNeeded();
            
            errorHandler.logInfo("Startup", "Muzlik's SMP Starter enabled successfully!");
            errorHandler.logInfo("Startup", "Plugin ready. Use /smp start to begin the countdown.");
            
        } catch (Exception e) {
            if (errorHandler != null) {
                errorHandler.handleException("Plugin Startup", e);
            } else {
                getLogger().severe("Failed to enable Muzlik's SMP Starter: " + e.getMessage());
                e.printStackTrace();
            }
            // Disable the plugin if initialization fails
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Muzlik's SMP Starter is disabling...");
        
        try {
            // Cleanup resources in reverse order
            if (errorHandler != null) {
                errorHandler.logInfo("Shutdown", "Cleaning up managers...");
            }
            
            if (stateManager instanceof StateManagerImpl) {
                ((StateManagerImpl) stateManager).cleanup();
                if (errorHandler != null) {
                    errorHandler.logInfo("Shutdown", "State manager cleaned up");
                }
            }
            
            if (reminderSystem instanceof ReminderSystemImpl) {
                ((ReminderSystemImpl) reminderSystem).cleanup();
                if (errorHandler != null) {
                    errorHandler.logInfo("Shutdown", "Reminder system cleaned up");
                }
            }
            
            if (pvpManager instanceof PvPManagerImpl) {
                ((PvPManagerImpl) pvpManager).cleanup();
                if (errorHandler != null) {
                    errorHandler.logInfo("Shutdown", "PvP manager cleaned up");
                }
            }
            
            // Save current state before shutdown
            if (dataManager != null && stateManager != null) {
                dataManager.saveState(((StateManagerImpl) stateManager).getStateData());
                if (errorHandler != null) {
                    errorHandler.logInfo("Shutdown", "Plugin state saved");
                }
            }
            
            // Save configuration before shutdown
            if (configManager != null) {
                configManager.saveConfig();
                if (errorHandler != null) {
                    errorHandler.logInfo("Shutdown", "Configuration saved");
                }
            }
            
            if (errorHandler != null) {
                errorHandler.logInfo("Shutdown", "Muzlik's SMP Starter disabled successfully!");
            } else {
                getLogger().info("Muzlik's SMP Starter disabled successfully!");
            }
            
        } catch (Exception e) {
            if (errorHandler != null) {
                errorHandler.handleException("Plugin Shutdown", e);
            } else {
                getLogger().warning("Error during plugin shutdown: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    // Getters for managers (will be used by other components)
    public StateManager getStateManager() {
        return stateManager;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public EffectSystem getEffectSystem() {
        return effectSystem;
    }
    
    public ReminderSystem getReminderSystem() {
        return reminderSystem;
    }
    
    public BorderManager getBorderManager() {
        return borderManager;
    }
    
    public CommandManager getCommandManager() {
        return commandManager;
    }
    
    public PvPManager getPvPManager() {
        return pvpManager;
    }
    
    public DataManager getDataManager() {
        return dataManager;
    }
    
    public BlockProtectionManager getBlockProtectionManager() {
        return blockProtectionManager;
    }
    
    public ConfigMenuManager getConfigMenuManager() {
        return configMenuManager;
    }
    
    /**
     * Check if all systems are properly initialized
     * @return true if all systems are ready, false otherwise
     */
    public boolean areSystemsReady() {
        return configManager != null && stateManager != null && borderManager != null && 
               effectSystem != null && reminderSystem != null && commandManager != null &&
               pvpManager != null && dataManager != null && blockProtectionManager != null;
    }
    
    /**
     * Get plugin version information
     * @return version string
     */
    public String getPluginVersion() {
        return getDescription().getVersion();
    }
    
    /**
     * Get plugin author information
     * @return author string
     */
    public String getPluginAuthor() {
        return "muzlik";
    }
    
    /**
     * Get the error handler for centralized error management
     * @return the error handler instance
     */
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }
    
    /**
     * Initialize the world border based on current SMP state
     * Sets pre-start size if SMP hasn't started, or final size if it has
     */
    private void initializeWorldBorder() {
        // Delay border initialization to ensure worlds are loaded and state is loaded
        getServer().getScheduler().runTaskLater(this, () -> {
            try {
                // Check if SMP has already started
                if (stateManager instanceof StateManagerImpl) {
                    StateData stateData = ((StateManagerImpl) stateManager).getStateData();
                    
                    if (stateData.isSmpStarted()) {
                        // SMP has started - set final border size
                        borderManager.setFinalBorder();
                        double finalSize = configManager.getFinalBorderSize();
                        errorHandler.logInfo("Startup", "SMP already started - world border set to final size: " + finalSize + " blocks");
                    } else {
                        // SMP hasn't started - set pre-start border size
                        borderManager.initializeBorder();
                        double preStartSize = configManager.getPreStartBorderSize();
                        errorHandler.logInfo("Startup", "SMP not started - world border set to pre-start size: " + preStartSize + " blocks");
                    }
                } else {
                    // Fallback - use pre-start border
                    borderManager.initializeBorder();
                    double borderSize = configManager.getPreStartBorderSize();
                    errorHandler.logInfo("Startup", "World border initialized to pre-start size: " + borderSize + " blocks");
                }
            } catch (Exception e) {
                errorHandler.handleException("Border Initialization", e);
            }
        }, 40L); // Wait 2 seconds (40 ticks) to ensure state is loaded first
    }
    
    /**
     * Load saved state from file if it exists
     */
    private void loadSavedState() {
        try {
            if (dataManager.hasSavedState()) {
                errorHandler.logInfo("Startup", "Found saved state file, attempting to restore...");
                StateData savedState = dataManager.loadState();
                if (savedState != null && stateManager instanceof StateManagerImpl) {
                    ((StateManagerImpl) stateManager).loadStateData(savedState);
                    errorHandler.logInfo("Startup", "State restored successfully");
                }
            } else {
                errorHandler.logInfo("Startup", "No saved state found, starting fresh");
            }
        } catch (Exception e) {
            errorHandler.handleException("State Loading", e);
        }
    }
    
    /**
     * Start join reminders only when SMP hasn't started and the plugin is idle
     */
    private void startJoinRemindersIfNeeded() {
        if (reminderSystem == null || configManager == null || stateManager == null) {
            return;
        }
        
        if (!configManager.areJoinRemindersEnabled()) {
            return;
        }
        
        if (!(stateManager instanceof StateManagerImpl)) {
            return;
        }
        
        StateData stateData = ((StateManagerImpl) stateManager).getStateData();
        if (stateData.isSmpStarted()) {
            return;
        }
        
        if (stateManager.getCurrentState() != PluginState.IDLE) {
            return;
        }
        
        reminderSystem.startReminders();
    }
}
