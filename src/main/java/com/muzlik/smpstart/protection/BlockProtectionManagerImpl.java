package com.muzlik.smpstart.protection;

import com.muzlik.smpstart.SMPStartPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

/**
 * Implementation of BlockProtectionManager that prevents block interactions before SMP starts
 */
public class BlockProtectionManagerImpl implements BlockProtectionManager, Listener {
    
    private final SMPStartPlugin plugin;
    private boolean blockProtectionActive = true; // Start with protection enabled
    
    public BlockProtectionManagerImpl(SMPStartPlugin plugin) {
        this.plugin = plugin;
        // Register event listener
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @Override
    public void enableBlockProtection() {
        blockProtectionActive = true;
        plugin.getLogger().info("Block protection enabled - players cannot break/place blocks");
        
        // Notify all online players
        String message = ChatColor.YELLOW + "[SMP] " + ChatColor.RED + 
                        "Block breaking/placing is disabled until the SMP starts!";
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
    
    @Override
    public void disableBlockProtection() {
        blockProtectionActive = false;
        plugin.getLogger().info("Block protection disabled - players can now break/place blocks");
        
        // Notify all online players
        String message = ChatColor.YELLOW + "[SMP] " + ChatColor.GREEN + 
                        "Block breaking/placing is now enabled! The SMP has begun!";
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }
    
    @Override
    public boolean isBlockProtectionActive() {
        return blockProtectionActive;
    }
    
    /**
     * Prevent block breaking when protection is active
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!blockProtectionActive) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Allow OPs to break blocks for setup purposes
        if (player.isOp()) {
            return;
        }
        
        // Cancel the event and notify player
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "Block breaking is disabled until the SMP starts! Use /smpstart to begin.");
    }
    
    /**
     * Prevent block placing when protection is active
     */
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!blockProtectionActive) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Allow OPs to place blocks for setup purposes
        if (player.isOp()) {
            return;
        }
        
        // Cancel the event and notify player
        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "Block placing is disabled until the SMP starts! Use /smpstart to begin.");
    }
    
    /**
     * Prevent certain interactions when protection is active
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!blockProtectionActive) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Allow OPs to interact for setup purposes
        if (player.isOp()) {
            return;
        }
        
        // Check if this is a block interaction that should be restricted
        if (event.getClickedBlock() != null) {
            switch (event.getClickedBlock().getType()) {
                case CHEST:
                case BARREL:
                case SHULKER_BOX:
                case ENDER_CHEST:
                case FURNACE:
                case BLAST_FURNACE:
                case SMOKER:
                case CRAFTING_TABLE:
                case ENCHANTING_TABLE:
                case ANVIL:
                case CHIPPED_ANVIL:
                case DAMAGED_ANVIL:
                case BREWING_STAND:
                case CAULDRON:
                case WATER_CAULDRON:
                case LAVA_CAULDRON:
                case POWDER_SNOW_CAULDRON:
                    // Cancel interaction with storage/utility blocks
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Block interactions are disabled until the SMP starts!");
                    break;
                default:
                    // Allow other interactions (like doors, buttons, etc.)
                    break;
            }
        }
    }
    
    /**
     * Prevent opening certain inventories when protection is active
     */
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!blockProtectionActive) {
            return;
        }
        
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getPlayer();
        
        // Allow OPs to open inventories for setup purposes
        if (player.isOp()) {
            return;
        }
        
        // Prevent opening certain inventory types
        InventoryType type = event.getInventory().getType();
        switch (type) {
            case CHEST:
            case BARREL:
            case SHULKER_BOX:
            case ENDER_CHEST:
            case FURNACE:
            case BLAST_FURNACE:
            case SMOKER:
            case ENCHANTING:
            case ANVIL:
            case BREWING:
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Container access is disabled until the SMP starts!");
                break;
            default:
                // Allow other inventory types (player inventory, crafting, etc.)
                break;
        }
    }
}