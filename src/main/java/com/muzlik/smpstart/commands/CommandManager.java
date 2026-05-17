package com.muzlik.smpstart.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

/**
 * Manages all plugin commands under the /smp root command.
 */
public interface CommandManager extends CommandExecutor, TabCompleter {

    /**
     * Register the /smp command and all subcommands with Bukkit.
     */
    void registerCommands();

    /**
     * Display the help menu to the sender.
     *
     * @param sender the command sender
     */
    void showHelp(org.bukkit.command.CommandSender sender);
}
