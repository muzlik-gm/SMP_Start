package com.muzlik.smpstart.border;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for BorderManager validation logic
 */
class BorderManagerTest {
    
    @Test
    void testBorderSizeValidation() {
        // Create a mock implementation for testing validation
        BorderManager borderManager = new BorderManager() {
            @Override
            public void setPreStartBorder() {}
            @Override
            public void transitionToFinalBorder() {}
            @Override
            public double getCurrentBorderSize() { return 0; }
            @Override
            public void setBorderSize(double size) {}
            @Override
            public void initializeBorder() {}
            @Override
            public void setFinalBorder() {}
            
            @Override
            public boolean validateBorderSize(double size) {
                return size > 0 && size <= 60000000; // Minecraft's maximum world border size
            }
        };
        
        // Test valid border sizes
        assertTrue(borderManager.validateBorderSize(1.0));
        assertTrue(borderManager.validateBorderSize(100.0));
        assertTrue(borderManager.validateBorderSize(1000.0));
        assertTrue(borderManager.validateBorderSize(60000000.0)); // Max size
        
        // Test invalid border sizes
        assertFalse(borderManager.validateBorderSize(0.0));
        assertFalse(borderManager.validateBorderSize(-1.0));
        assertFalse(borderManager.validateBorderSize(-100.0));
        assertFalse(borderManager.validateBorderSize(60000001.0)); // Over max size
    }
    
    @Test
    void testBorderSizeLimits() {
        // Test specific boundary conditions
        BorderManager borderManager = new BorderManager() {
            @Override
            public void setPreStartBorder() {}
            @Override
            public void transitionToFinalBorder() {}
            @Override
            public double getCurrentBorderSize() { return 0; }
            @Override
            public void setBorderSize(double size) {}
            @Override
            public void initializeBorder() {}
            @Override
            public void setFinalBorder() {}
            
            @Override
            public boolean validateBorderSize(double size) {
                return size > 0 && size <= 60000000;
            }
        };
        
        // Test edge cases
        assertFalse(borderManager.validateBorderSize(0.0)); // Exactly zero
        assertTrue(borderManager.validateBorderSize(0.1)); // Just above zero
        assertTrue(borderManager.validateBorderSize(60000000.0)); // Exactly max
        assertFalse(borderManager.validateBorderSize(60000000.1)); // Just above max
    }
}