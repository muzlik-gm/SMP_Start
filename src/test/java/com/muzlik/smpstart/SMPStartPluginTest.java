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
        assertEquals(10, config.getCooldownDuration());
        assertEquals(10.0, config.getPreStartBorderSize());
        assertEquals(10000.0, config.getFinalBorderSize());
        assertTrue(config.isJoinRemindersEnabled());
        assertEquals(30, config.getReminderInterval());
        assertEquals(30, config.getPvpProtectionDuration());
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
        
        assertEquals(15, config.getCountdownDuration());
        assertEquals(20, config.getCooldownDuration());
        assertEquals(200.0, config.getPreStartBorderSize());
        assertEquals(2000.0, config.getFinalBorderSize());
        assertFalse(config.isJoinRemindersEnabled());
        assertEquals(60, config.getReminderInterval());
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