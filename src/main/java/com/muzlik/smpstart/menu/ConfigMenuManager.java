package com.muzlik.smpstart.menu;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.state.StateManager.PluginState;
import com.muzlik.smpstart.state.StateManagerImpl;
import com.muzlik.smpstart.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
 * Modern GUI for SMP Starter.
 * Design: Integrated HEX colors, sound effects, and cleaner layouts.
 */
public class ConfigMenuManager implements Listener {

    private final SMPStartPlugin plugin;

    private static final String TITLE_MAIN      = MessageUtils.color("&#00FBFF&lSMP STARTER");
    private static final String TITLE_COUNTDOWN = MessageUtils.color("&#00FBFF&lCOUNTDOWN SETTINGS");
    private static final String TITLE_BORDER    = MessageUtils.color("&#00FBFF&lBORDER SETTINGS");
    private static final String TITLE_PVP       = MessageUtils.color("&#00FBFF&lPVP SETTINGS");
    private static final String TITLE_REMINDERS = MessageUtils.color("&#00FBFF&lREMINDER SETTINGS");
    private static final String TITLE_PHASES    = MessageUtils.color("&#00FBFF&lPHASE SETTINGS");

    private final Map<UUID, PlayerMenuState> menuStates = new HashMap<>();
    private final Map<UUID, String> openMenus = new HashMap<>();
    private final Map<UUID, Double> pendingBorderX = new HashMap<>();

    public ConfigMenuManager(SMPStartPlugin plugin) {
        this.plugin = plugin;
    }

    // =========================================================================
    //  OPENERS
    // =========================================================================

