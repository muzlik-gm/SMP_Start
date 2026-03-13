# SMP Start Plugin

A coordinated launch system for Minecraft SMP servers with countdown, border management, and player notifications.

## Features

- **Countdown System**: Multi-second countdown with audio and visual effects
- **World Border Management**: Automatic border resizing during SMP start
- **Join Reminders**: Automated reminders for offline OP players
- **Command-based Configuration**: No file editing required
- **Comprehensive Effects**: Sounds, titles, and chat messages
- **Cooldown System**: Prevents spam usage of start command

## Commands

### `/smpstart`
Starts the SMP countdown sequence.
- **Permission**: `smpstart.use`
- **Aliases**: `/start`, `/begin`

### `/smpconfig <setting> [value]`
Configure plugin settings.
- **Permission**: `smpstart.config`
- **Aliases**: `/smpset`, `/smpsettings`

#### Configuration Options:
- `countdown <seconds>` - Set countdown duration
- `cooldown <seconds>` - Set cooldown duration  
- `preborder <size>` - Set pre-start border size
- `finalborder <size>` - Set final border size
- `reminders <true|false>` - Enable/disable join reminders
- `status` - Show current configuration
- `help` - Show help information

## Permissions

- `smpstart.*` - Access to all plugin features
- `smpstart.use` - Use the `/smpstart` command
- `smpstart.config` - Configure plugin settings
- `smpstart.admin` - Access admin features and detailed error messages

## Installation

1. Download the latest release JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure settings using `/smpconfig` commands

## Configuration

The plugin creates a `config.yml` file with default settings:

```yaml
# Countdown duration in seconds
countdown-duration: 10

# Cooldown duration in seconds  
cooldown-duration: 10

# World border size before SMP starts
pre-start-border-size: 100.0

# World border size after SMP starts
final-border-size: 1000.0

# Whether to send join reminders to offline OP players
join-reminders-enabled: true

# How often to send reminders in seconds
reminder-interval: 30

# Enable debug logging
debug: false
```

## How It Works

1. **Setup**: Configure your desired settings using `/smpconfig`
2. **Start**: Use `/smpstart` to begin the countdown
3. **Countdown**: Players see countdown with effects every second
4. **Launch**: At zero, border expands and SMP officially begins
5. **Cooldown**: Command is disabled for the cooldown period

## Requirements

- Minecraft Server 1.19+
- Java 17+
- Bukkit/Spigot/Paper

## Building

```bash
git clone https://github.com/muzlik/smp-start-plugin.git
cd smp-start-plugin
mvn clean package
```

The compiled JAR will be in the `target` directory.

## License

This project is licensed under the MIT License.

## Author

Created by **muzlik**