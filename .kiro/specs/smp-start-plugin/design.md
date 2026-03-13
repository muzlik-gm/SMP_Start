# SMP Start Plugin Design Document

## Overview

The SMP Start Plugin is a Minecraft server plugin built on the Bukkit/Spigot API that provides a coordinated launch system for SMP servers. The plugin manages countdown sequences, world border transitions, player notifications, and audio-visual effects to create a unified start experience. The system is designed around command-based configuration and state management with robust error handling.

## Architecture

The plugin follows a modular architecture with clear separation of concerns:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Command       │    │   State         │    │   Effect        │
│   Handlers      │────│   Manager       │────│   System        │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │              ┌─────────────────┐              │
         └──────────────│   Config        │──────────────┘
                        │   Manager       │
                        └─────────────────┘
                                 │
                        ┌─────────────────┐
                        │   Persistence   │
                        │   Layer         │
                        └─────────────────┘
```

The plugin operates as a state machine with three primary states:
- **IDLE**: No active countdown or cooldown
- **COUNTDOWN**: Active countdown sequence in progress  
- **COOLDOWN**: Post-start cooldown period active

## Components and Interfaces

### Core Plugin Class
```java
public class SMPStartPlugin extends JavaPlugin {
    private StateManager stateManager;
    private ConfigManager configManager;
    private EffectSystem effectSystem;
    private CommandManager commandManager;
}
```

### State Manager
Manages the plugin's operational state and coordinates between systems:
```java
public class StateManager {
    public enum PluginState { IDLE, COUNTDOWN, COOLDOWN }
    
