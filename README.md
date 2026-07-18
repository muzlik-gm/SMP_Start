<div align="center">

[![Typing SVG](https://readme-typing-svg.demolab.com?font=Bitcount+Grid+Double&size=45&duration=3000&pause=1000&color=3498DB&center=true&vCenter=true&width=900&height=60&lines=SMP+STARTER;Ultimate+SMP+Launch+System;Cinematic++%26+Dynamic)](https://git.io/typing-svg)

[![Release](https://img.shields.io/github/v/release/muzlik-gm/SMP_Start?color=2ecc71&logo=github&style=for-the-badge)](https://github.com/muzlik-gm/SMP_Start/releases)
[![Downloads](https://img.shields.io/github/downloads/muzlik-gm/SMP_Start/total?color=3498db&logo=github&style=for-the-badge)](https://github.com/muzlik-gm/SMP_Start/releases)
[![License](https://img.shields.io/github/license/muzlik-gm/SMP_Start?color=f1c40f&style=for-the-badge)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)

*Transform your server launch into a cinematic event with countdowns, dynamic borders, and phased gameplay.*

</div>

---

## ✨ Why SMP Starter?

Launching an SMP server shouldn't be chaotic. SMP Starter provides a professional, controlled environment for your server launch.

| Feature | Description |
| :--- | :--- |
| 🎬 **Cinematic Countdown** | Immersive title sequences, sound effects, and a live BossBar timer for all players. |
| 🌍 **Dynamic Borders** | Automatically expands the world border from a cozy pre-start size to the full SMP scale. |
| 🛡️ **Phased Protection** | Temporary PvP immunity and block protection until the official start signal. |
| 🧠 **Smart Reminders** | Automatically notifies OPs when they join if the SMP hasn't started yet. |
| ⚙️ **Full Control** | Configure everything via an interactive in-game menu or simple config commands. |

---

## 🎮 Commands & Usage

All commands use the `/smp` alias. Permissions are granular for easy staff management.

### Core Commands

| Command | Permission | Description |
| :--- | :--- | :--- |
| `/smp start` | `smpstart.use` | Initiates the cinematic countdown sequence. |
| `/smp cancel` | `smpstart.cancel` | Aborts an active countdown immediately. |
| `/smp reset` | `smpstart.reset` | Resets the server to pre-start state (teleports players, resets border). |
| `/smp status` | `smpstart.use` | Displays current phase, timer, and configuration summary. |
| `/smp menu` | `smpstart.use` | Opens the graphical control panel for quick actions. |
| `/smp config` | `smpstart.config` | Opens the interactive configuration editor. |

### Configuration Shortcuts
Quickly tweak settings without editing files:
```bash
/smp config countdown 30       # Set countdown to 30 seconds
/smp config minplayers 5       # Require 5 players to start
/smp config border final 5000  # Set final border size to 5000x5000
/smp config pvp 15             # Set PvP protection to 15 minutes
```

---

## 🔐 Permissions

| Permission Node | Description |
| :--- | :--- |
| `smpstart.*` | Grants access to all plugin features. |
| `smpstart.use` | Allows running start, status, and menu commands. |
| `smpstart.admin` | Grants access to reset, cancel, and reload commands. |
| `smpstart.config` | Allows modification of plugin settings. |

---

## ⚙️ Configuration

Edit `plugins/SMPStart/config.yml` or use `/smp config`.

```yaml
# 🌍 World Settings
general:
  world: "world"             # Target world name
  min-players: 2             # Minimum players required to start
  debug: false

# ⏱️ Countdown Settings
countdown:
  duration: 10               # Seconds
  cooldown: 120              # Cooldown between starts
  bossbar: true              # Show visual timer

# 🚧 Border Management
border:
  pre-start-size: 200        # Safe zone size
  final-size: 10000          # Full SMP size
  transition-seconds: 30     # Smooth expansion time
  center-mode: "spawn"       # 'spawn' or 'fixed'

# 🛡️ Protection Phases
pvp:
  protection-duration: 30    # Minutes of peace after start

phases:
  pre-start:
    difficulty: "peaceful"
    disable-mob-spawning: true
  started:
    difficulty: "normal"
    disable-mob-spawning: false
```

---

## 🔄 How It Works

1.  **Setup**: Configure your border sizes and player requirements.
2.  **Wait**: Players join and explore the safe pre-start zone.
3.  **Launch**: Run `/smp start`. The countdown begins!
4.  **Expand**: At zero, the border expands, PvP unlocks, and difficulty increases.
5.  **Play**: Your SMP has officially begun.

---

## 📦 Installation

1.  Download the latest `.jar` from the [Releases Page](https://github.com/muzlik-gm/SMP_Start/releases).
2.  Drop the file into your server's `plugins/` folder.
3.  Restart your server.
4.  Run `/smp config` to set up your preferences.

> **Requirements**: Paper/Spigot 1.19+ | Java 17+

---

<div align="center">

### 🛠️ Building from Source

```bash
git clone https://github.com/muzlik-gm/SMP_Start.git
cd SMP_Start
mvn clean package
```

---

**Made with ❤️ by MuzlikGamer**  
*Licensed under MIT*

[Report Bug](https://github.com/muzlik-gm/SMP_Start/issues) • [Request Feature](https://github.com/muzlik-gm/SMP_Start/issues)

</div>
