package com.muzlik.smpstart.commands.subcommands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.SubCommand;
import com.muzlik.smpstart.state.StateManager.PluginState;
import com.muzlik.smpstart.state.StateManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * /smp start — begins the SMP countdown sequence.
 */
public class StartSubCommand implements SubCommand {

    private final SMPStartPlugin plugin;

    public StartSubCommand(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String getName()        { return "start"; }
    @Override public String getPermission()  { return "smpstart.use"; }
    @Override public String getUsage()       { return "start"; }
    @Override public String getDescription() { return "Begin the SMP countdown sequence."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        boolean smpStarted = false;
        if (plugin.getStateManager() instanceof StateManagerImpl) {
            smpStarted = ((StateManagerImpl) plugin.getStateManager()).getStateData().isSmpStarted();
        }
        if (smpStarted) {
            sender.sendMessage(ChatColor.YELLOW + "The SMP has already started. Use " +
                    ChatColor.WHITE + "/smp reset" + ChatColor.YELLOW + " to start over.");
            return true;
        }

        if (!plugin.getStateManager().canExecuteStart()) {
            PluginState state = plugin.getStateManager().getCurrentState();
            if (state == PluginState.COUNTDOWN) {
                sender.sendMessage(ChatColor.YELLOW + "Countdown already active — " +
                        plugin.getStateManager().getRemainingCountdown() + "s remaining.");
            } else if (state == PluginState.COOLDOWN) {
                sender.sendMessage(ChatColor.YELLOW + "On cooldown — " +
                        plugin.getStateManager().getRemainingCooldown() + "s remaining.");
            }
            return true;
        }

        int minPlayers = plugin.getConfigManager().getMinOnlinePlayers();
        int online = getOnlineCount();
        if (minPlayers > 0 && online < minPlayers) {
            sender.sendMessage(ChatColor.RED + "Not enough players online. Need " +
                    minPlayers + ", have " + online + ".");
            return true;
        }

        plugin.getStateManager().startCountdown();
        plugin.getServer().broadcastMessage(
                ChatColor.GREEN + "SMP countdown started by " +
                ChatColor.WHITE + sender.getName() + ChatColor.GREEN + "!");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    private int getOnlineCount() {
        String worldName = plugin.getConfigManager().getWorldName();
        if (worldName != null && !worldName.isBlank()) {
            World world = Bukkit.getWorld(worldName.trim());
            if (world != null) return world.getPlayers().size();
        }
        return Bukkit.getOnlinePlayers().size();
    }
}
