package com.muzlik.smpstart.border;

import com.muzlik.smpstart.SMPStartPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
                    // Set border center based on configuration
                    border.setCenter(getBorderCenter(world));
                    // Set border size
                    border.setSize(size);
                    // Disable damage during countdown to prevent deaths
                    border.setDamageAmount(plugin.getConfigManager().getPreStartBorderDamageAmount());
                    border.setDamageBuffer(plugin.getConfigManager().getPreStartBorderDamageBuffer());
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
                    // Ensure border is centered based on configuration before transition
                    border.setCenter(getBorderCenter(world));
                    // Re-enable border damage for the final border
                    border.setDamageAmount(plugin.getConfigManager().getBorderDamageAmount());
                    border.setDamageBuffer(plugin.getConfigManager().getBorderDamageBuffer());
                    // Smooth transition over configured seconds
                    int transitionSeconds = plugin.getConfigManager().getBorderTransitionSeconds();
                    border.setSize(finalSize, Math.max(0, transitionSeconds));
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
                    // Ensure border is centered based on configuration
                    border.setCenter(getBorderCenter(world));
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
        // Prefer configured world if provided
        String configuredWorld = plugin.getConfigManager().getWorldName();
        if (configuredWorld != null && !configuredWorld.trim().isEmpty()) {
            World world = Bukkit.getWorld(configuredWorld.trim());
            if (world != null) {
                return world;
            }
            plugin.getLogger().warning("Configured world not found: " + configuredWorld + ". Falling back to default world.");
        }
        
        // Try to get the default world first
        if (!Bukkit.getWorlds().isEmpty()) {
            World world = Bukkit.getWorlds().get(0);
            if (world != null) {
                return world;
            }
        }
        
        // Fallback: try to find a world with environment NORMAL
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() == World.Environment.NORMAL) {
                return w;
            }
        }
        
        return null;
    }
    
    private Location getBorderCenter(World world) {
        String mode = plugin.getConfigManager().getBorderCenterMode();
        if ("fixed".equalsIgnoreCase(mode)) {
            double x = plugin.getConfigManager().getBorderCenterX();
            double z = plugin.getConfigManager().getBorderCenterZ();
            return new Location(world, x, world.getSpawnLocation().getY(), z);
        }
        
        return world.getSpawnLocation();
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
                    
                    // Set border center based on configuration
                    border.setCenter(getBorderCenter(world));
                    plugin.getLogger().info("Set border center to: " + getBorderCenter(world));
                    
                    // Set border size
                    border.setSize(size);
                    plugin.getLogger().info("Set border size to: " + size);
                    
                    // Disable damage during pre-start phase
                    border.setDamageAmount(plugin.getConfigManager().getPreStartBorderDamageAmount());
                    border.setDamageBuffer(plugin.getConfigManager().getPreStartBorderDamageBuffer());
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
                    
                    // Set border center based on configuration
                    border.setCenter(getBorderCenter(world));
                    plugin.getLogger().info("Set border center to: " + getBorderCenter(world));
                    
                    // Set border size to final size
                    border.setSize(size);
                    plugin.getLogger().info("Set border size to final: " + size);
                    
                    // Enable border damage (SMP has started)
                    border.setDamageAmount(plugin.getConfigManager().getBorderDamageAmount());
                    border.setDamageBuffer(plugin.getConfigManager().getBorderDamageBuffer());
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