    public void startCountdown();
    public void handleCountdownTick();
    public void completeCountdown();
    public void startCooldown();
    public boolean canExecuteStart();
}
```

### Configuration Manager
Handles all configurable values and persistence:
```java
public class ConfigManager {
    public int getCountdownDuration();
    public int getCooldownDuration();
    public double getPreStartBorderSize();
    public double getFinalBorderSize();
    public boolean areJoinRemindersEnabled();
}
```

### Effect System
Manages all audio-visual effects and animations:
```java
public class EffectSystem {
    public void playCountdownTick(int remaining);
    public void playStartSound();
    public void playTotemPopSound();
    public void showCountdownTitle(int remaining);
    public void showStartAnimation();
}
```

### Border Manager
Handles world border operations:
```java
public class BorderManager {
    public void setPreStartBorder();
    public void transitionToFinalBorder();
    public void validateBorderSize(double size);
}
```

### Reminder System
Manages OP player join reminders:
```java
public class ReminderSystem {
    public void startReminders();
    public void stopReminders();
    public void sendReminderToOfflineOPs();
}
```

## Data Models

### Plugin Configuration
```java
public class PluginConfig {
    private int countdownDuration = 10; // seconds
    private int cooldownDuration = 10; // seconds
    private double preStartBorderSize = 100.0; // blocks
    private double finalBorderSize = 1000.0; // blocks
    private boolean joinRemindersEnabled = true;
    private int reminderInterval = 30; // seconds
}
```

### State Data
```java
public class StateData {
    private PluginState currentState = PluginState.IDLE;
    private long countdownStartTime;
    private long cooldownStartTime;
    private int remainingCountdown;
    private int remainingCooldown;
}
```

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system-essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property Reflection

After analyzing all acceptance criteria, several properties can be consolidated to eliminate redundancy:

- Properties 1.1 and 5.1 both test countdown initiation - can be combined into a comprehensive countdown start property
- Properties 1.3 and 5.2 both test border transition on countdown completion - can be combined
- Properties 2.1, 2.2, and 2.3 all test countdown tick behavior - can be combined into one comprehensive property
- Properties 4.1, 4.2, 4.3, and 4.4 all test configuration updates - can be combined into one configuration property

### Core Properties

**Property 1: Countdown initiation sets correct state and border**
*For any* valid OP player and countdown duration, executing `/smpstart` should transition the plugin to COUNTDOWN state, initialize the countdown with the specified duration, and set the world border to the pre-start size
**Validates: Requirements 1.1, 5.1**

**Property 2: Cooldown prevents command execution**
*For any* attempt to execute `/smpstart` during active cooldown, the command should be rejected and display the remaining cooldown time
**Validates: Requirements 1.2**

**Property 3: Countdown completion triggers state transitions**
*For any* active countdown, when it reaches zero, the plugin should transition the world border to final size, activate cooldown period, and transition to COOLDOWN state
**Validates: Requirements 1.3, 1.4, 5.2**

**Property 4: Cooldown expiration plays completion sound**
*For any* active cooldown period, when it expires, a totem pop sound should be played globally to all players
**Validates: Requirements 1.5**

**Property 5: Countdown tick effects are comprehensive**
*For any* active countdown second, the plugin should broadcast a message with remaining time, play a sound effect, and display visual animation to all players
**Validates: Requirements 2.1, 2.2, 2.3**

**Property 6: Countdown completion triggers start effects**
*For any* countdown that reaches zero, the plugin should play a distinct start sound and display completion animation
**Validates: Requirements 2.4, 2.5**

**Property 7: Join reminders target offline OPs when enabled**
*For any* configuration where join reminders are enabled, periodic messages should be sent only to OP players who are not online
**Validates: Requirements 3.1**

**Property 8: SMP start stops all reminders**
*For any* active join reminder system, when countdown completes, all reminder sending should cease
**Validates: Requirements 3.2**

**Property 9: OP join stops individual reminders**
*For any* OP player receiving reminders, when that player joins the server, reminders should stop for that specific player only
**Validates: Requirements 3.3**

**Property 10: Reminder configuration takes effect**
*For any* changes to reminder frequency or message content, the new settings should be applied to subsequent reminder operations
**Validates: Requirements 3.4**

**Property 11: Disabled reminders send no messages**
*For any* configuration where join reminders are disabled, no reminder messages should be sent regardless of OP online status
**Validates: Requirements 3.5**

**Property 12: Configuration commands update stored values**
*For any* valid configuration command (border sizes, countdown duration, cooldown duration), the command should update the corresponding stored configuration value
**Validates: Requirements 4.1, 4.2, 4.3, 4.4**

**Property 13: Help command displays complete information**
*For any* help command execution, all available configuration options and their current values should be displayed
**Validates: Requirements 4.5**

**Property 14: Border size validation rejects invalid inputs**
*For any* border size configuration command with non-positive values, the command should be rejected with appropriate error message
**Validates: Requirements 5.3**

**Property 15: Default configuration provides sensible values**
*For any* fresh plugin installation with no custom configuration, default values should be used for all settings
**Validates: Requirements 5.5**

**Property 16: Effects reach all online players**
*For any* effect display operation (sound, animation, message), the effect should be sent to all currently online players
**Validates: Requirements 6.5**

**Property 17: Invalid command parameters trigger error messages**
*For any* command with invalid parameters, clear error messages with usage instructions should be displayed
**Validates: Requirements 7.1**

**Property 18: State persistence survives server restart**
*For any* active countdown or cooldown state, if the server restarts, the plugin should handle state recovery appropriately
**Validates: Requirements 7.5**

## Error Handling

The plugin implements comprehensive error handling across all systems:

### Command Validation
- Parameter validation with clear error messages
- Permission checking for OP-only commands
- State validation (preventing commands during inappropriate states)

### State Management
- Graceful handling of timer interruptions
- Recovery from invalid state transitions
- Logging of all state changes for debugging

### External API Integration
- Bukkit API error handling for world border operations
- Sound system fallbacks if audio fails
- Player messaging with connection state validation

### Configuration Management
- Validation of all configuration values
- Fallback to defaults for invalid settings
- Persistence error handling with retry mechanisms

## Testing Strategy

The plugin will use a dual testing approach combining unit tests and property-based tests:

### Unit Testing Framework
- **JUnit 5** for unit test structure
- **Mockito** for mocking Bukkit API components
- **MockBukkit** for Bukkit-specific testing utilities

Unit tests will cover:
- Specific command execution examples
- State transition edge cases
- Configuration validation scenarios
- Error condition handling

### Property-Based Testing Framework
- **jqwik** for property-based testing in Java
- Minimum 100 iterations per property test
- Custom generators for Minecraft-specific data types

Property-based tests will verify:
- Universal behaviors across all valid inputs
- State consistency under various conditions
- Configuration changes with random valid values
- Timer operations with different durations

### Test Organization
- Each property-based test will be tagged with format: **Feature: smp-start-plugin, Property {number}: {property_text}**
- Tests will be co-located with source files using standard Maven directory structure
- Integration tests will use MockBukkit to simulate full server environment

### Coverage Requirements
- Minimum 90% line coverage for core logic
- All correctness properties must have corresponding property-based tests
- Critical paths (countdown, cooldown, state transitions) require both unit and property tests
