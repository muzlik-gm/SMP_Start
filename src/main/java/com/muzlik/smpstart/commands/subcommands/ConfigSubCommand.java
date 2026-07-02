package com.muzlik.smpstart.commands.subcommands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.SubCommand;
import com.muzlik.smpstart.state.StateManagerImpl;
import com.muzlik.smpstart.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

/**
 * /smp config [key] [value] — view or change settings via command line.
 */
public class ConfigSubCommand implements SubCommand {

    private final SMPStartPlugin plugin;

    private static final List<String> KEYS = Arrays.asList(
            "countdown", "cooldown", "preborder", "finalborder",
            "pvp", "minplayers", "reminders", "reminderinterval",
            "bossbar", "bordercenter", "bordercenterpos", "world"
    );

    public ConfigSubCommand(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String getName()        { return "config"; }
    @Override public List<String> getAliases() { return List.of("c", "settings"); }
    @Override public String getPermission()  { return "smpstart.config"; }
    @Override public String getUsage()       { return "config [setting] [value]"; }
    @Override public String getDescription() { return "View or change plugin settings."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showConfigHelp(sender);
            return true;
        }

        String key = args[0].toLowerCase();

        if (args.length == 1) {
            showCurrentValue(sender, key);
            return true;
        }

        switch (key) {
            case "countdown":
                setInt(sender, args, 1, 3600, v -> {
                    plugin.getConfigManager().setCountdownDuration(v);
                    plugin.getConfigManager().saveConfig();
                    MessageUtils.sendSuccess(sender, "Countdown duration set to &f" + v + "s&7.");
                });
                break;

            case "cooldown":
                setInt(sender, args, 0, 86400, v -> {
                    plugin.getConfigManager().setCooldownDuration(v);
                    plugin.getConfigManager().saveConfig();
                    MessageUtils.sendSuccess(sender, "Cooldown duration set to &f" + v + "s&7.");
                });
                break;

            case "preborder":
                setDouble(sender, args, 1.0, 1000000.0, v -> {
                    plugin.getConfigManager().setPreStartBorderSize(v);
                    plugin.getConfigManager().saveConfig();
                    applyBorderUpdate();
                    MessageUtils.sendSuccess(sender, "Pre-start border size set to &f" + v + " blocks&7.");
                });
                break;

            case "finalborder":
                setDouble(sender, args, 1.0, 1000000.0, v -> {
                    plugin.getConfigManager().setFinalBorderSize(v);
                    plugin.getConfigManager().saveConfig();
                    applyBorderUpdate();
                    MessageUtils.sendSuccess(sender, "Final border size set to &f" + v + " blocks&7.");
                });
                break;

            case "pvp":
                setInt(sender, args, 0, 10000, v -> {
                    plugin.getConfigManager().setPvpProtectionDuration(v);
                    plugin.getConfigManager().saveConfig();
                    MessageUtils.sendSuccess(sender, "PvP protection set to &f" + v + " min&7.");
                });
                break;

            case "minplayers":
                setInt(sender, args, 0, 1000, v -> {
                    plugin.getConfigManager().setMinOnlinePlayers(v);
                    plugin.getConfigManager().saveConfig();
                    MessageUtils.sendSuccess(sender, "Minimum players set to &f" + v + "&7.");
                });
                break;

            case "reminders":
                setBool(sender, args, v -> {
                    plugin.getConfigManager().setJoinRemindersEnabled(v);
                    plugin.getConfigManager().saveConfig();
                    MessageUtils.sendSuccess(sender, "Join reminders " + (v ? "&aenabled" : "&cdisabled") + "&7.");
                });
                break;

            case "reminderinterval":
                setInt(sender, args, 1, 3600, v -> {
                    plugin.getConfigManager().setReminderInterval(v);
                    plugin.getConfigManager().saveConfig();
                    MessageUtils.sendSuccess(sender, "Reminder interval set to &f" + v + "s&7.");
                });
                break;

            case "bossbar":
                setBool(sender, args, v -> {
                    plugin.getConfigManager().setCountdownBossBarEnabled(v);
                    plugin.getConfigManager().saveConfig();
                    MessageUtils.sendSuccess(sender, "Countdown boss bar " + (v ? "&aenabled" : "&cdisabled") + "&7.");
                });
                break;

            case "bordercenter":
                if (args.length < 2) { MessageUtils.sendError(sender, "Usage: &f/smp config bordercenter <spawn|fixed>"); return true; }
                String mode = args[1].toLowerCase();
                if (!mode.equals("spawn") && !mode.equals("fixed")) {
                    MessageUtils.sendError(sender, "Invalid mode. Use &fspawn &7or &ffixed&7.");
                    return true;
                }
                plugin.getConfigManager().setBorderCenterMode(mode);
                plugin.getConfigManager().saveConfig();
                applyBorderUpdate();
                MessageUtils.sendSuccess(sender, "Border center mode set to &f" + mode + "&7.");
                break;

            case "bordercenterpos":
                if (args.length < 3) { MessageUtils.sendError(sender, "Usage: &f/smp config bordercenterpos <x> <z>"); return true; }
                try {
                    double x = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    plugin.getConfigManager().setBorderCenterPosition(x, z);
                    plugin.getConfigManager().saveConfig();
                    applyBorderUpdate();
                    MessageUtils.sendSuccess(sender, "Border center set to &fX=" + x + ", Z=" + z + "&7.");
                } catch (NumberFormatException e) {
                    MessageUtils.sendError(sender, "Invalid coordinates.");
                }
                break;

            case "world":
                if (args.length < 2) { MessageUtils.sendError(sender, "Usage: &f/smp config world <name|default>"); return true; }
                String worldArg = args[1];
                if (worldArg.equalsIgnoreCase("default")) {
                    plugin.getConfigManager().setWorldName("");
                    plugin.getConfigManager().saveConfig();
                    applyBorderUpdate();
                    MessageUtils.sendSuccess(sender, "World set to &fdefault&7.");
                } else {
                    World world = Bukkit.getWorld(worldArg);
                    if (world == null) { MessageUtils.sendError(sender, "World not found: &f" + worldArg); return true; }
                    plugin.getConfigManager().setWorldName(world.getName());
                    plugin.getConfigManager().saveConfig();
                    applyBorderUpdate();
                    MessageUtils.sendSuccess(sender, "World set to &f" + world.getName() + "&7.");
                }
                break;

            default:
                MessageUtils.sendError(sender, "Unknown setting: &f" + key + "&7. Run &f/smp config &7for a list.");
                break;
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return KEYS.stream()
                    .filter(k -> k.startsWith(args[0].toLowerCase()))
                    .sorted()
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
        MessageUtils.sendRawMessage(sender, "");
        MessageUtils.sendRawMessage(sender, "&#00FBFF&lCONFIG SETTINGS");
        MessageUtils.sendRawMessage(sender, "");
        for (String key : KEYS) {
             MessageUtils.sendRawMessage(sender, " &#00FBFF» &b" + key);
        }
        MessageUtils.sendRawMessage(sender, "");
        MessageUtils.sendRawMessage(sender, "&7Use &f/smp config <setting> <value> &7to update.");
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
                MessageUtils.sendError(sender, "Unknown setting: &f" + key);
                return;
        }
        MessageUtils.sendInfo(sender, "Current value for &f" + key + "&7: &f" + value);
    }

