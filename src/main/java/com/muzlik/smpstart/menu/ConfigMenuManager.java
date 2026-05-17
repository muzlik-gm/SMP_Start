package com.muzlik.smpstart.menu;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.state.StateManager.PluginState;
import com.muzlik.smpstart.state.StateManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Clean, simplistic GUI for SMP Starter.
 * Design: dark glass borders, named panes as buttons, small-caps style text.
 */
public class ConfigMenuManager implements Listener {

    // ── Titles (kept short — Minecraft truncates long titles) ─────────────────
    private static final String TITLE_MAIN      = ChatColor.DARK_GRAY + "SMP Starter";
    private static final String TITLE_COUNTDOWN = ChatColor.DARK_GRAY + "Countdown";
    private static final String TITLE_BORDER    = ChatColor.DARK_GRAY + "World Border";
    private static final String TITLE_PVP       = ChatColor.DARK_GRAY + "PvP";
    private static final String TITLE_REMINDERS = ChatColor.DARK_GRAY + "Reminders";
    private static final String TITLE_PHASES    = ChatColor.DARK_GRAY + "Phases";

    // ── Shared pane materials ─────────────────────────────────────────────────
    private static final Material PANE_DARK   = Material.BLACK_STAINED_GLASS_PANE;
    private static final Material PANE_ACCENT = Material.GRAY_STAINED_GLASS_PANE;

    // ── Main menu slot map ────────────────────────────────────────────────────
    //  Row 0 (0-8):   top border
    //  Row 1 (9-17):  status info
    //  Row 2 (18-26): action buttons
    //  Row 3 (27-35): divider
    //  Row 4 (36-44): category buttons
    //  Row 5 (45-53): bottom border

    private static final int SLOT_STATUS_STATE   = 10;
    private static final int SLOT_STATUS_BORDER  = 12;
    private static final int SLOT_STATUS_PVP     = 14;
    private static final int SLOT_STATUS_PLAYERS = 16;

    private static final int SLOT_START   = 19;
    private static final int SLOT_CANCEL  = 21;
    private static final int SLOT_RESET   = 23;
    private static final int SLOT_RELOAD  = 25;

    private static final int SLOT_CAT_COUNTDOWN = 37;
    private static final int SLOT_CAT_BORDER    = 39;
    private static final int SLOT_CAT_PVP       = 41;
    private static final int SLOT_CAT_REMINDERS = 43;
    private static final int SLOT_CAT_PHASES    = 36;

    private final SMPStartPlugin plugin;
    private final Map<UUID, PlayerMenuState> menuStates  = new HashMap<>();
    private final Map<UUID, String>          openMenus   = new HashMap<>();
    private final Map<UUID, Double>          pendingBorderX = new HashMap<>();

    public ConfigMenuManager(SMPStartPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    // =========================================================================
    //  MAIN MENU
    // =========================================================================

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE_MAIN);

        // Full border + divider row
        fill(inv, PANE_DARK, 0,1,2,3,4,5,6,7,8);                        // top
        fill(inv, PANE_DARK, 45,46,47,48,49,50,51,52,53);                // bottom
        fill(inv, PANE_DARK, 9,17,18,26,27,28,29,30,31,32,33,34,35,44); // sides + divider

        // ── Status row ────────────────────────────────────────────────────────
        PluginState state  = plugin.getStateManager().getCurrentState();
        boolean started    = isSmpStarted();

        String stateValue  = started                       ? ChatColor.GREEN  + "STARTED"
                           : state == PluginState.COUNTDOWN ? ChatColor.YELLOW + "COUNTDOWN  " + plugin.getStateManager().getRemainingCountdown() + "s"
                           : state == PluginState.COOLDOWN  ? ChatColor.AQUA   + "COOLDOWN  "  + plugin.getStateManager().getRemainingCooldown()  + "s"
                           :                                  ChatColor.RED    + "IDLE";

        inv.setItem(SLOT_STATUS_STATE, pane(PANE_ACCENT,
                ChatColor.WHITE + "STATE",
                ChatColor.DARK_GRAY + "» " + stateValue));

        double pre  = plugin.getConfigManager().getPreStartBorderSize();
        double fin  = plugin.getConfigManager().getFinalBorderSize();
        inv.setItem(SLOT_STATUS_BORDER, pane(PANE_ACCENT,
                ChatColor.WHITE + "BORDER",
                ChatColor.DARK_GRAY + "Pre  " + ChatColor.GRAY + fmt(pre),
                ChatColor.DARK_GRAY + "Post " + ChatColor.GRAY + fmt(fin)));

