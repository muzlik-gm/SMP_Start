# Implementation Plan

- [x] 1. Set up project structure and core interfaces


  - Create Maven project structure with Bukkit/Spigot dependencies
  - Define core interfaces for StateManager, ConfigManager, EffectSystem
  - Set up testing framework with JUnit 5, Mockito, MockBukkit, and jqwik
  - Create main plugin class extending JavaPlugin
  - _Requirements: 1.1, 4.1-4.5_



- [x] 2. Implement configuration management system


  - Create PluginConfig data model with default values
  - Implement ConfigManager with persistence to config.yml
  - Add configuration validation methods
  - _Requirements: 4.1-4.5, 5.3, 5.5_

- [ ]* 2.1 Write property test for configuration management
  - **Property 12: Configuration commands update stored values**
  - **Validates: Requirements 4.1, 4.2, 4.3, 4.4**

- [ ]* 2.2 Write property test for configuration validation
  - **Property 14: Border size validation rejects invalid inputs**
  - **Validates: Requirements 5.3**

- [ ]* 2.3 Write property test for default configuration
  - **Property 15: Default configuration provides sensible values**


  - **Validates: Requirements 5.5**

- [x] 3. Implement state management system



  - Create StateData model and PluginState enum
  - Implement StateManager with state transitions and validation
  - Add timer management for countdown and cooldown
  - Implement state persistence for server restart recovery
  - _Requirements: 1.1-1.5, 7.5_

- [ ]* 3.1 Write property test for countdown initiation
  - **Property 1: Countdown initiation sets correct state and border**
  - **Validates: Requirements 1.1, 5.1**

- [ ]* 3.2 Write property test for cooldown prevention
  - **Property 2: Cooldown prevents command execution**
  - **Validates: Requirements 1.2**

- [ ]* 3.3 Write property test for countdown completion
  - **Property 3: Countdown completion triggers state transitions**
  - **Validates: Requirements 1.3, 1.4, 5.2**

- [ ]* 3.4 Write property test for cooldown expiration
  - **Property 4: Cooldown expiration plays completion sound**
  - **Validates: Requirements 1.5**


- [ ]* 3.5 Write property test for state persistence
  - **Property 18: State persistence survives server restart**
  - **Validates: Requirements 7.5**

- [x] 4. Implement world border management

  - Create BorderManager class with Bukkit World API integration
  - Add methods for setting pre-start and final border sizes
  - Implement smooth border transition with validation
  - Add error handling for border operations
  - _Requirements: 1.3, 5.1-5.3_

- [x] 5. Implement effect system for audio and visual feedback


  - Create EffectSystem class with Bukkit sound and title APIs
  - Implement countdown tick effects (sound, message, animation)
  - Add start completion effects and totem pop sound
  - Ensure effects reach all online players
  - _Requirements: 2.1-2.5, 6.5_

- [ ]* 5.1 Write property test for countdown tick effects
  - **Property 5: Countdown tick effects are comprehensive**
  - **Validates: Requirements 2.1, 2.2, 2.3**

- [ ]* 5.2 Write property test for completion effects
  - **Property 6: Countdown completion triggers start effects**
  - **Validates: Requirements 2.4, 2.5**

- [ ]* 5.3 Write property test for effect distribution
  - **Property 16: Effects reach all online players**
  - **Validates: Requirements 6.5**



- [x] 6. Implement join reminder system


  - Create ReminderSystem class with OP player tracking
  - Add periodic reminder scheduling with configurable intervals
  - Implement reminder stopping for individual OPs and on SMP start
  - Add enable/disable functionality
  - _Requirements: 3.1-3.5_

- [ ]* 6.1 Write property test for reminder targeting
  - **Property 7: Join reminders target offline OPs when enabled**
  - **Validates: Requirements 3.1**

- [ ]* 6.2 Write property test for reminder stopping on start
  - **Property 8: SMP start stops all reminders**
  - **Validates: Requirements 3.2**

- [ ]* 6.3 Write property test for individual reminder stopping
  - **Property 9: OP join stops individual reminders**
  - **Validates: Requirements 3.3**

- [ ]* 6.4 Write property test for reminder configuration
  - **Property 10: Reminder configuration takes effect**
  - **Validates: Requirements 3.4**

- [ ]* 6.5 Write property test for disabled reminders
  - **Property 11: Disabled reminders send no messages**


  - **Validates: Requirements 3.5**

- [x] 7. Implement command system


  - Create command handlers for /smpstart and configuration commands
  - Add parameter validation and error messaging
  - Implement help command with current configuration display
  - Add permission checking for OP-only commands
  - _Requirements: 1.1-1.2, 4.1-4.5, 7.1_

- [ ]* 7.1 Write property test for help command completeness
  - **Property 13: Help command displays complete information**
  - **Validates: Requirements 4.5**



- [ ]* 7.2 Write property test for command error handling
  - **Property 17: Invalid command parameters trigger error messages**
  - **Validates: Requirements 7.1**

- [x] 8. Integrate all systems in main plugin class


  - Wire together all managers and systems in plugin onEnable()
  - Register command executors and event listeners
  - Initialize configuration and state on plugin startup
  - Add plugin shutdown cleanup in onDisable()
  - _Requirements: All requirements_



- [x]* 8.1 Write unit tests for plugin integration


  - Test plugin initialization and shutdown sequences
  - Test command registration and event listener setup
  - Test system integration and coordination
  - _Requirements: All requirements_



- [x] 9. Checkpoint - Ensure all tests pass


  - Ensure all tests pass, ask the user if questions arise.

- [x] 10. Add comprehensive error handling and logging


  - Implement try-catch blocks for all external API calls



  - Add detailed logging for state changes and errors
  - Create graceful fallbacks for system failures
  - Add configuration validation with user-friendly messages
  - _Requirements: 7.1-7.5_

- [x] 11. Create plugin.yml and build configuration


  - Configure plugin metadata (name, version, author, description)
  - Set up Maven build with proper Bukkit/Spigot dependencies
  - Configure command definitions and permissions
  - Add plugin loading dependencies if needed
  - _Requirements: All requirements_

- [x] 12. Final checkpoint - Ensure all tests pass



  - Ensure all tests pass, ask the user if questions arise.