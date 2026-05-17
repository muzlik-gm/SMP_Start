package com.muzlik.smpstart.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Represents a single subcommand for the /smp root command.
 */
public interface SubCommand {

    /**
     * The subcommand name (e.g. "start", "cancel").
     */
    String getName();

    /**
     * The permission node required to run this subcommand.
     */
    String getPermission();

    /**
     * Short usage hint shown in help, e.g. "start".
     */
    String getUsage();

    /**
     * One-line description shown in help.
     */
    String getDescription();

    /**
     * Execute the subcommand.
     *
     * @param sender the command sender
     * @param args   remaining arguments after the subcommand name
     * @return true if handled
     */
    boolean execute(CommandSender sender, String[] args);

    /**
     * Tab-complete suggestions for this subcommand.
     *
     * @param sender the command sender
     * @param args   remaining arguments after the subcommand name
     * @return list of completions
     */
    List<String> tabComplete(CommandSender sender, String[] args);
}