        int pvpMin = plugin.getConfigManager().getPvpProtectionDuration();
        inv.setItem(SLOT_STATUS_PVP, pane(PANE_ACCENT,
                ChatColor.WHITE + "PVP PROTECTION",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + pvpMin + " min"));

        int minP   = plugin.getConfigManager().getMinOnlinePlayers();
        int online = Bukkit.getOnlinePlayers().size();
        inv.setItem(SLOT_STATUS_PLAYERS, pane(PANE_ACCENT,
                ChatColor.WHITE + "PLAYERS",
                ChatColor.DARK_GRAY + "Online   " + ChatColor.GRAY + online,
                ChatColor.DARK_GRAY + "Required " + ChatColor.GRAY + minP));

        // ── Action buttons ────────────────────────────────────────────────────
        boolean canStart  = plugin.getStateManager().canExecuteStart();
        boolean canCancel = state == PluginState.COUNTDOWN;

        inv.setItem(SLOT_START, pane(
                canStart ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE,
                canStart ? ChatColor.GREEN + "START SMP" : ChatColor.DARK_GRAY + "START SMP",
                canStart ? ChatColor.GRAY + "Begin the countdown."
                         : ChatColor.DARK_GRAY + "Not available right now."));

        inv.setItem(SLOT_CANCEL, pane(
                canCancel ? Material.RED_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE,
                canCancel ? ChatColor.RED + "CANCEL" : ChatColor.DARK_GRAY + "CANCEL",
                canCancel ? ChatColor.GRAY + "Stop the active countdown."
                          : ChatColor.DARK_GRAY + "No countdown running."));

        inv.setItem(SLOT_RESET, pane(Material.ORANGE_STAINED_GLASS_PANE,
                ChatColor.GOLD + "RESET SMP",
                ChatColor.GRAY + "Revert to pre-start state.",
                ChatColor.DARK_GRAY + "Teleports all players to spawn."));

        inv.setItem(SLOT_RELOAD, pane(Material.CYAN_STAINED_GLASS_PANE,
                ChatColor.AQUA + "RELOAD CONFIG",
                ChatColor.GRAY + "Reload config.yml from disk."));

        // ── Category buttons ──────────────────────────────────────────────────
        inv.setItem(SLOT_CAT_PHASES,    pane(Material.GREEN_STAINED_GLASS_PANE,
                ChatColor.GREEN + "PHASES",
                ChatColor.GRAY + "Difficulty, mobs, min players."));

        inv.setItem(SLOT_CAT_COUNTDOWN, pane(Material.YELLOW_STAINED_GLASS_PANE,
                ChatColor.YELLOW + "COUNTDOWN",
                ChatColor.GRAY + "Duration, cooldown, boss bar."));

        inv.setItem(SLOT_CAT_BORDER, pane(Material.PURPLE_STAINED_GLASS_PANE,
                ChatColor.LIGHT_PURPLE + "WORLD BORDER",
                ChatColor.GRAY + "Sizes, center, transition."));

        inv.setItem(SLOT_CAT_PVP, pane(Material.RED_STAINED_GLASS_PANE,
                ChatColor.RED + "PVP",
                ChatColor.GRAY + "Protection duration."));

        inv.setItem(SLOT_CAT_REMINDERS, pane(Material.ORANGE_STAINED_GLASS_PANE,
                ChatColor.GOLD + "REMINDERS",
                ChatColor.GRAY + "Join reminders & interval."));

