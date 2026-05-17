package com.muzlik.smpstart.config;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.data.PluginConfig;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Implementation of ConfigManager that handles plugin configuration.
 */
public class ConfigManagerImpl implements ConfigManager {

    private final SMPStartPlugin plugin;
    private final PluginConfig config;

    public ConfigManagerImpl(SMPStartPlugin plugin) {
        this.plugin = plugin;
        this.config = new PluginConfig();
        loadConfig();
    }

    private void loadConfig() {
        plugin.saveDefaultConfig();
        FileConfiguration c = plugin.getConfig();

        // general
        config.setWorldName(c.getString("general.world", ""));
        config.setMinOnlinePlayers(c.getInt("general.min-players", 1));

        // countdown
        config.setCountdownDuration(c.getInt("countdown.duration", 10));
        config.setCooldownDuration(c.getInt("countdown.cooldown", 60));
        config.setCountdownBossBarEnabled(c.getBoolean("countdown.bossbar", true));

        // border
        config.setPreStartBorderSize(c.getDouble("border.pre-start-size", 10.0));
        config.setFinalBorderSize(c.getDouble("border.final-size", 10000.0));
        config.setBorderTransitionSeconds(c.getInt("border.transition-seconds", 10));
        config.setBorderCenterMode(normalizeBorderCenterMode(c.getString("border.center-mode", "spawn")));
        config.setBorderCenterX(c.getDouble("border.center-x", 0.0));
        config.setBorderCenterZ(c.getDouble("border.center-z", 0.0));
        config.setPreStartBorderDamageAmount(c.getDouble("border.pre-start-damage.amount", 0.0));
        config.setPreStartBorderDamageBuffer(c.getDouble("border.pre-start-damage.buffer", 0.0));
        config.setBorderDamageAmount(c.getDouble("border.post-start-damage.amount", 0.2));
        config.setBorderDamageBuffer(c.getDouble("border.post-start-damage.buffer", 5.0));

        // pvp
        config.setPvpProtectionDuration(c.getInt("pvp.protection-duration", 30));

        // reminders
        config.setJoinRemindersEnabled(c.getBoolean("reminders.enabled", true));
        config.setReminderInterval(c.getInt("reminders.interval", 60));

        // phases
        config.setStartingDifficulty(c.getString("phases.pre-start.difficulty", "peaceful"));
        config.setStartingDisableMobSpawning(c.getBoolean("phases.pre-start.disable-mob-spawning", true));
        config.setStartingDisableMobDamage(c.getBoolean("phases.pre-start.disable-mob-damage", true));
        config.setStartedDifficulty(c.getString("phases.started.difficulty", "normal"));
        config.setStartedDisableMobSpawning(c.getBoolean("phases.started.disable-mob-spawning", false));
        config.setStartedDisableMobDamage(c.getBoolean("phases.started.disable-mob-damage", false));
    }

    // -------------------------------------------------------------------------
    // Setters — also write back to the Bukkit config object so saveConfig works
    // -------------------------------------------------------------------------

    @Override public int getCountdownDuration() { return config.getCountdownDuration(); }
    @Override public void setCountdownDuration(int duration) {
        if (duration > 0) { config.setCountdownDuration(duration); plugin.getConfig().set("countdown.duration", duration); }
    }

    @Override public int getCooldownDuration() { return config.getCooldownDuration(); }
    @Override public void setCooldownDuration(int duration) {
        if (duration > 0) { config.setCooldownDuration(duration); plugin.getConfig().set("countdown.cooldown", duration); }
    }

    @Override public double getPreStartBorderSize() { return config.getPreStartBorderSize(); }
    @Override public void setPreStartBorderSize(double size) {
        if (isValidBorderSize(size)) { config.setPreStartBorderSize(size); plugin.getConfig().set("border.pre-start-size", size); }
    }

    @Override public double getFinalBorderSize() { return config.getFinalBorderSize(); }
    @Override public void setFinalBorderSize(double size) {
        if (isValidBorderSize(size)) { config.setFinalBorderSize(size); plugin.getConfig().set("border.final-size", size); }
    }

    @Override public boolean areJoinRemindersEnabled() { return config.isJoinRemindersEnabled(); }
    @Override public void setJoinRemindersEnabled(boolean enabled) {
        config.setJoinRemindersEnabled(enabled); plugin.getConfig().set("reminders.enabled", enabled);
    }

    @Override public int getReminderInterval() { return config.getReminderInterval(); }
    @Override public void setReminderInterval(int interval) {
        if (interval > 0) { config.setReminderInterval(interval); plugin.getConfig().set("reminders.interval", interval); }
    }

    @Override public String getWorldName() { return config.getWorldName(); }
    @Override public void setWorldName(String worldName) {
        String value = worldName != null ? worldName.trim() : "";
        config.setWorldName(value); plugin.getConfig().set("general.world", value);
    }

    @Override public int getMinOnlinePlayers() { return config.getMinOnlinePlayers(); }
    @Override public void setMinOnlinePlayers(int minPlayers) {
        if (minPlayers >= 0) { config.setMinOnlinePlayers(minPlayers); plugin.getConfig().set("general.min-players", minPlayers); }
    }

