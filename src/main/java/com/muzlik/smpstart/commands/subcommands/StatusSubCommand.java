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
 * /smp status — shows current plugin state and configuration.
 */
public class StatusSubCommand implements SubCommand {

    private final SMPStartPlugin plugin;

    public StatusSubCommand(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String getName()        { return "status"; }
    @Override public String getPermission()  { return "smpstart.use"; }
    @Override public String getUsage()       { return "status"; }
    @Override public String getDescription() { return "Show current state and configuration."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GOLD + "--- Muzlik's SMP Starter ---");

        PluginState state = plugin.getStateManager().getCurrentState();
        sender.sendMessage(line("State", state.name()));

        if (plugin.getStateManager() instanceof StateManagerImpl) {
            boolean started = ((StateManagerImpl) plugin.getStateManager()).getStateData().isSmpStarted();
            sender.sendMessage(line("SMP Started", started ? "Yes" : "No"));
        }

        if (state == PluginState.COUNTDOWN) {
            sender.sendMessage(line("Countdown Remaining", plugin.getStateManager().getRemainingCountdown() + "s"));
        } else if (state == PluginState.COOLDOWN) {
            sender.sendMessage(line("Cooldown Remaining", plugin.getStateManager().getRemainingCooldown() + "s"));
        }

        sender.sendMessage(line("Countdown Duration",  plugin.getConfigManager().getCountdownDuration() + "s"));
        sender.sendMessage(line("Cooldown Duration",   plugin.getConfigManager().getCooldownDuration() + "s"));
        sender.sendMessage(line("Pre-start Border",    plugin.getConfigManager().getPreStartBorderSize() + " blocks"));
        sender.sendMessage(line("Final Border",        plugin.getConfigManager().getFinalBorderSize() + " blocks"));
        sender.sendMessage(line("PvP Protection",      plugin.getConfigManager().getPvpProtectionDuration() + " min"));
        sender.sendMessage(line("Min Players",         String.valueOf(plugin.getConfigManager().getMinOnlinePlayers())));
        sender.sendMessage(line("Join Reminders",      plugin.getConfigManager().areJoinRemindersEnabled() ? "Enabled" : "Disabled"));
        sender.sendMessage(line("Reminder Interval",   plugin.getConfigManager().getReminderInterval() + "s"));
        sender.sendMessage(line("Boss Bar",            plugin.getConfigManager().isCountdownBossBarEnabled() ? "Enabled" : "Disabled"));
        sender.sendMessage(line("Border Center",       plugin.getConfigManager().getBorderCenterMode()));

        String world = plugin.getConfigManager().getWorldName();
        sender.sendMessage(line("World", (world == null || world.isBlank()) ? "Default" : world));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    private String line(String key, String value) {
        return ChatColor.YELLOW + key + ": " + ChatColor.WHITE + value;
    }
}
