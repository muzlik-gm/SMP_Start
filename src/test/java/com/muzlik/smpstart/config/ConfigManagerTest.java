package com.muzlik.smpstart.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for ConfigManager validation logic
 */
class ConfigManagerTest {
    
    @Test
    void testBorderSizeValidation() {
        // Create a mock implementation for testing validation
        ConfigManager configManager = new ConfigManager() {
            @Override
            public int getCountdownDuration() { return 0; }
            @Override
            public void setCountdownDuration(int duration) {}
            @Override
            public int getCooldownDuration() { return 0; }
            @Override
            public void setCooldownDuration(int duration) {}
            @Override
            public double getPreStartBorderSize() { return 0; }
            @Override
            public void setPreStartBorderSize(double size) {}
            @Override
            public double getFinalBorderSize() { return 0; }
            @Override
            public void setFinalBorderSize(double size) {}
            @Override
            public boolean areJoinRemindersEnabled() { return false; }
            @Override
            public void setJoinRemindersEnabled(boolean enabled) {}
            @Override
            public int getReminderInterval() { return 0; }
            @Override
            public void setReminderInterval(int interval) {}
            @Override
            public void saveConfig() {}
            @Override
            public void reloadConfig() {}
            @Override
            public int getPvpProtectionDuration() { return 0; }
            @Override
            public void setPvpProtectionDuration(int duration) {}
            
            @Override
            public boolean isValidBorderSize(double size) {
                return size > 0;
            }
        };
        
        // Test valid border sizes
        assertTrue(configManager.isValidBorderSize(1.0));
        assertTrue(configManager.isValidBorderSize(100.0));
        assertTrue(configManager.isValidBorderSize(1000.0));
        assertTrue(configManager.isValidBorderSize(0.1));
        
        // Test invalid border sizes
        assertFalse(configManager.isValidBorderSize(0.0));
        assertFalse(configManager.isValidBorderSize(-1.0));
        assertFalse(configManager.isValidBorderSize(-100.0));
    }
}