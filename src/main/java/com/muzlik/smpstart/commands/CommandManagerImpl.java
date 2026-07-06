package com.muzlik.smpstart.commands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.subcommands.*;
import com.muzlik.smpstart.utils.MessageUtils;
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
    private final Map<String, String> aliases = new HashMap<>();

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

        plugin.getLogger().info("Registered /smp with " + subCommands.size() + " subcommands and " + aliases.size() + " aliases.");
    }

    private void register(SubCommand sub) {
        String name = sub.getName().toLowerCase();
        subCommands.put(name, sub);
        for (String alias : sub.getAliases()) {
            aliases.put(alias.toLowerCase(), name);
        }
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

        String input = args[0].toLowerCase();
        String subName = aliases.getOrDefault(input, input);

        if (subName.equals("help")) {
            showHelp(sender);
            return true;
        }

        SubCommand sub = subCommands.get(subName);
        if (sub == null) {
            MessageUtils.sendError(sender, "Unknown subcommand: &f" + input + "&7. Run &f/smp help &7for a list.");
            return true;
        }

        if (!sender.hasPermission(sub.getPermission())) {
            MessageUtils.sendError(sender, "You don't have permission to do that.");
            return true;
        }

        // Pass remaining args (everything after the subcommand name)
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        try {
            return sub.execute(sender, subArgs);
        } catch (Exception e) {
            MessageUtils.sendError(sender, "An error occurred while executing that command.");
            plugin.getErrorHandler().handleException("Command: " + subName, e);
            return true;
        }
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
                if (sender.hasPermission(sub.getPermission())) {
                    names.add(name);
                    names.addAll(sub.getAliases());
                }
            });
            return names.stream()
                    .filter(n -> n.startsWith(args[0].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (args.length >= 2) {
            String input = args[0].toLowerCase();
            String subName = aliases.getOrDefault(input, input);
            SubCommand sub = subCommands.get(subName);
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
        MessageUtils.sendRawMessage(sender, "");
        MessageUtils.sendRawMessage(sender, "&#00FBFF&lMUZLIK'S SMP STARTER &7- &fCommands");
        MessageUtils.sendRawMessage(sender, "");
        subCommands.forEach((name, sub) -> {
            if (sender.hasPermission(sub.getPermission())) {
                String aliasesStr = sub.getAliases().isEmpty() ? "" : " &7(" + String.join(", ", sub.getAliases()) + ")";
                MessageUtils.sendRawMessage(sender, " &#00FBFF» &b/smp " + sub.getUsage() + aliasesStr);
                MessageUtils.sendRawMessage(sender, "   &8└ &f" + sub.getDescription());
            }
        });
        MessageUtils.sendRawMessage(sender, " &#00FBFF» &b/smp help");
        MessageUtils.sendRawMessage(sender, "   &8└ &fShow this help menu.");
        MessageUtils.sendRawMessage(sender, "");
    }
}
