package com.muzlik.smpstart.commands.subcommands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * /smp reset — resets the SMP back to pre-start state.
 */
public class ResetSubCommand implements SubCommand {

    private final SMPStartPlugin plugin;

    public ResetSubCommand(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String getName()        { return "reset"; }
    @Override public String getPermission()  { return "smpstart.reset"; }
    @Override public String getUsage()       { return "reset"; }
    @Override public String getDescription() { return "Reset the SMP to pre-start state."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        plugin.getStateManager().resetSmp();
        plugin.getServer().broadcastMessage(
                ChatColor.RED + "SMP has been reset by " +
                ChatColor.WHITE + sender.getName() + ChatColor.RED + ".");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
