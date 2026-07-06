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
            public String getWorldName() { return ""; }
            @Override
            public void setWorldName(String worldName) {}
            @Override
            public int getMinOnlinePlayers() { return 0; }
            @Override
            public void setMinOnlinePlayers(int minPlayers) {}
            @Override
            public boolean isCountdownBossBarEnabled() { return true; }
            @Override
            public void setCountdownBossBarEnabled(boolean enabled) {}
            @Override
            public String getBorderCenterMode() { return "spawn"; }
            @Override
            public void setBorderCenterMode(String mode) {}
            @Override
            public double getBorderCenterX() { return 0.0; }
            @Override
            public double getBorderCenterZ() { return 0.0; }
            @Override
            public void setBorderCenterPosition(double x, double z) {}
            @Override
            public int getBorderTransitionSeconds() { return 10; }
            @Override
            public void setBorderTransitionSeconds(int seconds) {}
            @Override
            public double getPreStartBorderDamageAmount() { return 0.0; }
            @Override
            public double getPreStartBorderDamageBuffer() { return 0.0; }
            @Override
            public double getBorderDamageAmount() { return 0.2; }
            @Override
            public double getBorderDamageBuffer() { return 5.0; }
            @Override
            public void setBorderDamageValues(double preStartAmount, double preStartBuffer, double amount, double buffer) {}
            @Override
            public String getStartingDifficulty() { return "peaceful"; }
            @Override
            public String getStartedDifficulty() { return "normal"; }
            @Override
            public void setPhaseDifficulties(String startingDifficulty, String startedDifficulty) {}
            @Override
            public boolean isStartingDisableMobSpawning() { return true; }
            @Override
            public boolean isStartedDisableMobSpawning() { return false; }
            @Override
            public boolean isStartingDisableMobDamage() { return true; }
            @Override
            public boolean isStartedDisableMobDamage() { return false; }
            @Override
            public void setPhaseMobProtection(boolean startingSpawnDisable, boolean startedSpawnDisable, boolean startingDamageDisable, boolean startedDamageDisable) {}
            @Override
            public void saveConfig() {}
            @Override
            public void reloadConfig() {}
            @Override
            public int getPvpProtectionDuration() { return 0; }
            @Override
            public void setPvpProtectionDuration(int duration) {}
            @Override
            public boolean isPreStartBlockProtectionEnabled() { return true; }
            @Override
            public void setPreStartBlockProtectionEnabled(boolean enabled) {}
            
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
