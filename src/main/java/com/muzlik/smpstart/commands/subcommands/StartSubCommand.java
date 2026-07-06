package com.muzlik.smpstart.commands.subcommands;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.commands.SubCommand;
import com.muzlik.smpstart.state.StateManager.PluginState;
import com.muzlik.smpstart.state.StateManagerImpl;
import com.muzlik.smpstart.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * /smp start — begins the SMP countdown sequence.
 */
public class StartSubCommand implements SubCommand {

    private final SMPStartPlugin plugin;

    public StartSubCommand(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String getName()        { return "start"; }
    @Override public List<String> getAliases() { return List.of("s"); }
    @Override public String getPermission()  { return "smpstart.use"; }
    @Override public String getUsage()       { return "start"; }
    @Override public String getDescription() { return "Begin the SMP countdown sequence."; }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        boolean smpStarted = false;
        if (plugin.getStateManager() instanceof StateManagerImpl) {
            smpStarted = ((StateManagerImpl) plugin.getStateManager()).getStateData().isSmpStarted();
        }
        if (smpStarted) {
            MessageUtils.sendInfo(sender, "The SMP has already started. Use &f/smp reset &7to start over.");
            return true;
        }

        if (!plugin.getStateManager().canExecuteStart()) {
            PluginState state = plugin.getStateManager().getCurrentState();
            if (state == PluginState.COUNTDOWN) {
                MessageUtils.sendInfo(sender, "Countdown already active — &f" +
                        plugin.getStateManager().getRemainingCountdown() + "s &7remaining.");
            } else if (state == PluginState.COOLDOWN) {
                MessageUtils.sendInfo(sender, "On cooldown — &f" +
                        plugin.getStateManager().getRemainingCooldown() + "s &7remaining.");
            }
            return true;
        }

        int minPlayers = plugin.getConfigManager().getMinOnlinePlayers();
        int online = getOnlineCount();
        if (minPlayers > 0 && online < minPlayers) {
            MessageUtils.sendError(sender, "Not enough players online. Need &f" +
                    minPlayers + "&7, have &f" + online + "&7.");
            return true;
        }

        plugin.getStateManager().startCountdown();
        Bukkit.broadcastMessage(MessageUtils.color("&#00FBFF&lSMP » &#55FF55Countdown started by &f" + sender.getName() + "&55FF55!"));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    private int getOnlineCount() {
        String worldName = plugin.getConfigManager().getWorldName();
        if (worldName != null && !worldName.isBlank()) {
            World world = Bukkit.getWorld(worldName.trim());
            if (world != null) return world.getPlayers().size();
        }
        return Bukkit.getOnlinePlayers().size();
    }
}
