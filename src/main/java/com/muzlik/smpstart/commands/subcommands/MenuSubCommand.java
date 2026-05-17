package com.muzlik.smpstart.commands.subcommands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * /smp menu — opens the interactive SMP starter control menu.
 */
public class MenuSubCommand implements SubCommand {

    private final SMPStartPlugin plugin;

    public MenuSubCommand(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String getName()        { return "menu"; }
    @Override public String getPermission()  { return "smpstart.use"; }
    @Override public String getUsage()       { return "menu"; }
    @Override public String getDescription() { return "Open the SMP starter control menu."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "The menu can only be opened by a player in-game.");
            return true;
        }
        plugin.getConfigMenuManager().openMainMenu(player);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
