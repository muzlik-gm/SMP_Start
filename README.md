# Muzlik's SMP Starter

A coordinated launch system for Minecraft SMP servers with balanced early-game defaults, phase-based safety, and deep customization.

## Features

- **Countdown System**: Multi-second countdown with audio and visual effects
- **Countdown Boss Bar**: On-screen countdown progress bar
- **World Border Management**: Automatic border resizing on SMP start
- **Phase Controls**: Pre-start vs post-start difficulty and mob rules
- **Join Reminders**: Automated reminders for offline OP players
- **PvP Protection**: Temporary PvP disable window after SMP starts
- **Block Protection**: Prevents non-OP block interactions before SMP start
- **Spawn Safety**: Teleports players inside the border pre-start
- **Mob Safety**: Optional pre-start mob spawning and damage disable
- **Border Controls**: Transition timing and damage settings per phase
- **Minimum Player Gate**: Require a minimum number of players to start
- **Cooldown System**: Prevents spam usage of the start command

## Commands

All commands are under the `/smp` root. Aliases: `/smpstart`, `/msmp`.

### `/smp start`
Starts the SMP countdown sequence.
- **Permission**: `smpstart.use`

### `/smp cancel`
Cancels the active countdown.
- **Permission**: `smpstart.cancel`

### `/smp reset`
Resets the SMP back to pre-start state and teleports players to spawn.
- **Permission**: `smpstart.reset`

### `/smp reload`
Reloads plugin configuration.
- **Permission**: `smpstart.reload`

### `/smp status`
Shows current plugin state and configuration.
- **Permission**: `smpstart.use`

### `/smp menu`
Opens the interactive SMP starter control menu.
- **Permission**: `smpstart.use`

### `/smp config <key> [value]`
View or change a configuration setting. Use `/smp config` with no arguments in-game to open the interactive config menu.
- **Permission**: `smpstart.config`

#### Configuration Keys:
| Key | Description |
| --- | --- |
| `countdown <seconds>` | Countdown duration |
| `cooldown <seconds>` | Cooldown duration |
| `preborder <blocks>` | Pre-start border size |
| `finalborder <blocks>` | Final border size |
| `pvp <minutes>` | PvP protection duration |
| `minplayers <count>` | Minimum online players to start |
| `reminders <true\|false>` | Enable/disable join reminders |
| `reminderinterval <seconds>` | Reminder interval |
| `bossbar <true\|false>` | Toggle countdown boss bar |
| `bordercenter <spawn\|fixed>` | Border center mode |
| `bordercenterpos <x> <z>` | Fixed border center coordinates |
| `world <name\|default>` | Target world for border/PvP |

### `/smp help`
Shows the full command list.

## Permissions

| Permission | Description |
| --- | --- |
| `smpstart.*` | All permissions |
| `smpstart.use` | Use `/smp start` and `/smp status` |
| `smpstart.config` | Configure settings |
| `smpstart.cancel` | Cancel an active countdown |
| `smpstart.reload` | Reload plugin configuration |
| `smpstart.reset` | Reset SMP to pre-start state |
| `smpstart.admin` | Admin features and detailed error messages |

## Installation

1. Download the latest release JAR
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure settings using `/smp config`

## Configuration

Settings are grouped into sections in `config.yml`:

```yaml
general:
  world: ""          # target world (empty = default)
  min-players: 1     # minimum online players to start
  debug: false

countdown:
  duration: 10       # seconds
  cooldown: 60       # seconds before /smp start can be used again
  bossbar: true

border:
  pre-start-size: 6000.0
  final-size: 10000.0
  transition-seconds: 10
  center-mode: "spawn"   # spawn | fixed
  center-x: 0.0
  center-z: 0.0
  pre-start-damage:
    amount: 0.0
    buffer: 0.0
  post-start-damage:
    amount: 0.2
    buffer: 5.0

pvp:
  protection-duration: 30   # minutes (0 to disable)

reminders:
  enabled: true
  interval: 60   # seconds

phases:
  pre-start:
    difficulty: "peaceful"
    disable-mob-spawning: true
    disable-mob-damage: true
  started:
    difficulty: "normal"
    disable-mob-spawning: false
    disable-mob-damage: false
```

## How It Works

1. **Setup**: Configure your desired settings using `/smp config`
2. **Start**: Use `/smp start` to begin the countdown
3. **Countdown**: Players see countdown titles, sounds, and a boss bar
4. **Launch**: At zero, the border expands and the SMP officially begins
5. **Cooldown**: The command is disabled for the cooldown period

## Requirements

- Minecraft Server 1.19+
- Java 17+
- Bukkit / Spigot / Paper

## Building

```bash
git clone https://github.com/muzlik-gm/SMP_Start.git
cd SMP_Start
mvn clean package
```

The compiled JAR will be in the `target` directory.

## License

This project is licensed under the MIT License.

## Author

Created by **muzlik**