    public void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE_MAIN);
        fill(inv, Material.BLACK_STAINED_GLASS_PANE, 0,1,2,3,4,5,6,7,8, 9,17, 18,26, 27,35, 36,37,38,39,40,41,42,43,44);

        boolean smpStarted = isSmpStarted();
        PluginState state = plugin.getStateManager().getCurrentState();

        // Status Item
        ItemStack status = pane(Material.BEACON, MessageUtils.color("&#00FBFF&lCURRENT STATUS"),
                MessageUtils.color("&8&m━━━━━━━━━━━━━━━━━━━━"),
                MessageUtils.color("&7Status: &f" + state.name()),
                MessageUtils.color("&7SMP Started: " + (smpStarted ? "&aYes" : "&cNo")),
                MessageUtils.color("&8&m━━━━━━━━━━━━━━━━━━━━"));
        inv.setItem(4, status);

        // Control Buttons
        inv.setItem(11, pane(Material.CLOCK, MessageUtils.color("&#00FBFF&lCOUNTDOWN"), MessageUtils.color("&7Duration, Cooldown, Boss Bar")));
        inv.setItem(13, pane(Material.BARRIER, MessageUtils.color("&#00FBFF&lWORLD BORDER"), MessageUtils.color("&7Sizes, Transition, Center")));
        inv.setItem(15, pane(Material.DIAMOND_SWORD, MessageUtils.color("&#00FBFF&lPVP & SAFETY"), MessageUtils.color("&7PvP Protection, Blocks")));
        inv.setItem(29, pane(Material.BELL, MessageUtils.color("&#00FBFF&lREMINDERS"), MessageUtils.color("&7Join alerts for Admins")));
        inv.setItem(31, pane(Material.GRASS_BLOCK, MessageUtils.color("&#00FBFF&lPHASES"), MessageUtils.color("&7Difficulty, Mobs, Players")));

        // Action Buttons
        if (state == PluginState.IDLE && !smpStarted) {
            inv.setItem(22, pane(Material.LIME_CONCRETE, MessageUtils.color("&a&lSTART SMP"), MessageUtils.color("&7Begins the launch sequence")));
        } else if (state == PluginState.COUNTDOWN) {
            inv.setItem(22, pane(Material.RED_CONCRETE, MessageUtils.color("&c&lCANCEL START"), MessageUtils.color("&7Stops the countdown")));
        } else {
            inv.setItem(22, pane(Material.TNT, MessageUtils.color("&c&lRESET SMP"), MessageUtils.color("&7Returns everything to pre-start")));
        }

        player.openInventory(inv);
        openMenus.put(player.getUniqueId(), TITLE_MAIN);
    }

    public void openCountdownMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE_COUNTDOWN);
        fill(inv, Material.BLACK_STAINED_GLASS_PANE, 0,1,2,3,4,5,6,7,8, 9,17, 18,26, 27,35, 36,37,38,39,40,41,42,43,44);

        int dur = plugin.getConfigManager().getCountdownDuration();
        int cool = plugin.getConfigManager().getCooldownDuration();
        boolean bb = plugin.getConfigManager().isCountdownBossBarEnabled();

        inv.setItem(20, pane(Material.CLOCK, MessageUtils.color("&#00FBFF&lDURATION"), MessageUtils.color("&7Current: &f" + dur + "s"), "", MessageUtils.color("&eClick to change")));
        inv.setItem(22, pane(Material.RECOVERY_COMPASS, MessageUtils.color("&#00FBFF&lCOOLDOWN"), MessageUtils.color("&7Current: &f" + cool + "s"), "", MessageUtils.color("&eClick to change")));
        inv.setItem(24, pane(bb ? Material.LIME_DYE : Material.GRAY_DYE, MessageUtils.color("&#00FBFF&lBOSS BAR"), MessageUtils.color("&7Status: " + (bb ? "&aEnabled" : "&cDisabled")), "", MessageUtils.color("&eClick to toggle")));

        inv.setItem(40, pane(Material.ARROW, MessageUtils.color("&c&lBACK")));
        player.openInventory(inv);
        openMenus.put(player.getUniqueId(), TITLE_COUNTDOWN);
    }

    public void openBorderMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE_BORDER);
        fill(inv, Material.BLACK_STAINED_GLASS_PANE, 0,1,2,3,4,5,6,7,8, 9,17, 18,26, 27,35, 36,37,38,39,40,41,42,43,44);

        double pre = plugin.getConfigManager().getPreStartBorderSize();
        double fin = plugin.getConfigManager().getFinalBorderSize();
        int trans = plugin.getConfigManager().getBorderTransitionSeconds();
        String mode = plugin.getConfigManager().getBorderCenterMode();
        double cx = plugin.getConfigManager().getBorderCenterX();
        double cz = plugin.getConfigManager().getBorderCenterZ();

        inv.setItem(11, pane(Material.IRON_BARS, MessageUtils.color("&#00FBFF&lPRE-START SIZE"), MessageUtils.color("&7Current: &f" + fmt(pre) + " blocks"), "", MessageUtils.color("&eClick to change")));
        inv.setItem(13, pane(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, MessageUtils.color("&#00FBFF&lFINAL SIZE"), MessageUtils.color("&7Current: &f" + fmt(fin) + " blocks"), "", MessageUtils.color("&eClick to change")));
        inv.setItem(15, pane(Material.REPEATER, MessageUtils.color("&#00FBFF&lTRANSITION"), MessageUtils.color("&7Current: &f" + trans + "s"), "", MessageUtils.color("&eClick to change")));

        inv.setItem(29, pane(Material.COMPASS, MessageUtils.color("&#00FBFF&lCENTER MODE"), MessageUtils.color("&7Current: &f" + mode), "", MessageUtils.color("&eClick to cycle")));
        inv.setItem(31, pane(Material.MAP, MessageUtils.color("&#00FBFF&lFIXED POSITION"), MessageUtils.color("&7X: &f" + fmt(cx) + " &7Z: &f" + fmt(cz)), "", MessageUtils.color("&eClick to set")));

        inv.setItem(40, pane(Material.ARROW, MessageUtils.color("&c&lBACK")));
        player.openInventory(inv);
        openMenus.put(player.getUniqueId(), TITLE_BORDER);
    }

    public void openPvpMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE_PVP);
        fill(inv, Material.BLACK_STAINED_GLASS_PANE, 0,1,2,3,4,5,6,7,8, 9,17, 18,26, 27,35, 36,37,38,39,40,41,42,43,44);

        int pvp = plugin.getConfigManager().getPvpProtectionDuration();
        boolean blocks = plugin.getConfigManager().isPreStartBlockProtectionEnabled();

        inv.setItem(21, pane(Material.SHIELD, MessageUtils.color("&#00FBFF&lPVP PROTECTION"), MessageUtils.color("&7Duration: &f" + pvp + " min"), "", MessageUtils.color("&eClick to change")));
        inv.setItem(23, pane(blocks ? Material.IRON_BLOCK : Material.COBBLESTONE, MessageUtils.color("&#00FBFF&lBLOCK PROTECTION"), MessageUtils.color("&7Status: " + (blocks ? "&aEnabled" : "&cDisabled")), "", MessageUtils.color("&eClick to toggle")));

        inv.setItem(40, pane(Material.ARROW, MessageUtils.color("&c&lBACK")));
        player.openInventory(inv);
        openMenus.put(player.getUniqueId(), TITLE_PVP);
    }

    public void openRemindersMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE_REMINDERS);
        fill(inv, Material.BLACK_STAINED_GLASS_PANE, 0,1,2,3,4,5,6,7,8, 9,17, 18,26, 27,35, 36,37,38,39,40,41,42,43,44);

        boolean en = plugin.getConfigManager().areJoinRemindersEnabled();
        int iv = plugin.getConfigManager().getReminderInterval();

        inv.setItem(21, pane(en ? Material.LIME_DYE : Material.GRAY_DYE, MessageUtils.color("&#00FBFF&lENABLED"), MessageUtils.color("&7Status: " + (en ? "&aYes" : "&cNo")), "", MessageUtils.color("&eClick to toggle")));
        inv.setItem(23, pane(Material.CLOCK, MessageUtils.color("&#00FBFF&lINTERVAL"), MessageUtils.color("&7Every: &f" + iv + "s"), "", MessageUtils.color("&eClick to change")));

        inv.setItem(40, pane(Material.ARROW, MessageUtils.color("&c&lBACK")));
        player.openInventory(inv);
        openMenus.put(player.getUniqueId(), TITLE_REMINDERS);
    }

    public void openPhasesMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE_PHASES);
        fill(inv, Material.BLACK_STAINED_GLASS_PANE, 0,1,2,3,4,5,6,7,8, 9,17, 18,26, 27,35, 36,37,38,39,40,41,42,43,44);

        String d1 = plugin.getConfigManager().getStartingDifficulty();
        String d2 = plugin.getConfigManager().getStartedDifficulty();
        boolean m1 = plugin.getConfigManager().isStartingDisableMobSpawning();
        boolean m2 = plugin.getConfigManager().isStartedDisableMobSpawning();
        boolean md1 = plugin.getConfigManager().isStartingDisableMobDamage();
        boolean md2 = plugin.getConfigManager().isStartedDisableMobDamage();
        int min = plugin.getConfigManager().getMinOnlinePlayers();

        inv.setItem(10, pane(Material.SUNFLOWER, MessageUtils.color("&#00FBFF&lPRE-START DIFF"), MessageUtils.color("&7Current: &f" + d1), "", MessageUtils.color("&eClick to cycle")));
        inv.setItem(11, pane(m1 ? Material.SPAWNER : Material.ZOMBIE_HEAD, MessageUtils.color("&#00FBFF&lPRE-START MOBS"), MessageUtils.color("&7Spawn: " + (!m1 ? "&aOn" : "&cOff")), "", MessageUtils.color("&eClick to toggle")));
        inv.setItem(12, pane(md1 ? Material.CHAINMAIL_CHESTPLATE : Material.IRON_SWORD, MessageUtils.color("&#00FBFF&lPRE-START DAMAGE"), MessageUtils.color("&7Mob Dmg: " + (!md1 ? "&aOn" : "&cOff")), "", MessageUtils.color("&eClick to toggle")));

        inv.setItem(14, pane(Material.DIAMOND_SWORD, MessageUtils.color("&#00FBFF&lSTARTED DIFF"), MessageUtils.color("&7Current: &f" + d2), "", MessageUtils.color("&eClick to cycle")));
        inv.setItem(15, pane(m2 ? Material.SPAWNER : Material.ZOMBIE_HEAD, MessageUtils.color("&#00FBFF&lSTARTED MOBS"), MessageUtils.color("&7Spawn: " + (!m2 ? "&aOn" : "&cOff")), "", MessageUtils.color("&eClick to toggle")));
        inv.setItem(16, pane(md2 ? Material.CHAINMAIL_CHESTPLATE : Material.IRON_SWORD, MessageUtils.color("&#00FBFF&lSTARTED DAMAGE"), MessageUtils.color("&7Mob Dmg: " + (!md2 ? "&aOn" : "&cOff")), "", MessageUtils.color("&eClick to toggle")));

        inv.setItem(31, pane(Material.PLAYER_HEAD, MessageUtils.color("&#00FBFF&lMIN PLAYERS"), MessageUtils.color("&7Required: &f" + min), "", MessageUtils.color("&eClick to change")));

        inv.setItem(40, pane(Material.ARROW, MessageUtils.color("&c&lBACK")));
        player.openInventory(inv);
        openMenus.put(player.getUniqueId(), TITLE_PHASES);
    }

    // =========================================================================
    //  EVENTS
    // =========================================================================

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        openMenus.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (!isOurMenu(title)) return;

        event.setCancelled(true);
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType().isAir()) return;

        int slot = event.getRawSlot();
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

        if (title.equals(TITLE_MAIN)) handleMainClick(player, slot);
        else if (title.equals(TITLE_COUNTDOWN)) handleCountdownClick(player, slot);
        else if (title.equals(TITLE_BORDER)) handleBorderClick(player, slot);
        else if (title.equals(TITLE_PVP)) handlePvpClick(player, slot);
        else if (title.equals(TITLE_REMINDERS)) handleRemindersClick(player, slot);
        else if (title.equals(TITLE_PHASES)) handlePhasesClick(player, slot);
    }

    private void handleMainClick(Player player, int slot) {
        switch (slot) {
            case 11 -> openCountdownMenu(player);
            case 13 -> openBorderMenu(player);
            case 15 -> openPvpMenu(player);
            case 29 -> openRemindersMenu(player);
            case 31 -> openPhasesMenu(player);
            case 22 -> {
                player.closeInventory();
                PluginState state = plugin.getStateManager().getCurrentState();
                boolean smpStarted = isSmpStarted();
                if (state == PluginState.IDLE && !smpStarted) player.performCommand("smp start");
                else if (state == PluginState.COUNTDOWN) player.performCommand("smp cancel");
                else player.performCommand("smp reset");
            }
        }
    }

    private void handleCountdownClick(Player player, int slot) {
        switch (slot) {
            case 20 -> prompt(player, PlayerMenuState.InputMode.COUNTDOWN_DURATION, MessageUtils.color("&#00FBFF&l» &fEnter countdown duration in seconds:"));
            case 22 -> prompt(player, PlayerMenuState.InputMode.COOLDOWN_DURATION, MessageUtils.color("&#00FBFF&l» &fEnter cooldown duration in seconds:"));
            case 24 -> {
                boolean cur = plugin.getConfigManager().isCountdownBossBarEnabled();
                plugin.getConfigManager().setCountdownBossBarEnabled(!cur);
                plugin.getConfigManager().saveConfig();
                openCountdownMenu(player);
            }
            case 40 -> openMainMenu(player);
        }
    }

    private void handleBorderClick(Player player, int slot) {
        switch (slot) {
            case 11 -> prompt(player, PlayerMenuState.InputMode.PRE_BORDER_SIZE, MessageUtils.color("&#00FBFF&l» &fEnter pre-start border size (blocks):"));
            case 13 -> prompt(player, PlayerMenuState.InputMode.FINAL_BORDER_SIZE, MessageUtils.color("&#00FBFF&l» &fEnter final border size (blocks):"));
            case 15 -> prompt(player, PlayerMenuState.InputMode.BORDER_TRANSITION, MessageUtils.color("&#00FBFF&l» &fEnter expansion time in seconds:"));
            case 29 -> {
                String cur = plugin.getConfigManager().getBorderCenterMode();
                plugin.getConfigManager().setBorderCenterMode(cur.equals("spawn") ? "fixed" : "spawn");
                plugin.getConfigManager().saveConfig();
                openBorderMenu(player);
            }
            case 31 -> prompt(player, PlayerMenuState.InputMode.BORDER_CENTER_X, MessageUtils.color("&#00FBFF&l» &fEnter center X coordinate:"));
            case 40 -> openMainMenu(player);
        }
    }

    private void handlePvpClick(Player player, int slot) {
        switch (slot) {
            case 21 -> prompt(player, PlayerMenuState.InputMode.PVP_DURATION, MessageUtils.color("&#00FBFF&l» &fEnter PvP protection duration (minutes):"));
            case 23 -> {
                boolean cur = plugin.getConfigManager().isPreStartBlockProtectionEnabled();
                plugin.getConfigManager().setPreStartBlockProtectionEnabled(!cur);
                plugin.getConfigManager().saveConfig();
                openPvpMenu(player);
            }
            case 40 -> openMainMenu(player);
        }
    }

    private void handleRemindersClick(Player player, int slot) {
        switch (slot) {
            case 21 -> {
                boolean cur = plugin.getConfigManager().areJoinRemindersEnabled();
                plugin.getConfigManager().setJoinRemindersEnabled(!cur);
                plugin.getConfigManager().saveConfig();
                openRemindersMenu(player);
            }
            case 23 -> prompt(player, PlayerMenuState.InputMode.REMINDER_INTERVAL, MessageUtils.color("&#00FBFF&l» &fEnter reminder interval in seconds:"));
            case 40 -> openMainMenu(player);
        }
    }

    private void handlePhasesClick(Player player, int slot) {
        switch (slot) {
            case 10 -> {
                String cur = plugin.getConfigManager().getStartingDifficulty();
                plugin.getConfigManager().setPhaseDifficulties(cycleDiff(cur), plugin.getConfigManager().getStartedDifficulty());
                plugin.getConfigManager().saveConfig();
                openPhasesMenu(player);
            }
            case 11 -> {
                boolean cur = plugin.getConfigManager().isStartingDisableMobSpawning();
                plugin.getConfigManager().setPhaseMobProtection(!cur, plugin.getConfigManager().isStartedDisableMobSpawning(),
                        plugin.getConfigManager().isStartingDisableMobDamage(), plugin.getConfigManager().isStartedDisableMobDamage());
                plugin.getConfigManager().saveConfig();
                openPhasesMenu(player);
            }
            case 12 -> {
                boolean cur = plugin.getConfigManager().isStartingDisableMobDamage();
                plugin.getConfigManager().setPhaseMobProtection(plugin.getConfigManager().isStartingDisableMobSpawning(),
                        plugin.getConfigManager().isStartedDisableMobSpawning(), !cur, plugin.getConfigManager().isStartedDisableMobDamage());
                plugin.getConfigManager().saveConfig();
                openPhasesMenu(player);
            }
            case 14 -> {
                String cur = plugin.getConfigManager().getStartedDifficulty();
                plugin.getConfigManager().setPhaseDifficulties(plugin.getConfigManager().getStartingDifficulty(), cycleDiff(cur));
                plugin.getConfigManager().saveConfig();
                openPhasesMenu(player);
            }
            case 15 -> {
                boolean cur = plugin.getConfigManager().isStartedDisableMobSpawning();
                plugin.getConfigManager().setPhaseMobProtection(plugin.getConfigManager().isStartingDisableMobSpawning(),
                        !cur, plugin.getConfigManager().isStartingDisableMobDamage(), plugin.getConfigManager().isStartedDisableMobDamage());
                plugin.getConfigManager().saveConfig();
                openPhasesMenu(player);
            }
            case 16 -> {
                boolean cur = plugin.getConfigManager().isStartedDisableMobDamage();
                plugin.getConfigManager().setPhaseMobProtection(plugin.getConfigManager().isStartingDisableMobSpawning(),
                        plugin.getConfigManager().isStartedDisableMobSpawning(), plugin.getConfigManager().isStartingDisableMobDamage(), !cur);
                plugin.getConfigManager().saveConfig();
                openPhasesMenu(player);
            }
            case 31 -> prompt(player, PlayerMenuState.InputMode.MIN_PLAYERS, MessageUtils.color("&#00FBFF&l» &fEnter min players to start (0=off):"));
            case 40 -> openMainMenu(player);
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
            MessageUtils.sendInfo(player, "Cancelled.");
            reopen(player);
            return;
        }

        switch (mode) {
            case COUNTDOWN_DURATION -> {
                Integer v = parseInt(player, raw, 1); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setCountdownDuration(v);
                plugin.getConfigManager().saveConfig();
                MessageUtils.sendSuccess(player, "Countdown duration set to &f" + v + "s&7.");
                openCountdownMenu(player);
            }
            case COOLDOWN_DURATION -> {
                Integer v = parseInt(player, raw, 1); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setCooldownDuration(v);
                plugin.getConfigManager().saveConfig();
                MessageUtils.sendSuccess(player, "Cooldown duration set to &f" + v + "s&7.");
                openCountdownMenu(player);
            }
            case PRE_BORDER_SIZE -> {
                Double v = parseDouble(player, raw, 1); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setPreStartBorderSize(v);
                plugin.getConfigManager().saveConfig();
                MessageUtils.sendSuccess(player, "Pre-start border set to &f" + fmt(v) + " blocks&7.");
                openBorderMenu(player);
            }
            case FINAL_BORDER_SIZE -> {
                Double v = parseDouble(player, raw, 1); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setFinalBorderSize(v);
                plugin.getConfigManager().saveConfig();
                MessageUtils.sendSuccess(player, "Final border set to &f" + fmt(v) + " blocks&7.");
                openBorderMenu(player);
            }
            case BORDER_TRANSITION -> {
                Integer v = parseInt(player, raw, 0); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setBorderTransitionSeconds(v);
                plugin.getConfigManager().saveConfig();
                MessageUtils.sendSuccess(player, "Border transition set to &f" + v + "s&7.");
                openBorderMenu(player);
            }
            case BORDER_CENTER_X -> {
                Double v = parseAnyDouble(player, raw); if (v == null) { reopen(player); return; }
                pendingBorderX.put(player.getUniqueId(), v);
                prompt(player, PlayerMenuState.InputMode.BORDER_CENTER_Z, MessageUtils.color("&#00FBFF&l» &fNow enter the Z coordinate:"));
            }
            case BORDER_CENTER_Z -> {
                Double v = parseAnyDouble(player, raw); if (v == null) { reopen(player); return; }
                double x = pendingBorderX.getOrDefault(player.getUniqueId(), plugin.getConfigManager().getBorderCenterX());
                pendingBorderX.remove(player.getUniqueId());
                plugin.getConfigManager().setBorderCenterPosition(x, v);
                plugin.getConfigManager().saveConfig();
                MessageUtils.sendSuccess(player, "Border center set to &fX=" + x + " Z=" + v + "&7.");
                openBorderMenu(player);
            }
            case PVP_DURATION -> {
                Integer v = parseInt(player, raw, 0); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setPvpProtectionDuration(v);
                plugin.getConfigManager().saveConfig();
                MessageUtils.sendSuccess(player, "PvP protection set to &f" + v + " min&7.");
                openPvpMenu(player);
            }
            case MIN_PLAYERS -> {
                Integer v = parseInt(player, raw, 0); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setMinOnlinePlayers(v);
                plugin.getConfigManager().saveConfig();
                MessageUtils.sendSuccess(player, "Min players set to &f" + v + "&7.");
                openPhasesMenu(player);
            }
            case REMINDER_INTERVAL -> {
                Integer v = parseInt(player, raw, 1); if (v == null) { reopen(player); return; }
                plugin.getConfigManager().setReminderInterval(v);
                plugin.getConfigManager().saveConfig();
                MessageUtils.sendSuccess(player, "Reminder interval set to &f" + v + "s&7.");
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
        player.sendMessage("");
        player.sendMessage(msg);
        player.sendMessage(MessageUtils.color("&7Type &f\"cancel\" &7to go back."));
        player.sendMessage("");
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

    private ItemStack pane(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta  meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(name);
        if (lore.length > 0) {
            List<String> list = new ArrayList<>();
            for (String s : lore) list.add(MessageUtils.color(s));
            meta.setLore(list);
        }
        item.setItemMeta(meta);
        return item;
    }

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

    private String fmt(double v) {
        return v == Math.floor(v) ? String.valueOf((long) v) : String.valueOf(v);
    }

    private Integer parseInt(Player p, String s, int min) {
        try {
            int v = Integer.parseInt(s);
            if (v < min) { MessageUtils.sendError(p, "Must be at least &f" + min + "&7."); return null; }
            return v;
        } catch (NumberFormatException e) {
            MessageUtils.sendError(p, "Invalid number: &f" + s);
            return null;
        }
    }

    private Double parseDouble(Player p, String s, double min) {
        try {
            double v = Double.parseDouble(s);
            if (v < min) { MessageUtils.sendError(p, "Must be at least &f" + min + "&7."); return null; }
            return v;
        } catch (NumberFormatException e) {
            MessageUtils.sendError(p, "Invalid number: &f" + s);
            return null;
        }
    }

    private Double parseAnyDouble(Player p, String s) {
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { MessageUtils.sendError(p, "Invalid number: &f" + s); return null; }
    }
}
