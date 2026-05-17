package com.muzlik.smpstart.commands.subcommands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.SubCommand;
import com.muzlik.smpstart.state.StateManager.PluginState;
import com.muzlik.smpstart.state.StateManagerImpl;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * /smp reload — reloads the plugin configuration.
 */
public class ReloadSubCommand implements SubCommand {

    private final SMPStartPlugin plugin;

    public ReloadSubCommand(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String getName()        { return "reload"; }
    @Override public String getPermission()  { return "smpstart.reload"; }
    @Override public String getUsage()       { return "reload"; }
    @Override public String getDescription() { return "Reload the plugin configuration."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        plugin.getConfigManager().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded.");

        if (plugin.getReminderSystem() != null && plugin.getStateManager() != null) {
            boolean smpStarted = false;
            if (plugin.getStateManager() instanceof StateManagerImpl) {
                smpStarted = ((StateManagerImpl) plugin.getStateManager()).getStateData().isSmpStarted();
            }
            plugin.getReminderSystem().stopReminders();
            if (!smpStarted && plugin.getStateManager().getCurrentState() == PluginState.IDLE
                    && plugin.getConfigManager().areJoinRemindersEnabled()) {
                plugin.getReminderSystem().startReminders();
            }
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
