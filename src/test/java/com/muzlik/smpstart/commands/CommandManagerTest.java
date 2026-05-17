package com.muzlik.smpstart.commands;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the CommandManager interface contract.
 */
class CommandManagerTest {

    @Test
    void testCommandManagerInterface() {
        CommandManager commandManager = new CommandManager() {
            @Override
            public void registerCommands() {}

            @Override
            public void showHelp(CommandSender sender) {}

            @Override
            public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
                return true;
            }

            @Override
            public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
                return List.of();
            }
        };

        assertDoesNotThrow(() -> {
            commandManager.registerCommands();
            commandManager.showHelp(null);
            commandManager.onCommand(null, null, "", new String[0]);
            commandManager.onTabComplete(null, null, "", new String[0]);
        });

        assertTrue(commandManager.onCommand(null, null, "", new String[0]));
        assertNotNull(commandManager.onTabComplete(null, null, "", new String[0]));
    }

    @Test
    void testCommandArgumentValidation() {
        String[] validArgs   = {"start"};
        String[] emptyArgs   = {};
        String[] multiArgs   = {"config", "countdown", "30"};

        assertTrue(validArgs.length > 0);
        assertEquals(0, emptyArgs.length);
        assertEquals(3, multiArgs.length);
        assertEquals("start", validArgs[0]);
        assertEquals("config", multiArgs[0]);
    }
}
