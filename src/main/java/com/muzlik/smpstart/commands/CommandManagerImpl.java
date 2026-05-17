package com.muzlik.smpstart.commands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.subcommands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Root handler for the /smp command.
 * Dispatches to registered {@link SubCommand} instances.
 */
public class CommandManagerImpl implements CommandManager {

    private final SMPStartPlugin plugin;
    private final Map<String, SubCommand> subCommands = new LinkedHashMap<>();

    public CommandManagerImpl(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerCommands() {
        // Register all subcommands
        register(new StartSubCommand(plugin));
        register(new CancelSubCommand(plugin));
        register(new ResetSubCommand(plugin));
        register(new ReloadSubCommand(plugin));
        register(new StatusSubCommand(plugin));
        register(new ConfigSubCommand(plugin));
        register(new MenuSubCommand(plugin));

        // Bind the single /smp root command
        PluginCommand smp = plugin.getCommand("smp");
        if (smp != null) {
            smp.setExecutor(this);
            smp.setTabCompleter(this);
        } else {
            plugin.getLogger().severe("Command 'smp' not found in plugin.yml!");
        }

        plugin.getLogger().info("Registered /smp with " + subCommands.size() + " subcommands.");
    }

    private void register(SubCommand sub) {
        subCommands.put(sub.getName().toLowerCase(), sub);
    }

    // -------------------------------------------------------------------------
    // CommandExecutor
    // -------------------------------------------------------------------------

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof org.bukkit.entity.Player player) {
                plugin.getConfigMenuManager().openMainMenu(player);
            } else {
                showHelp(sender);
            }
            return true;
        }

        String subName = args[0].toLowerCase();

        if (subName.equals("help")) {
            showHelp(sender);
            return true;
        }

        SubCommand sub = subCommands.get(subName);
        if (sub == null) {
            sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + subName +
                    ". Run /smp help for a list.");
            return true;
        }

        if (!sender.hasPermission(sub.getPermission())) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
            return true;
        }

        // Pass remaining args (everything after the subcommand name)
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return sub.execute(sender, subArgs);
    }

    // -------------------------------------------------------------------------
    // TabCompleter
    // -------------------------------------------------------------------------

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> names = new ArrayList<>();
            names.add("help");
            subCommands.forEach((name, sub) -> {
                if (sender.hasPermission(sub.getPermission())) names.add(name);
            });
            return names.stream()
                    .filter(n -> n.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length >= 2) {
            SubCommand sub = subCommands.get(args[0].toLowerCase());
            if (sub != null && sender.hasPermission(sub.getPermission())) {
                return sub.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Help
    // -------------------------------------------------------------------------

    @Override
    public void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- Muzlik's SMP Starter ---");
        sender.sendMessage(ChatColor.GRAY + "Usage: " + ChatColor.WHITE + "/smp <subcommand>");
        sender.sendMessage("");
        subCommands.forEach((name, sub) -> {
            if (sender.hasPermission(sub.getPermission())) {
                sender.sendMessage(ChatColor.YELLOW + "/smp " + sub.getUsage() +
                        ChatColor.GRAY + " — " + ChatColor.WHITE + sub.getDescription());
            }
        });
        sender.sendMessage(ChatColor.YELLOW + "/smp help" +
                ChatColor.GRAY + " — " + ChatColor.WHITE + "Show this help menu.");
    }
}
