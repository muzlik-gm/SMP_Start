package com.muzlik.smpstart.border;

import com.muzlik.smpstart.SMPStartPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

/**
 * Implementation of BorderManager that handles world border operations
 */
public class BorderManagerImpl implements BorderManager {
    
    private final SMPStartPlugin plugin;
    
    public BorderManagerImpl(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void setPreStartBorder() {
        try {
            double size = plugin.getConfigManager().getPreStartBorderSize();
            if (validateBorderSize(size)) {
                World world = getMainWorld();
                if (world != null) {
                    WorldBorder border = world.getWorldBorder();
                    // Set border center to spawn location to ensure players are inside
                    border.setCenter(world.getSpawnLocation());
                    // Set border size
                    border.setSize(size);
                    // Disable damage during countdown to prevent deaths
                    border.setDamageAmount(0.0);
                    border.setDamageBuffer(0.0);
                    plugin.getLogger().info("Set world border to pre-start size: " + size + " blocks (damage disabled)");
                } else {
                    plugin.getLogger().warning("Could not find main world for pre-start border");
                }
            } else {
                plugin.getLogger().warning("Invalid pre-start border size: " + size);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to set pre-start border: " + e.getMessage());
        }
    }
    
    @Override
    public void transitionToFinalBorder() {
        try {
            double finalSize = plugin.getConfigManager().getFinalBorderSize();
            if (validateBorderSize(finalSize)) {
                World world = getMainWorld();
                if (world != null) {
                    WorldBorder border = world.getWorldBorder();
                    // Re-enable border damage for the final border
                    border.setDamageAmount(0.2); // Default Minecraft damage
                    border.setDamageBuffer(5.0); // Default buffer
                    // Smooth transition over 10 seconds
                    border.setSize(finalSize, 10);
                    plugin.getLogger().info("Transitioning world border to final size: " + finalSize + " blocks (damage enabled)");
                } else {
                    plugin.getLogger().warning("Could not find main world for border transition");
                }
            } else {
                plugin.getLogger().warning("Invalid final border size: " + finalSize);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to transition to final border: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validateBorderSize(double size) {
        return size > 0 && size <= 60000000; // Minecraft's maximum world border size
    }
    
    @Override
    public double getCurrentBorderSize() {
        try {
            World world = getMainWorld();
            if (world != null) {
                return world.getWorldBorder().getSize();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get current border size: " + e.getMessage());
        }
        return 0;
    }
    
    @Override
    public void setBorderSize(double size) {
        try {
            if (validateBorderSize(size)) {
                World world = getMainWorld();
                if (world != null) {
                    WorldBorder border = world.getWorldBorder();
                    // Ensure border is centered on spawn
                    border.setCenter(world.getSpawnLocation());
                    border.setSize(size);
                    plugin.getLogger().info("Set world border size to: " + size + " blocks");
                } else {
                    plugin.getLogger().warning("Could not find main world for border setting");
                }
            } else {
                plugin.getLogger().warning("Invalid border size: " + size);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to set border size: " + e.getMessage());
        }
    }
    
    /**
     * Get the main world (overworld)
     * @return the main world, or null if not found
     */
    private World getMainWorld() {
        // Try to get the default world first
        World world = Bukkit.getWorlds().get(0);
        if (world != null) {
            return world;
        }
        
        // Fallback: try to find a world with environment NORMAL
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() == World.Environment.NORMAL) {
                return w;
            }
        }
        
        return null;
    }
    
    @Override
    public void initializeBorder() {
        try {
            plugin.getLogger().info("Starting border initialization...");
            
            // Always set the pre-start border on initialization
            // This ensures the border is correct when the plugin loads
            double size = plugin.getConfigManager().getPreStartBorderSize();
            plugin.getLogger().info("Pre-start border size from config: " + size);
            
            if (validateBorderSize(size)) {
                World world = getMainWorld();
                plugin.getLogger().info("Found world for border initialization: " + (world != null ? world.getName() : "null"));
                
                if (world != null) {
                    WorldBorder border = world.getWorldBorder();
                    plugin.getLogger().info("Current border size before initialization: " + border.getSize());
                    
                    // Set border center to spawn location
                    border.setCenter(world.getSpawnLocation());
                    plugin.getLogger().info("Set border center to spawn location: " + world.getSpawnLocation());
                    
                    // Set border size
                    border.setSize(size);
                    plugin.getLogger().info("Set border size to: " + size);
                    
                    // Disable damage during pre-start phase
                    border.setDamageAmount(0.0);
                    border.setDamageBuffer(0.0);
                    plugin.getLogger().info("Disabled border damage");
                    
                    plugin.getLogger().info("Successfully initialized world border to pre-start size: " + size + " blocks (damage disabled)");
                } else {
                    plugin.getLogger().warning("Could not find main world for border initialization. Available worlds: " + Bukkit.getWorlds().size());
                    for (World w : Bukkit.getWorlds()) {
                        plugin.getLogger().info("Available world: " + w.getName() + " (Environment: " + w.getEnvironment() + ")");
                    }
                }
            } else {
                plugin.getLogger().warning("Invalid pre-start border size for initialization: " + size);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize border: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void setFinalBorder() {
        try {
            double size = plugin.getConfigManager().getFinalBorderSize();
            plugin.getLogger().info("Setting final border size: " + size);
            
            if (validateBorderSize(size)) {
                World world = getMainWorld();
                plugin.getLogger().info("Found world for final border: " + (world != null ? world.getName() : "null"));
                
                if (world != null) {
                    WorldBorder border = world.getWorldBorder();
                    plugin.getLogger().info("Current border size before setting final: " + border.getSize());
                    
                    // Set border center to spawn location
                    border.setCenter(world.getSpawnLocation());
                    plugin.getLogger().info("Set border center to spawn location: " + world.getSpawnLocation());
                    
                    // Set border size to final size
                    border.setSize(size);
                    plugin.getLogger().info("Set border size to final: " + size);
                    
                    // Enable border damage (SMP has started)
                    border.setDamageAmount(0.2); // Default Minecraft damage
                    border.setDamageBuffer(5.0); // Default buffer
                    plugin.getLogger().info("Enabled border damage for final border");
                    
                    plugin.getLogger().info("Successfully set world border to final size: " + size + " blocks (damage enabled)");
                } else {
                    plugin.getLogger().warning("Could not find main world for final border setup");
                }
            } else {
                plugin.getLogger().warning("Invalid final border size: " + size);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to set final border: " + e.getMessage());
            e.printStackTrace();
        }
    }
}