        openMenus.put(player.getUniqueId(), TITLE_MAIN);
        player.openInventory(inv);
    }

    // =========================================================================
    //  COUNTDOWN MENU  (27 slots — 3 rows)
    // =========================================================================

    public void openCountdownMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE_COUNTDOWN);
        fill(inv, PANE_DARK, 0,1,2,3,4,5,6,7,8);
        fill(inv, PANE_DARK, 18,19,20,21,22,23,24,25,26);
        fill(inv, PANE_DARK, 9,17);

        int cd   = plugin.getConfigManager().getCountdownDuration();
        int cool = plugin.getConfigManager().getCooldownDuration();
        boolean bb = plugin.getConfigManager().isCountdownBossBarEnabled();

        inv.setItem(10, pane(Material.YELLOW_STAINED_GLASS_PANE,
                ChatColor.YELLOW + "COUNTDOWN DURATION",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + cd + "s",
                ChatColor.DARK_GRAY + "Click to change."));

        inv.setItem(12, pane(Material.BLUE_STAINED_GLASS_PANE,
                ChatColor.AQUA + "COOLDOWN DURATION",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + cool + "s",
                ChatColor.DARK_GRAY + "Click to change."));

        inv.setItem(14, pane(bb ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE,
                ChatColor.WHITE + "BOSS BAR",
                ChatColor.DARK_GRAY + "» " + (bb ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
                ChatColor.DARK_GRAY + "Click to toggle."));

        inv.setItem(16, pane(PANE_ACCENT,
                ChatColor.GRAY + "BACK",
                ChatColor.DARK_GRAY + "Return to main menu."));

        openMenus.put(player.getUniqueId(), TITLE_COUNTDOWN);
        player.openInventory(inv);
    }

    // =========================================================================
    //  BORDER MENU  (36 slots — 4 rows)
    // =========================================================================

    public void openBorderMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, TITLE_BORDER);
        fill(inv, PANE_DARK, 0,1,2,3,4,5,6,7,8);
        fill(inv, PANE_DARK, 27,28,29,30,31,32,33,34,35);
        fill(inv, PANE_DARK, 9,17,18,26);

        double pre  = plugin.getConfigManager().getPreStartBorderSize();
        double fin  = plugin.getConfigManager().getFinalBorderSize();
        int    trans = plugin.getConfigManager().getBorderTransitionSeconds();
        String mode  = plugin.getConfigManager().getBorderCenterMode();
        boolean fixed = "fixed".equalsIgnoreCase(mode);

        inv.setItem(10, pane(Material.ORANGE_STAINED_GLASS_PANE,
                ChatColor.GOLD + "PRE-START SIZE",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + fmt(pre) + " blocks",
                ChatColor.DARK_GRAY + "Click to change."));

        inv.setItem(12, pane(Material.LIME_STAINED_GLASS_PANE,
                ChatColor.GREEN + "FINAL SIZE",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + fmt(fin) + " blocks",
                ChatColor.DARK_GRAY + "Click to change."));

        inv.setItem(14, pane(Material.CYAN_STAINED_GLASS_PANE,
                ChatColor.AQUA + "TRANSITION",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + trans + "s",
                ChatColor.DARK_GRAY + "Click to change."));

        inv.setItem(16, pane(fixed ? Material.PURPLE_STAINED_GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE,
                ChatColor.LIGHT_PURPLE + "CENTER MODE",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + mode.toUpperCase(),
                ChatColor.DARK_GRAY + "Click to toggle SPAWN / FIXED."));

        if (fixed) {
            double cx = plugin.getConfigManager().getBorderCenterX();
            double cz = plugin.getConfigManager().getBorderCenterZ();
            inv.setItem(21, pane(Material.MAGENTA_STAINED_GLASS_PANE,
                    ChatColor.LIGHT_PURPLE + "FIXED COORDS",
                    ChatColor.DARK_GRAY + "X  " + ChatColor.GRAY + cx,
                    ChatColor.DARK_GRAY + "Z  " + ChatColor.GRAY + cz,
                    ChatColor.DARK_GRAY + "Click to set new X (then Z)."));
        }

        inv.setItem(27 + 8, pane(PANE_ACCENT,
                ChatColor.GRAY + "BACK",
                ChatColor.DARK_GRAY + "Return to main menu."));

        openMenus.put(player.getUniqueId(), TITLE_BORDER);
        player.openInventory(inv);
    }

    // =========================================================================
    //  PVP MENU  (27 slots)
    // =========================================================================

    public void openPvpMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE_PVP);
        fill(inv, PANE_DARK, 0,1,2,3,4,5,6,7,8);
        fill(inv, PANE_DARK, 18,19,20,21,22,23,24,25,26);
        fill(inv, PANE_DARK, 9,17);

        int pvp = plugin.getConfigManager().getPvpProtectionDuration();
        inv.setItem(13, pane(Material.RED_STAINED_GLASS_PANE,
                ChatColor.RED + "PROTECTION DURATION",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + pvp + " min",
                ChatColor.DARK_GRAY + "Set 0 to disable.",
                ChatColor.DARK_GRAY + "Click to change."));

        inv.setItem(16, pane(PANE_ACCENT,
                ChatColor.GRAY + "BACK",
                ChatColor.DARK_GRAY + "Return to main menu."));

        openMenus.put(player.getUniqueId(), TITLE_PVP);
        player.openInventory(inv);
    }

    // =========================================================================
    //  REMINDERS MENU  (27 slots)
    // =========================================================================

    public void openRemindersMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE_REMINDERS);
        fill(inv, PANE_DARK, 0,1,2,3,4,5,6,7,8);
        fill(inv, PANE_DARK, 18,19,20,21,22,23,24,25,26);
        fill(inv, PANE_DARK, 9,17);

        boolean en  = plugin.getConfigManager().areJoinRemindersEnabled();
        int interval = plugin.getConfigManager().getReminderInterval();

        inv.setItem(11, pane(en ? Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE,
                ChatColor.WHITE + "JOIN REMINDERS",
                ChatColor.DARK_GRAY + "» " + (en ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"),
                ChatColor.DARK_GRAY + "Click to toggle."));

        inv.setItem(13, pane(Material.ORANGE_STAINED_GLASS_PANE,
                ChatColor.GOLD + "REMINDER INTERVAL",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + interval + "s",
                ChatColor.DARK_GRAY + "Click to change."));

        inv.setItem(16, pane(PANE_ACCENT,
                ChatColor.GRAY + "BACK",
                ChatColor.DARK_GRAY + "Return to main menu."));

        openMenus.put(player.getUniqueId(), TITLE_REMINDERS);
        player.openInventory(inv);
    }

    // =========================================================================
    //  PHASES MENU  (45 slots — 5 rows)
    // =========================================================================

    public void openPhasesMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE_PHASES);
        fill(inv, PANE_DARK, 0,1,2,3,4,5,6,7,8);
        fill(inv, PANE_DARK, 36,37,38,39,40,41,42,43,44);
        fill(inv, PANE_DARK, 9,17,18,26,27,35);

        // ── Pre-start column (slots 10-16 area) ───────────────────────────────
        inv.setItem(10, pane(PANE_ACCENT, ChatColor.DARK_GRAY + "PRE-START PHASE"));

        String preDiff = plugin.getConfigManager().getStartingDifficulty();
        inv.setItem(11, pane(Material.YELLOW_STAINED_GLASS_PANE,
                ChatColor.YELLOW + "DIFFICULTY",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + preDiff.toUpperCase(),
                ChatColor.DARK_GRAY + "Click to cycle."));

        boolean preMobs = plugin.getConfigManager().isStartingDisableMobSpawning();
        inv.setItem(12, pane(preMobs ? Material.RED_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE,
                ChatColor.WHITE + "MOB SPAWNING",
                ChatColor.DARK_GRAY + "» " + (preMobs ? ChatColor.RED + "DISABLED" : ChatColor.GREEN + "ENABLED"),
                ChatColor.DARK_GRAY + "Click to toggle."));

        boolean preDmg = plugin.getConfigManager().isStartingDisableMobDamage();
        inv.setItem(13, pane(preDmg ? Material.RED_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE,
                ChatColor.WHITE + "MOB DAMAGE",
                ChatColor.DARK_GRAY + "» " + (preDmg ? ChatColor.RED + "DISABLED" : ChatColor.GREEN + "ENABLED"),
                ChatColor.DARK_GRAY + "Click to toggle."));

        // ── Divider ───────────────────────────────────────────────────────────
        fill(inv, PANE_DARK, 19, 28);

        // ── Started column ────────────────────────────────────────────────────
        inv.setItem(21, pane(PANE_ACCENT, ChatColor.DARK_GRAY + "STARTED PHASE"));

        String startedDiff = plugin.getConfigManager().getStartedDifficulty();
        inv.setItem(22, pane(Material.YELLOW_STAINED_GLASS_PANE,
                ChatColor.YELLOW + "DIFFICULTY",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + startedDiff.toUpperCase(),
                ChatColor.DARK_GRAY + "Click to cycle."));

        boolean startedMobs = plugin.getConfigManager().isStartedDisableMobSpawning();
        inv.setItem(23, pane(startedMobs ? Material.RED_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE,
                ChatColor.WHITE + "MOB SPAWNING",
                ChatColor.DARK_GRAY + "» " + (startedMobs ? ChatColor.RED + "DISABLED" : ChatColor.GREEN + "ENABLED"),
                ChatColor.DARK_GRAY + "Click to toggle."));

        boolean startedDmg = plugin.getConfigManager().isStartedDisableMobDamage();
        inv.setItem(24, pane(startedDmg ? Material.RED_STAINED_GLASS_PANE : Material.LIME_STAINED_GLASS_PANE,
                ChatColor.WHITE + "MOB DAMAGE",
                ChatColor.DARK_GRAY + "» " + (startedDmg ? ChatColor.RED + "DISABLED" : ChatColor.GREEN + "ENABLED"),
                ChatColor.DARK_GRAY + "Click to toggle."));

        // ── Min players ───────────────────────────────────────────────────────
        int minP = plugin.getConfigManager().getMinOnlinePlayers();
        inv.setItem(31, pane(Material.CYAN_STAINED_GLASS_PANE,
                ChatColor.AQUA + "MIN PLAYERS",
                ChatColor.DARK_GRAY + "» " + ChatColor.GRAY + minP,
                ChatColor.DARK_GRAY + "Required to start.",
                ChatColor.DARK_GRAY + "Click to change."));

        inv.setItem(44, pane(PANE_ACCENT,
                ChatColor.GRAY + "BACK",
                ChatColor.DARK_GRAY + "Return to main menu."));

        openMenus.put(player.getUniqueId(), TITLE_PHASES);
        player.openInventory(inv);
    }

    // =========================================================================
    //  CLICK HANDLERS
    // =========================================================================

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (!isOurMenu(title)) return;

        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        if (item.getType() == PANE_DARK) return;

        int slot = event.getSlot();
        if      (title.equals(TITLE_MAIN))      handleMain(player, slot);
        else if (title.equals(TITLE_COUNTDOWN)) handleCountdown(player, slot);
        else if (title.equals(TITLE_BORDER))    handleBorder(player, slot);
        else if (title.equals(TITLE_PVP))       handlePvp(player, slot);
        else if (title.equals(TITLE_REMINDERS)) handleReminders(player, slot);
        else if (title.equals(TITLE_PHASES))    handlePhases(player, slot);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        PlayerMenuState ms = menuStates.get(player.getUniqueId());
        if (ms == null || !ms.isAwaitingInput()) openMenus.remove(player.getUniqueId());
    }

    // ── Main ──────────────────────────────────────────────────────────────────

    private void handleMain(Player player, int slot) {
        switch (slot) {
            case SLOT_START -> {
                player.closeInventory();
                if (!plugin.getStateManager().canExecuteStart()) {
                    player.sendMessage(ChatColor.RED + "Cannot start right now."); return;
                }
                int minP = plugin.getConfigManager().getMinOnlinePlayers();
                int on   = Bukkit.getOnlinePlayers().size();
                if (minP > 0 && on < minP) {
                    player.sendMessage(ChatColor.RED + "Need " + minP + " players, have " + on + "."); return;
                }
                plugin.getStateManager().startCountdown();
                plugin.getServer().broadcastMessage(ChatColor.GREEN + "SMP countdown started by " + ChatColor.WHITE + player.getName() + ChatColor.GREEN + "!");
            }
            case SLOT_CANCEL -> {
                player.closeInventory();
                if (!plugin.getStateManager().cancelCountdown())
                    player.sendMessage(ChatColor.YELLOW + "No active countdown.");
                else
                    plugin.getServer().broadcastMessage(ChatColor.RED + "Countdown cancelled by " + ChatColor.WHITE + player.getName() + ChatColor.RED + ".");
            }
            case SLOT_RESET -> {
                player.closeInventory();
                plugin.getStateManager().resetSmp();
                plugin.getServer().broadcastMessage(ChatColor.RED + "SMP reset by " + ChatColor.WHITE + player.getName() + ChatColor.RED + ".");
            }
            case SLOT_RELOAD -> {
                plugin.getConfigManager().reloadConfig();
                player.sendMessage(ChatColor.GREEN + "Config reloaded.");
                openMainMenu(player);
            }
            case SLOT_CAT_COUNTDOWN -> openCountdownMenu(player);
            case SLOT_CAT_BORDER    -> openBorderMenu(player);
            case SLOT_CAT_PVP       -> openPvpMenu(player);
            case SLOT_CAT_REMINDERS -> openRemindersMenu(player);
            case SLOT_CAT_PHASES    -> openPhasesMenu(player);
        }
    }

    // ── Countdown ─────────────────────────────────────────────────────────────

    private void handleCountdown(Player player, int slot) {
        switch (slot) {
            case 10 -> prompt(player, PlayerMenuState.InputMode.COUNTDOWN_DURATION,
                    ChatColor.YELLOW + "Enter countdown duration in seconds:");
            case 12 -> prompt(player, PlayerMenuState.InputMode.COOLDOWN_DURATION,
                    ChatColor.YELLOW + "Enter cooldown duration in seconds:");
            case 14 -> {
                boolean cur = plugin.getConfigManager().isCountdownBossBarEnabled();
                plugin.getConfigManager().setCountdownBossBarEnabled(!cur);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Boss bar " + (!cur ? "enabled" : "disabled") + ".");
                openCountdownMenu(player);
            }
            case 16 -> openMainMenu(player);
        }
    }

    // ── Border ────────────────────────────────────────────────────────────────

    private void handleBorder(Player player, int slot) {
        switch (slot) {
            case 10 -> prompt(player, PlayerMenuState.InputMode.PRE_BORDER_SIZE,
                    ChatColor.YELLOW + "Enter pre-start border size in blocks:");
            case 12 -> prompt(player, PlayerMenuState.InputMode.FINAL_BORDER_SIZE,
                    ChatColor.YELLOW + "Enter final border size in blocks:");
            case 14 -> prompt(player, PlayerMenuState.InputMode.BORDER_TRANSITION,
                    ChatColor.YELLOW + "Enter border transition duration in seconds:");
            case 16 -> {
                String cur  = plugin.getConfigManager().getBorderCenterMode();
                String next = "fixed".equalsIgnoreCase(cur) ? "spawn" : "fixed";
                plugin.getConfigManager().setBorderCenterMode(next);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Border center set to " + next + ".");
                openBorderMenu(player);
            }
            case 21 -> prompt(player, PlayerMenuState.InputMode.BORDER_CENTER_X,
                    ChatColor.YELLOW + "Enter border center X coordinate:");
            case 35 -> openMainMenu(player);
        }
    }

    // ── PvP ───────────────────────────────────────────────────────────────────

    private void handlePvp(Player player, int slot) {
        switch (slot) {
            case 13 -> prompt(player, PlayerMenuState.InputMode.PVP_DURATION,
                    ChatColor.YELLOW + "Enter PvP protection duration in minutes (0 = off):");
            case 16 -> openMainMenu(player);
        }
    }

    // ── Reminders ─────────────────────────────────────────────────────────────

    private void handleReminders(Player player, int slot) {
        switch (slot) {
            case 11 -> {
                boolean cur = plugin.getConfigManager().areJoinRemindersEnabled();
                plugin.getConfigManager().setJoinRemindersEnabled(!cur);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Join reminders " + (!cur ? "enabled" : "disabled") + ".");
                openRemindersMenu(player);
            }
            case 13 -> prompt(player, PlayerMenuState.InputMode.REMINDER_INTERVAL,
                    ChatColor.YELLOW + "Enter reminder interval in seconds:");
            case 16 -> openMainMenu(player);
        }
    }

    // ── Phases ────────────────────────────────────────────────────────────────

    private void handlePhases(Player player, int slot) {
        switch (slot) {
            // Pre-start difficulty
            case 11 -> {
                String next = cycleDiff(plugin.getConfigManager().getStartingDifficulty());
                plugin.getConfigManager().setPhaseDifficulties(next, plugin.getConfigManager().getStartedDifficulty());
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Pre-start difficulty → " + next + ".");
                openPhasesMenu(player);
            }
            // Pre-start mob spawning
            case 12 -> {
                boolean cur = plugin.getConfigManager().isStartingDisableMobSpawning();
                plugin.getConfigManager().setPhaseMobProtection(!cur,
                        plugin.getConfigManager().isStartedDisableMobSpawning(),
                        plugin.getConfigManager().isStartingDisableMobDamage(),
                        plugin.getConfigManager().isStartedDisableMobDamage());
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Pre-start mob spawning " + (!cur ? "disabled" : "enabled") + ".");
                openPhasesMenu(player);
            }
            // Pre-start mob damage
            case 13 -> {
                boolean cur = plugin.getConfigManager().isStartingDisableMobDamage();
                plugin.getConfigManager().setPhaseMobProtection(
                        plugin.getConfigManager().isStartingDisableMobSpawning(),
                        plugin.getConfigManager().isStartedDisableMobSpawning(),
                        !cur,
                        plugin.getConfigManager().isStartedDisableMobDamage());
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Pre-start mob damage " + (!cur ? "disabled" : "enabled") + ".");
                openPhasesMenu(player);
            }
            // Started difficulty
            case 22 -> {
                String next = cycleDiff(plugin.getConfigManager().getStartedDifficulty());
                plugin.getConfigManager().setPhaseDifficulties(plugin.getConfigManager().getStartingDifficulty(), next);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Started difficulty → " + next + ".");
                openPhasesMenu(player);
            }
            // Started mob spawning
            case 23 -> {
                boolean cur = plugin.getConfigManager().isStartedDisableMobSpawning();
                plugin.getConfigManager().setPhaseMobProtection(
                        plugin.getConfigManager().isStartingDisableMobSpawning(),
                        !cur,
                        plugin.getConfigManager().isStartingDisableMobDamage(),
                        plugin.getConfigManager().isStartedDisableMobDamage());
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Started mob spawning " + (!cur ? "disabled" : "enabled") + ".");
                openPhasesMenu(player);
            }
            // Started mob damage
            case 24 -> {
                boolean cur = plugin.getConfigManager().isStartedDisableMobDamage();
                plugin.getConfigManager().setPhaseMobProtection(
                        plugin.getConfigManager().isStartingDisableMobSpawning(),
                        plugin.getConfigManager().isStartedDisableMobSpawning(),
                        plugin.getConfigManager().isStartingDisableMobDamage(),
                        !cur);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Started mob damage " + (!cur ? "disabled" : "enabled") + ".");
                openPhasesMenu(player);
            }
            // Min players
            case 31 -> prompt(player, PlayerMenuState.InputMode.MIN_PLAYERS,
                    ChatColor.YELLOW + "Enter minimum players required to start (0 = off):");
            // Back
            case 44 -> openMainMenu(player);
        }
    }

    // =========================================================================
    //  CHAT INPUT
    // =========================================================================

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerMenuState ms = menuStates.get(player.getUniqueId());
        if (ms == null || !ms.isAwaitingInput()) return;

        event.setCancelled(true);
        String raw  = event.getMessage().trim();
        PlayerMenuState.InputMode mode = ms.getInputMode();
        ms.clearInputMode();

        Bukkit.getScheduler().runTask(plugin, () -> processChatInput(player, mode, raw));
    }

    private void processChatInput(Player player, PlayerMenuState.InputMode mode, String raw) {
        if (raw.equalsIgnoreCase("cancel")) {
            player.sendMessage(ChatColor.GRAY + "Cancelled.");
            reopen(player);
            return;
        }

        switch (mode) {
            case COUNTDOWN_DURATION -> {
                Integer v = parseInt(player, raw, 1); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setCountdownDuration(v);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Countdown duration set to " + v + "s.");
                openCountdownMenu(player);
            }
            case COOLDOWN_DURATION -> {
                Integer v = parseInt(player, raw, 1); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setCooldownDuration(v);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Cooldown duration set to " + v + "s.");
                openCountdownMenu(player);
            }
            case PRE_BORDER_SIZE -> {
                Double v = parseDouble(player, raw, 1); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setPreStartBorderSize(v);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Pre-start border set to " + fmt(v) + " blocks.");
                openBorderMenu(player);
            }
            case FINAL_BORDER_SIZE -> {
                Double v = parseDouble(player, raw, 1); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setFinalBorderSize(v);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Final border set to " + fmt(v) + " blocks.");
                openBorderMenu(player);
            }
            case BORDER_TRANSITION -> {
                Integer v = parseInt(player, raw, 0); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setBorderTransitionSeconds(v);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Border transition set to " + v + "s.");
                openBorderMenu(player);
            }
            case BORDER_CENTER_X -> {
                Double v = parseAnyDouble(player, raw); if (v == null) { reopen(player); return; }
                pendingBorderX.put(player.getUniqueId(), v);
                prompt(player, PlayerMenuState.InputMode.BORDER_CENTER_Z,
                        ChatColor.YELLOW + "Now enter the Z coordinate:");
            }
            case BORDER_CENTER_Z -> {
                Double v = parseAnyDouble(player, raw); if (v == null) { reopen(player); return; }
                double x = pendingBorderX.getOrDefault(player.getUniqueId(), plugin.getConfigManager().getBorderCenterX());
                pendingBorderX.remove(player.getUniqueId());
                plugin.getConfigManager().setBorderCenterPosition(x, v);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Border center set to X=" + x + " Z=" + v + ".");
                openBorderMenu(player);
            }
            case PVP_DURATION -> {
                Integer v = parseInt(player, raw, 0); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setPvpProtectionDuration(v);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "PvP protection set to " + v + " min.");
                openPvpMenu(player);
            }
            case MIN_PLAYERS -> {
                Integer v = parseInt(player, raw, 0); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setMinOnlinePlayers(v);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Min players set to " + v + ".");
                openPhasesMenu(player);
            }
            case REMINDER_INTERVAL -> {
                Integer v = parseInt(player, raw, 1); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setReminderInterval(v);
                plugin.getConfigManager().saveConfig();
                player.sendMessage(ChatColor.GREEN + "Reminder interval set to " + v + "s.");
                openRemindersMenu(player);
            }
            default -> reopen(player);
        }
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    private void prompt(Player player, PlayerMenuState.InputMode mode, String msg) {
        player.closeInventory();
        menuStates.computeIfAbsent(player.getUniqueId(), PlayerMenuState::new).setInputMode(mode);
        player.sendMessage(ChatColor.DARK_GRAY + "                    ");
        player.sendMessage(msg);
        player.sendMessage(ChatColor.DARK_GRAY + "Type " + ChatColor.GRAY + "cancel" + ChatColor.DARK_GRAY + " to go back.");
        player.sendMessage(ChatColor.DARK_GRAY + "                    ");
    }

    private void reopen(Player player) {
        String t = openMenus.get(player.getUniqueId());
        if      (t == null)                  openMainMenu(player);
        else if (t.equals(TITLE_COUNTDOWN))  openCountdownMenu(player);
        else if (t.equals(TITLE_BORDER))     openBorderMenu(player);
        else if (t.equals(TITLE_PVP))        openPvpMenu(player);
        else if (t.equals(TITLE_REMINDERS))  openRemindersMenu(player);
        else if (t.equals(TITLE_PHASES))     openPhasesMenu(player);
        else                                 openMainMenu(player);
    }

    /** Glass pane with display name and optional lore. */
    private ItemStack pane(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta  meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(name);
        if (lore.length > 0) meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    /** Fill specific slots with a plain (unnamed) pane. */
    private void fill(Inventory inv, Material mat, int... slots) {
        ItemStack p = pane(mat, " ");
        for (int s : slots) inv.setItem(s, p);
    }

    private boolean isOurMenu(String t) {
        return t.equals(TITLE_MAIN) || t.equals(TITLE_COUNTDOWN) || t.equals(TITLE_BORDER)
                || t.equals(TITLE_PVP) || t.equals(TITLE_REMINDERS) || t.equals(TITLE_PHASES);
    }

    private boolean isSmpStarted() {
        if (plugin.getStateManager() instanceof StateManagerImpl impl)
            return impl.getStateData().isSmpStarted();
        return false;
    }

    private String cycleDiff(String cur) {
        return switch (cur == null ? "" : cur.toLowerCase()) {
            case "peaceful" -> "easy";
            case "easy"     -> "normal";
            case "normal"   -> "hard";
            default         -> "peaceful";
        };
    }

    /** Format a double — show as int if it has no fractional part. */
    private String fmt(double v) {
        return v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
    }

    private Integer parseInt(Player p, String s, int min) {
        try {
            int v = Integer.parseInt(s);
            if (v < min) { p.sendMessage(ChatColor.RED + "Must be at least " + min + "."); return null; }
            return v;
        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.RED + "Invalid number: " + s);
            return null;
        }
    }

    private Double parseDouble(Player p, String s, double min) {
        try {
            double v = Double.parseDouble(s);
            if (v < min) { p.sendMessage(ChatColor.RED + "Must be at least " + min + "."); return null; }
            return v;
        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.RED + "Invalid number: " + s);
            return null;
        }
    }

    private Double parseAnyDouble(Player p, String s) {
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { p.sendMessage(ChatColor.RED + "Invalid number: " + s); return null; }
    }
}
