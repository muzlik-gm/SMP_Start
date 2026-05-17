package com.muzlik.smpstart.commands.subcommands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.SubCommand;
import com.muzlik.smpstart.state.StateManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * /smp config <key> [value] — view or change a configuration setting.
 *
 * Usage examples:
 *   /smp config countdown 30
 *   /smp config finalborder 10000
 *   /smp config pvp 30
 */
public class ConfigSubCommand implements SubCommand {

    private static final List<String> KEYS = Arrays.asList(
            "countdown", "cooldown", "preborder", "finalborder",
            "pvp", "minplayers", "reminders", "reminderinterval",
            "bossbar", "bordercenter", "bordercenterpos", "world"
    );

    private final SMPStartPlugin plugin;

    public ConfigSubCommand(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String getName()        { return "config"; }
    @Override public String getPermission()  { return "smpstart.config"; }
    @Override public String getUsage()       { return "config <key> [value]"; }
    @Override public String getDescription() { return "View or change settings or open the interactive config menu."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            if (sender instanceof org.bukkit.entity.Player player) {
                plugin.getConfigMenuManager().openMainMenu(player);
                return true;
            } else {
                showConfigHelp(sender);
                return true;
            }
        }

        String key = args[0].toLowerCase();

        // Show current value when no value provided
        if (args.length == 1 && !key.equals("bordercenterpos")) {
            showCurrentValue(sender, key);
            return true;
        }

        switch (key) {
            case "countdown":
                setInt(sender, args, 1, Integer.MAX_VALUE, v -> {
                    plugin.getConfigManager().setCountdownDuration(v);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Countdown duration set to " + v + "s.");
                });
                break;

            case "cooldown":
                setInt(sender, args, 1, Integer.MAX_VALUE, v -> {
                    plugin.getConfigManager().setCooldownDuration(v);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Cooldown duration set to " + v + "s.");
                });
                break;

            case "preborder":
                setDouble(sender, args, 1, Double.MAX_VALUE, v -> {
                    plugin.getConfigManager().setPreStartBorderSize(v);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Pre-start border set to " + v + " blocks.");
                });
                break;

            case "finalborder":
                setDouble(sender, args, 1, Double.MAX_VALUE, v -> {
                    plugin.getConfigManager().setFinalBorderSize(v);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Final border set to " + v + " blocks.");
                });
                break;

            case "pvp":
                setInt(sender, args, 0, Integer.MAX_VALUE, v -> {
                    plugin.getConfigManager().setPvpProtectionDuration(v);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "PvP protection set to " + v + " minutes.");
                });
                break;

            case "minplayers":
                setInt(sender, args, 0, Integer.MAX_VALUE, v -> {
                    plugin.getConfigManager().setMinOnlinePlayers(v);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Minimum players set to " + v + ".");
                });
                break;

            case "reminders":
                setBool(sender, args, v -> {
                    plugin.getConfigManager().setJoinRemindersEnabled(v);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Join reminders " + (v ? "enabled" : "disabled") + ".");
                });
                break;

            case "reminderinterval":
                setInt(sender, args, 1, Integer.MAX_VALUE, v -> {
                    plugin.getConfigManager().setReminderInterval(v);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Reminder interval set to " + v + "s.");
                });
                break;

            case "bossbar":
                setBool(sender, args, v -> {
                    plugin.getConfigManager().setCountdownBossBarEnabled(v);
                    plugin.getConfigManager().saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Boss bar " + (v ? "enabled" : "disabled") + ".");
                });
                break;

            case "bordercenter":
                if (args.length < 2) { sender.sendMessage(ChatColor.RED + "Usage: /smp config bordercenter <spawn|fixed>"); return true; }
                String mode = args[1].toLowerCase();
                if (!mode.equals("spawn") && !mode.equals("fixed")) {
                    sender.sendMessage(ChatColor.RED + "Invalid mode. Use spawn or fixed.");
                    return true;
                }
                plugin.getConfigManager().setBorderCenterMode(mode);
                plugin.getConfigManager().saveConfig();
                applyBorderUpdate();
                sender.sendMessage(ChatColor.GREEN + "Border center mode set to " + mode + ".");
                break;

            case "bordercenterpos":
                if (args.length < 3) { sender.sendMessage(ChatColor.RED + "Usage: /smp config bordercenterpos <x> <z>"); return true; }
                try {
                    double x = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    plugin.getConfigManager().setBorderCenterPosition(x, z);
                    plugin.getConfigManager().saveConfig();
                    applyBorderUpdate();
                    sender.sendMessage(ChatColor.GREEN + "Border center set to X=" + x + ", Z=" + z + ".");
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid coordinates.");
                }
                break;

            case "world":
                if (args.length < 2) { sender.sendMessage(ChatColor.RED + "Usage: /smp config world <name|default>"); return true; }
                String worldArg = args[1];
                if (worldArg.equalsIgnoreCase("default")) {
                    plugin.getConfigManager().setWorldName("");
                    plugin.getConfigManager().saveConfig();
                    applyBorderUpdate();
                    sender.sendMessage(ChatColor.GREEN + "World set to default.");
                } else {
                    World world = Bukkit.getWorld(worldArg);
                    if (world == null) { sender.sendMessage(ChatColor.RED + "World not found: " + worldArg); return true; }
                    plugin.getConfigManager().setWorldName(world.getName());
                    plugin.getConfigManager().saveConfig();
                    applyBorderUpdate();
                    sender.sendMessage(ChatColor.GREEN + "World set to " + world.getName() + ".");
                }
                break;

            default:
                sender.sendMessage(ChatColor.RED + "Unknown setting: " + key + ". Run /smp config for a list.");
                break;
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return KEYS.stream()
                    .filter(k -> k.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "reminders":
                case "bossbar":
                    return Arrays.asList("true", "false");
                case "bordercenter":
                    return Arrays.asList("spawn", "fixed");
                case "world":
                    List<String> worlds = Bukkit.getWorlds().stream()
                            .map(World::getName).collect(Collectors.toList());
                    worlds.add("default");
                    return worlds;
            }
        }
        return Collections.emptyList();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void showConfigHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- Config Settings ---");
        sender.sendMessage(ChatColor.YELLOW + "countdown" + ChatColor.GRAY + " <seconds>       " + ChatColor.WHITE + "Countdown duration");
        sender.sendMessage(ChatColor.YELLOW + "cooldown" + ChatColor.GRAY + " <seconds>        " + ChatColor.WHITE + "Cooldown duration");
        sender.sendMessage(ChatColor.YELLOW + "preborder" + ChatColor.GRAY + " <blocks>        " + ChatColor.WHITE + "Pre-start border size");
        sender.sendMessage(ChatColor.YELLOW + "finalborder" + ChatColor.GRAY + " <blocks>      " + ChatColor.WHITE + "Final border size");
        sender.sendMessage(ChatColor.YELLOW + "pvp" + ChatColor.GRAY + " <minutes>            " + ChatColor.WHITE + "PvP protection duration");
        sender.sendMessage(ChatColor.YELLOW + "minplayers" + ChatColor.GRAY + " <count>        " + ChatColor.WHITE + "Minimum online players");
        sender.sendMessage(ChatColor.YELLOW + "reminders" + ChatColor.GRAY + " <true|false>    " + ChatColor.WHITE + "Join reminders toggle");
        sender.sendMessage(ChatColor.YELLOW + "reminderinterval" + ChatColor.GRAY + " <sec>   " + ChatColor.WHITE + "Reminder interval");
        sender.sendMessage(ChatColor.YELLOW + "bossbar" + ChatColor.GRAY + " <true|false>      " + ChatColor.WHITE + "Countdown boss bar toggle");
        sender.sendMessage(ChatColor.YELLOW + "bordercenter" + ChatColor.GRAY + " <spawn|fixed>" + ChatColor.WHITE + " Border center mode");
        sender.sendMessage(ChatColor.YELLOW + "bordercenterpos" + ChatColor.GRAY + " <x> <z>  " + ChatColor.WHITE + "Fixed border center coords");
        sender.sendMessage(ChatColor.YELLOW + "world" + ChatColor.GRAY + " <name|default>      " + ChatColor.WHITE + "Target world");
    }

    private void showCurrentValue(CommandSender sender, String key) {
        String value;
        switch (key) {
            case "countdown":       value = plugin.getConfigManager().getCountdownDuration() + "s"; break;
            case "cooldown":        value = plugin.getConfigManager().getCooldownDuration() + "s"; break;
            case "preborder":       value = plugin.getConfigManager().getPreStartBorderSize() + " blocks"; break;
            case "finalborder":     value = plugin.getConfigManager().getFinalBorderSize() + " blocks"; break;
            case "pvp":             value = plugin.getConfigManager().getPvpProtectionDuration() + " min"; break;
            case "minplayers":      value = String.valueOf(plugin.getConfigManager().getMinOnlinePlayers()); break;
            case "reminders":       value = String.valueOf(plugin.getConfigManager().areJoinRemindersEnabled()); break;
            case "reminderinterval":value = plugin.getConfigManager().getReminderInterval() + "s"; break;
            case "bossbar":         value = String.valueOf(plugin.getConfigManager().isCountdownBossBarEnabled()); break;
            case "bordercenter":    value = plugin.getConfigManager().getBorderCenterMode(); break;
            case "world":
                String w = plugin.getConfigManager().getWorldName();
                value = (w == null || w.isBlank()) ? "default" : w;
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown setting: " + key);
                return;
        }
        sender.sendMessage(ChatColor.YELLOW + key + ": " + ChatColor.WHITE + value);
    }

    private void setInt(CommandSender sender, String[] args, int min, int max, java.util.function.IntConsumer action) {
        if (args.length < 2) { sender.sendMessage(ChatColor.RED + "A value is required."); return; }
        try {
            int v = Integer.parseInt(args[1]);
            if (v < min) { sender.sendMessage(ChatColor.RED + "Value must be at least " + min + "."); return; }
            action.accept(v);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number: " + args[1]);
        }
    }

    private void setDouble(CommandSender sender, String[] args, double min, double max, java.util.function.DoubleConsumer action) {
        if (args.length < 2) { sender.sendMessage(ChatColor.RED + "A value is required."); return; }
        try {
            double v = Double.parseDouble(args[1]);
            if (v < min) { sender.sendMessage(ChatColor.RED + "Value must be at least " + min + "."); return; }
            action.accept(v);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number: " + args[1]);
        }
    }

    private void setBool(CommandSender sender, String[] args, java.util.function.Consumer<Boolean> action) {
        if (args.length < 2) { sender.sendMessage(ChatColor.RED + "Use true or false."); return; }
        String val = args[1].toLowerCase();
        if (!val.equals("true") && !val.equals("false")) {
            sender.sendMessage(ChatColor.RED + "Use true or false.");
            return;
        }
        action.accept(Boolean.parseBoolean(val));
    }

    private void applyBorderUpdate() {
        if (plugin.getBorderManager() == null || plugin.getStateManager() == null) return;
        boolean started = false;
        if (plugin.getStateManager() instanceof StateManagerImpl) {
            started = ((StateManagerImpl) plugin.getStateManager()).getStateData().isSmpStarted();
        }
        if (started) plugin.getBorderManager().setFinalBorder();
        else plugin.getBorderManager().setPreStartBorder();
    }
}
