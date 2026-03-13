package com.muzlik.smpstart.config;

import com.muzlik.smpstart.SMPStartPlugin;
import com.muzlik.smpstart.data.PluginConfig;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Implementation of ConfigManager that handles plugin configuration
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
        FileConfiguration fileConfig = plugin.getConfig();
        
        // Load values from config file, using defaults if not present
        config.setCountdownDuration(fileConfig.getInt("countdown-duration", 10));
        config.setCooldownDuration(fileConfig.getInt("cooldown-duration", 10));
        config.setPreStartBorderSize(fileConfig.getDouble("pre-start-border-size", 10.0));
        config.setFinalBorderSize(fileConfig.getDouble("final-border-size", 10000.0));
        config.setJoinRemindersEnabled(fileConfig.getBoolean("join-reminders-enabled", true));
        config.setReminderInterval(fileConfig.getInt("reminder-interval", 30));
        config.setPvpProtectionDuration(fileConfig.getInt("pvp-protection-duration", 30));
    }
    
    @Override
    public int getCountdownDuration() {
        return config.getCountdownDuration();
    }
    
    @Override
    public void setCountdownDuration(int duration) {
        if (duration > 0) {
            config.setCountdownDuration(duration);
            plugin.getConfig().set("countdown-duration", duration);
        }
    }
    
    @Override
    public int getCooldownDuration() {
        return config.getCooldownDuration();
    }
    
    @Override
    public void setCooldownDuration(int duration) {
        if (duration > 0) {
            config.setCooldownDuration(duration);
            plugin.getConfig().set("cooldown-duration", duration);
        }
    }
    
    @Override
    public double getPreStartBorderSize() {
        return config.getPreStartBorderSize();
    }
    
    @Override
    public void setPreStartBorderSize(double size) {
        if (isValidBorderSize(size)) {
            config.setPreStartBorderSize(size);
            plugin.getConfig().set("pre-start-border-size", size);
        }
    }
    
    @Override
    public double getFinalBorderSize() {
        return config.getFinalBorderSize();
    }
    
    @Override
    public void setFinalBorderSize(double size) {
        if (isValidBorderSize(size)) {
            config.setFinalBorderSize(size);
            plugin.getConfig().set("final-border-size", size);
        }
    }
    
    @Override
    public boolean areJoinRemindersEnabled() {
        return config.isJoinRemindersEnabled();
    }
    
    @Override
    public void setJoinRemindersEnabled(boolean enabled) {
        config.setJoinRemindersEnabled(enabled);
        plugin.getConfig().set("join-reminders-enabled", enabled);
    }
    
    @Override
    public int getReminderInterval() {
        return config.getReminderInterval();
    }
    
    @Override
    public void setReminderInterval(int interval) {
        if (interval > 0) {
            config.setReminderInterval(interval);
            plugin.getConfig().set("reminder-interval", interval);
        }
    }
    
    @Override
    public void saveConfig() {
        plugin.saveConfig();
    }
    
    @Override
    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }
    
    @Override
    public boolean isValidBorderSize(double size) {
        return size > 0;
    }
    
    public int getPvpProtectionDuration() {
        return config.getPvpProtectionDuration();
    }
    
    public void setPvpProtectionDuration(int duration) {
        if (duration >= 0) {
            config.setPvpProtectionDuration(duration);
            plugin.getConfig().set("pvp-protection-duration", duration);
        }
    }
}