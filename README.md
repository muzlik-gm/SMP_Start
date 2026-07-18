<div align="center">

![SMP Starter Banner](https://readme-typing-svg.demolab.com?font=Fira+Code&size=30&duration=3000&pause=1000&color=38bdf8&center=true&vCenter=true&width=600&lines=___+__+_____+____+__+__+_____+____;SMP+STARTER;Ultimate+SMP+Launch+System)

[![GitHub release](https://img.shields.io/github/v/release/muzlik-gm/SMP_Start?style=for-the-badge&logo=github&color=blue)](https://github.com/muzlik-gm/SMP_Start/releases)
[![Build Status](https://img.shields.io/github/actions/workflow/status/muzlik-gm/SMP_Start/ci.yml?style=for-the-badge&logo=github-actions&label=Build)](https://github.com/muzlik-gm/SMP_Start/actions)
[![License](https://img.shields.io/github/license/muzlik-gm/SMP_Start?style=for-the-badge&logo=github&color=green)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.19+-brightgreen?style=for-the-badge&logo=minecraft&logoColor=white)](https://www.minecraft.net/)

*A coordinated launch system for Minecraft SMP servers with balanced early-game defaults, phase-based safety, and deep customization.*

</div>

---

## ✨ Features

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

---

## 🎮 Commands

*All commands are under the `/smp` root. Aliases: `/smpstart`, `/msmp`.*

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

---

## 🔐 Permissions

| Permission | Description |
| --- | --- |
| `smpstart.*` | All permissions |
| `smpstart.use` | Use `/smp start` and `/smp status` |
| `smpstart.config` | Configure settings |
| `smpstart.cancel` | Cancel an active countdown |
| `smpstart.reload` | Reload plugin configuration |
| `smpstart.reset` | Reset SMP to pre-start state |
| `smpstart.admin` | Admin features and detailed error messages |

---

## 📦 Installation

1. Download the latest release JAR from the [Releases](https://github.com/muzlik-gm/SMP_Start/releases) page
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure settings using `/smp config`

---

## ⚙️ Configuration

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

---

## 🔄 How It Works

1. **Setup**: Configure your desired settings using `/smp config`
2. **Start**: Use `/smp start` to begin the countdown
3. **Countdown**: Players see countdown titles, sounds, and a boss bar
4. **Launch**: At zero, the border expands and the SMP officially begins
5. **Cooldown**: The command is disabled for the cooldown period

---

## 📋 Requirements

- Minecraft Server 1.19+
- Java 17+
- Bukkit / Spigot / Paper

---

## 🛠️ Building from Source

```bash
# Clone the repository
git clone https://github.com/muzlik-gm/SMP_Start.git
cd SMP_Start

# Build with Maven
mvn clean package
```

The compiled JAR will be available in the `target` directory.

---

<div align="center">

### 📄 License

This project is licensed under the [MIT License](LICENSE).

### 👨‍💻 Author

Created with ❤️ by **muzlik**

![Star History Chart](https://api.star-history.com/svg?repos=muzlik-gm/SMP_Start&type=Date)

</div>