    @Override public boolean isCountdownBossBarEnabled() { return config.isCountdownBossBarEnabled(); }
    @Override public void setCountdownBossBarEnabled(boolean enabled) {
        config.setCountdownBossBarEnabled(enabled); plugin.getConfig().set("countdown.bossbar", enabled);
    }

    @Override public String getBorderCenterMode() { return config.getBorderCenterMode(); }
    @Override public void setBorderCenterMode(String mode) {
        String normalized = normalizeBorderCenterMode(mode);
        config.setBorderCenterMode(normalized); plugin.getConfig().set("border.center-mode", normalized);
    }

    @Override public double getBorderCenterX() { return config.getBorderCenterX(); }
    @Override public double getBorderCenterZ() { return config.getBorderCenterZ(); }
    @Override public void setBorderCenterPosition(double x, double z) {
        config.setBorderCenterX(x); config.setBorderCenterZ(z);
        plugin.getConfig().set("border.center-x", x); plugin.getConfig().set("border.center-z", z);
    }

    @Override public int getBorderTransitionSeconds() { return config.getBorderTransitionSeconds(); }
    @Override public void setBorderTransitionSeconds(int seconds) {
        if (seconds >= 0) { config.setBorderTransitionSeconds(seconds); plugin.getConfig().set("border.transition-seconds", seconds); }
    }

    @Override public double getPreStartBorderDamageAmount() { return config.getPreStartBorderDamageAmount(); }
    @Override public double getPreStartBorderDamageBuffer() { return config.getPreStartBorderDamageBuffer(); }
    @Override public double getBorderDamageAmount() { return config.getBorderDamageAmount(); }
    @Override public double getBorderDamageBuffer() { return config.getBorderDamageBuffer(); }
    @Override public void setBorderDamageValues(double preStartAmount, double preStartBuffer, double amount, double buffer) {
        config.setPreStartBorderDamageAmount(preStartAmount);
        config.setPreStartBorderDamageBuffer(preStartBuffer);
        config.setBorderDamageAmount(amount);
        config.setBorderDamageBuffer(buffer);
        plugin.getConfig().set("border.pre-start-damage.amount", preStartAmount);
        plugin.getConfig().set("border.pre-start-damage.buffer", preStartBuffer);
        plugin.getConfig().set("border.post-start-damage.amount", amount);
        plugin.getConfig().set("border.post-start-damage.buffer", buffer);
    }

    @Override public String getStartingDifficulty() { return config.getStartingDifficulty(); }
    @Override public String getStartedDifficulty() { return config.getStartedDifficulty(); }
    @Override public void setPhaseDifficulties(String startingDifficulty, String startedDifficulty) {
        config.setStartingDifficulty(startingDifficulty); config.setStartedDifficulty(startedDifficulty);
        plugin.getConfig().set("phases.pre-start.difficulty", startingDifficulty);
        plugin.getConfig().set("phases.started.difficulty", startedDifficulty);
    }

    @Override public boolean isStartingDisableMobSpawning() { return config.isStartingDisableMobSpawning(); }
    @Override public boolean isStartedDisableMobSpawning() { return config.isStartedDisableMobSpawning(); }
    @Override public boolean isStartingDisableMobDamage() { return config.isStartingDisableMobDamage(); }
    @Override public boolean isStartedDisableMobDamage() { return config.isStartedDisableMobDamage(); }
    @Override public void setPhaseMobProtection(boolean startingSpawnDisable, boolean startedSpawnDisable,
                                                boolean startingDamageDisable, boolean startedDamageDisable) {
        config.setStartingDisableMobSpawning(startingSpawnDisable);
        config.setStartedDisableMobSpawning(startedSpawnDisable);
        config.setStartingDisableMobDamage(startingDamageDisable);
        config.setStartedDisableMobDamage(startedDamageDisable);
        plugin.getConfig().set("phases.pre-start.disable-mob-spawning", startingSpawnDisable);
        plugin.getConfig().set("phases.started.disable-mob-spawning", startedSpawnDisable);
        plugin.getConfig().set("phases.pre-start.disable-mob-damage", startingDamageDisable);
        plugin.getConfig().set("phases.started.disable-mob-damage", startedDamageDisable);
    }

    @Override public int getPvpProtectionDuration() { return config.getPvpProtectionDuration(); }
    @Override public void setPvpProtectionDuration(int duration) {
        if (duration >= 0) { config.setPvpProtectionDuration(duration); plugin.getConfig().set("pvp.protection-duration", duration); }
    }

    @Override public void saveConfig() { plugin.saveConfig(); }
    @Override public void reloadConfig() { plugin.reloadConfig(); loadConfig(); }
    @Override public boolean isValidBorderSize(double size) { return size > 0; }

    private String normalizeBorderCenterMode(String mode) {
        if (mode == null) return "spawn";
        return mode.trim().equalsIgnoreCase("fixed") ? "fixed" : "spawn";
    }
}
