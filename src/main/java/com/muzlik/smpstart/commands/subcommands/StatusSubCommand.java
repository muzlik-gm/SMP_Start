package com.muzlik.smpstart.commands.subcommands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.SubCommand;
import com.muzlik.smpstart.state.StateManager.PluginState;
import com.muzlik.smpstart.state.StateManagerImpl;
import com.muzlik.smpstart.utils.MessageUtils;
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
    @Override public List<String> getAliases() { return List.of("st", "info"); }
    @Override public String getPermission()  { return "smpstart.use"; }
    @Override public String getUsage()       { return "status"; }
    @Override public String getDescription() { return "Show current state and configuration."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        MessageUtils.sendRawMessage(sender, "");
        MessageUtils.sendRawMessage(sender, "&#00FBFF&lMUZLIK'S SMP STARTER &7- &fStatus");
        MessageUtils.sendRawMessage(sender, "");

        PluginState state = plugin.getStateManager().getCurrentState();
        MessageUtils.sendRawMessage(sender, line("State", state.name()));

        if (plugin.getStateManager() instanceof StateManagerImpl) {
            boolean started = ((StateManagerImpl) plugin.getStateManager()).getStateData().isSmpStarted();
            MessageUtils.sendRawMessage(sender, line("SMP Started", started ? "&aYes" : "&cNo"));
        }

        if (state == PluginState.COUNTDOWN) {
            MessageUtils.sendRawMessage(sender, line("Countdown Remaining", "&f" + plugin.getStateManager().getRemainingCountdown() + "s"));
        } else if (state == PluginState.COOLDOWN) {
            MessageUtils.sendRawMessage(sender, line("Cooldown Remaining", "&f" + plugin.getStateManager().getRemainingCooldown() + "s"));
        }

        MessageUtils.sendRawMessage(sender, "");
        MessageUtils.sendRawMessage(sender, line("Countdown Duration",  "&f" + plugin.getConfigManager().getCountdownDuration() + "s"));
        MessageUtils.sendRawMessage(sender, line("Cooldown Duration",   "&f" + plugin.getConfigManager().getCooldownDuration() + "s"));
        MessageUtils.sendRawMessage(sender, line("Pre-start Border",    "&f" + plugin.getConfigManager().getPreStartBorderSize() + " blocks"));
        MessageUtils.sendRawMessage(sender, line("Final Border",        "&f" + plugin.getConfigManager().getFinalBorderSize() + " blocks"));
        MessageUtils.sendRawMessage(sender, line("PvP Protection",      "&f" + plugin.getConfigManager().getPvpProtectionDuration() + " min"));
        MessageUtils.sendRawMessage(sender, line("Min Players",         "&f" + plugin.getConfigManager().getMinOnlinePlayers()));
        MessageUtils.sendRawMessage(sender, line("Join Reminders",      plugin.getConfigManager().areJoinRemindersEnabled() ? "&aEnabled" : "&cDisabled"));
        MessageUtils.sendRawMessage(sender, line("Reminder Interval",   "&f" + plugin.getConfigManager().getReminderInterval() + "s"));
        MessageUtils.sendRawMessage(sender, line("Boss Bar",            plugin.getConfigManager().isCountdownBossBarEnabled() ? "&aEnabled" : "&cDisabled"));
        MessageUtils.sendRawMessage(sender, line("Border Center",       "&f" + plugin.getConfigManager().getBorderCenterMode()));

        String world = plugin.getConfigManager().getWorldName();
        MessageUtils.sendRawMessage(sender, line("World", (world == null || world.isBlank()) ? "&fDefault" : "&f" + world));
        MessageUtils.sendRawMessage(sender, "");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    private String line(String key, String value) {
        return " &#00FBFF» &b" + key + ": " + value;
    }
}
