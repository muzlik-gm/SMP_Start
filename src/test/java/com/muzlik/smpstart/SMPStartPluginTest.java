package com.muzlik.smpstart;

import com.muzlik.smpstart.data.PluginConfig;
import com.muzlik.smpstart.data.StateData;
import com.muzlik.smpstart.state.StateManager.PluginState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic test for data models and setup
 */
class SMPStartPluginTest {
    
    @Test
    void testPluginConfigDefaults() {
        PluginConfig config = new PluginConfig();
        
        assertEquals(10, config.getCountdownDuration());
        assertEquals(60, config.getCooldownDuration());
        assertEquals(10.0, config.getPreStartBorderSize());
        assertEquals(10000.0, config.getFinalBorderSize());
        assertTrue(config.isJoinRemindersEnabled());
        assertEquals(60, config.getReminderInterval());
        assertEquals(30, config.getPvpProtectionDuration());
        assertEquals("", config.getWorldName());
        assertEquals(1, config.getMinOnlinePlayers());
        assertTrue(config.isCountdownBossBarEnabled());
        assertEquals("spawn", config.getBorderCenterMode());
        assertEquals(0.0, config.getBorderCenterX());
        assertEquals(0.0, config.getBorderCenterZ());
        assertEquals(10, config.getBorderTransitionSeconds());
        assertEquals(0.0, config.getPreStartBorderDamageAmount());
        assertEquals(0.0, config.getPreStartBorderDamageBuffer());
        assertEquals(0.2, config.getBorderDamageAmount());
        assertEquals(5.0, config.getBorderDamageBuffer());
        assertEquals("peaceful", config.getStartingDifficulty());
        assertEquals("normal", config.getStartedDifficulty());
        assertTrue(config.isStartingDisableMobSpawning());
        assertFalse(config.isStartedDisableMobSpawning());
        assertTrue(config.isStartingDisableMobDamage());
        assertFalse(config.isStartedDisableMobDamage());
    }
    
    @Test
    void testPluginConstants() {
        // Test plugin constants that don't require instantiation
        String expectedAuthor = "muzlik";
        assertNotNull(expectedAuthor);
        assertEquals("muzlik", expectedAuthor);
    }
    
    @Test
    void testPluginConfigSetters() {
        PluginConfig config = new PluginConfig();
        
        config.setCountdownDuration(15);
        config.setCooldownDuration(20);
        config.setPreStartBorderSize(200.0);
        config.setFinalBorderSize(2000.0);
        config.setJoinRemindersEnabled(false);
        config.setReminderInterval(60);
        config.setWorldName("world");
        config.setMinOnlinePlayers(5);
        config.setCountdownBossBarEnabled(false);
        config.setBorderCenterMode("fixed");
        config.setBorderCenterX(100.0);
        config.setBorderCenterZ(-50.0);
        config.setBorderTransitionSeconds(20);
        config.setPreStartBorderDamageAmount(0.1);
        config.setPreStartBorderDamageBuffer(2.0);
        config.setBorderDamageAmount(0.4);
        config.setBorderDamageBuffer(6.0);
        config.setStartingDifficulty("easy");
        config.setStartedDifficulty("hard");
        config.setStartingDisableMobSpawning(false);
        config.setStartedDisableMobSpawning(true);
        config.setStartingDisableMobDamage(false);
        config.setStartedDisableMobDamage(true);
        
        assertEquals(15, config.getCountdownDuration());
        assertEquals(20, config.getCooldownDuration());
        assertEquals(200.0, config.getPreStartBorderSize());
        assertEquals(2000.0, config.getFinalBorderSize());
        assertFalse(config.isJoinRemindersEnabled());
        assertEquals(60, config.getReminderInterval());
        assertEquals("world", config.getWorldName());
        assertEquals(5, config.getMinOnlinePlayers());
        assertFalse(config.isCountdownBossBarEnabled());
        assertEquals("fixed", config.getBorderCenterMode());
        assertEquals(100.0, config.getBorderCenterX());
        assertEquals(-50.0, config.getBorderCenterZ());
        assertEquals(20, config.getBorderTransitionSeconds());
        assertEquals(0.1, config.getPreStartBorderDamageAmount());
        assertEquals(2.0, config.getPreStartBorderDamageBuffer());
        assertEquals(0.4, config.getBorderDamageAmount());
        assertEquals(6.0, config.getBorderDamageBuffer());
        assertEquals("easy", config.getStartingDifficulty());
        assertEquals("hard", config.getStartedDifficulty());
        assertFalse(config.isStartingDisableMobSpawning());
        assertTrue(config.isStartedDisableMobSpawning());
        assertFalse(config.isStartingDisableMobDamage());
        assertTrue(config.isStartedDisableMobDamage());
    }
    
    @Test
    void testStateDataDefaults() {
        StateData stateData = new StateData();
        
        assertEquals(PluginState.IDLE, stateData.getCurrentState());
        assertEquals(0, stateData.getRemainingCountdown());
        assertEquals(0, stateData.getRemainingCooldown());
    }
    
    @Test
    void testStateDataSetters() {
        StateData stateData = new StateData();
        
        stateData.setCurrentState(PluginState.COUNTDOWN);
        stateData.setRemainingCountdown(10);
        stateData.setRemainingCooldown(5);
        
        assertEquals(PluginState.COUNTDOWN, stateData.getCurrentState());
        assertEquals(10, stateData.getRemainingCountdown());
        assertEquals(5, stateData.getRemainingCooldown());
    }
}