    private void setInt(CommandSender sender, String[] args, int min, int max, java.util.function.IntConsumer action) {
        if (args.length < 2) { MessageUtils.sendError(sender, "A value is required."); return; }
        try {
            int v = Integer.parseInt(args[1]);
            if (v < min) { MessageUtils.sendError(sender, "Value must be at least &f" + min + "&7."); return; }
            if (v > max) { MessageUtils.sendError(sender, "Value must be at most &f" + max + "&7."); return; }
            action.accept(v);
        } catch (NumberFormatException e) {
            MessageUtils.sendError(sender, "Invalid number: &f" + args[1]);
        }
    }

    private void setDouble(CommandSender sender, String[] args, double min, double max, java.util.function.DoubleConsumer action) {
        if (args.length < 2) { MessageUtils.sendError(sender, "A value is required."); return; }
        try {
            double v = Double.parseDouble(args[1]);
            if (v < min) { MessageUtils.sendError(sender, "Value must be at least &f" + min + "&7."); return; }
            if (v > max) { MessageUtils.sendError(sender, "Value must be at most &f" + max + "&7."); return; }
            action.accept(v);
        } catch (NumberFormatException e) {
            MessageUtils.sendError(sender, "Invalid number: &f" + args[1]);
        }
    }

    private void setBool(CommandSender sender, String[] args, java.util.function.Consumer<Boolean> action) {
        if (args.length < 2) { MessageUtils.sendError(sender, "Use &ftrue &7or &ffalse&7."); return; }
        String val = args[1].toLowerCase();
        if (!val.equals("true") && !val.equals("false")) {
            MessageUtils.sendError(sender, "Use &ftrue &7or &ffalse&7.");
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
