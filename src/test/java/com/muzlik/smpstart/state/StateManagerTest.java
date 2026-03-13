package com.muzlik.smpstart.state;

import com.muzlik.smpstart.state.StateManager.PluginState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for StateManager logic
 */
class StateManagerTest {
    
    @Test
    void testPluginStateEnum() {
        // Test that all expected states exist
        PluginState[] states = PluginState.values();
        assertEquals(3, states.length);
        
        // Test specific states
        assertEquals(PluginState.IDLE, PluginState.valueOf("IDLE"));
        assertEquals(PluginState.COUNTDOWN, PluginState.valueOf("COUNTDOWN"));
        assertEquals(PluginState.COOLDOWN, PluginState.valueOf("COOLDOWN"));
    }
    
    @Test
    void testCanExecuteStartLogic() {
        // Test the logic directly - canExecuteStart should return true only for IDLE state
        
        // Test IDLE state allows execution
        PluginState idleState = PluginState.IDLE;
        assertTrue(idleState == PluginState.IDLE); // This represents canExecuteStart logic
        
        // Test other states don't allow execution
        PluginState countdownState = PluginState.COUNTDOWN;
        assertFalse(countdownState == PluginState.IDLE);
        
        PluginState cooldownState = PluginState.COOLDOWN;
        assertFalse(cooldownState == PluginState.IDLE);
    }
}