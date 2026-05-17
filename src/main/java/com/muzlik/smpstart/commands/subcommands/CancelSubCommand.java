package com.muzlik.smpstart.commands.subcommands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * /smp cancel — cancels the active countdown.
 */
public class CancelSubCommand implements SubCommand {

    private final SMPStartPlugin plugin;

    public CancelSubCommand(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String getName()        { return "cancel"; }
    @Override public String getPermission()  { return "smpstart.cancel"; }
    @Override public String getUsage()       { return "cancel"; }
    @Override public String getDescription() { return "Cancel the active countdown."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!plugin.getStateManager().cancelCountdown()) {
            sender.sendMessage(ChatColor.YELLOW + "No active countdown to cancel.");
            return true;
        }
        plugin.getServer().broadcastMessage(
                ChatColor.RED + "SMP countdown cancelled by " +
                ChatColor.WHITE + sender.getName() + ChatColor.RED + ".");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
