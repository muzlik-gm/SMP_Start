| ![Muzlik's SMP Starter banner](https://capsule-render.vercel.app/api?type=rect&color=0:0B3D91,100:0F6B6B&height=140&section=header&text=Muzlik%27s%20SMP%20Starter&fontSize=48&fontColor=FFFFFF&desc=Balanced%20early-game%20launch%20system&descSize=16&descAlignY=75) |
|:--:|

| ![Minecraft badge](https://img.shields.io/badge/Minecraft-1.19%2B-6DB33F?style=for-the-badge&logo=minecraft&logoColor=white) | ![Java badge](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=java&logoColor=white) | ![Platform badge](https://img.shields.io/badge/Platform-Spigot%20%7C%20Paper-FFB400?style=for-the-badge) | ![Status badge](https://img.shields.io/badge/Status-Stable-2E7D32?style=for-the-badge) |
|:--:|:--:|:--:|:--:|

---

## The Problem This Solves

Most SMP launches fail because borders are wrong, players are scattered, and early damage or griefing happens before anyone is ready. Muzlik's SMP Starter gives you a predictable, repeatable launch flow with balanced early-game defaults that keep players safe and synced.

---

## Feature Set

| Countdown System | Border Control | Player Safety | Admin Control |
|:--|:--|:--|:--|
| Titles, sounds, chat, boss bar | Pre-start and final border | Spawn safety + block protection | Cancel, reset, reload |

- Countdown boss bar with clean transitions
- World border management with configurable center (spawn or fixed)
- Pre-start safety: block protection and spawn safety inside the border
- PvP protection with boss bar and warnings after launch
- Join reminders for offline OPs
- Minimum online player gate
- Full SMP reset to pre-start state
- Phase-based difficulty and mob rules for early-game balance
- Configurable border transition and damage settings

---

## Launch Flow

1. Admin runs `/smp start`
2. Countdown begins with effects and boss bar
3. Border expands to final size
4. PvP protection starts for the configured duration
5. Cooldown prevents spam restarts

---

## Commands

All commands are under `/smp`. Aliases: `/smpstart`, `/msmp`.

| Command | Description | Permission |
| --- | --- | --- |
| `/smp start` | Start the SMP countdown | `smpstart.use` |
| `/smp cancel` | Cancel the active countdown | `smpstart.cancel` |
| `/smp reset` | Reset SMP to pre-start and teleport to spawn | `smpstart.reset` |
| `/smp reload` | Reload config | `smpstart.reload` |
| `/smp status` | Show current state and config | `smpstart.use` |
| `/smp config <key> [value]` | View or change a setting | `smpstart.config` |
| `/smp help` | Show the command list | — |

---

## Config Highlights

| Key | Purpose |
| --- | --- |
| `general.world` | Target world for border and PvP (empty = default) |
| `general.min-players` | Minimum online players required to start |
| `countdown.duration` | Countdown length in seconds |
| `countdown.cooldown` | Cooldown before `/smp start` can be used again |
| `countdown.bossbar` | Toggle countdown boss bar |
| `border.pre-start-size` | Border size before SMP starts |
| `border.final-size` | Border size after SMP starts |
| `border.transition-seconds` | Border expansion duration |
| `border.center-mode` | `spawn` or `fixed` |
| `border.center-x` / `center-z` | Fixed center coordinates |
| `border.pre-start-damage.amount` / `buffer` | Border damage before start |
| `border.post-start-damage.amount` / `buffer` | Border damage after start |
| `pvp.protection-duration` | Minutes of PvP protection after start |
| `reminders.enabled` | Remind online players about offline OPs |
| `reminders.interval` | Reminder interval in seconds |
| `phases.pre-start.difficulty` | World difficulty before start |
| `phases.pre-start.disable-mob-spawning` | Disable mob spawning before start |
| `phases.pre-start.disable-mob-damage` | Disable mob damage before start |
| `phases.started.difficulty` | World difficulty after start |
| `phases.started.disable-mob-spawning` | Keep mob spawning disabled after start |
| `phases.started.disable-mob-damage` | Keep mob damage disabled after start |

---

## Border Centering

If your world spawn is at `0 0`, `border.center-mode: "spawn"` works out of the box.
For a custom center, set `border.center-mode: "fixed"` and configure `border.center-x` / `border.center-z`.

---

## Reset Behavior

`/smp reset` resets the SMP to pre-start state:

- Cancels countdowns and cooldowns
- Resets border to pre-start size
- Enables block protection
- Stops PvP protection
- Teleports all online players to a safe spawn location

---

## Early-Game Balance Preset (Default)

Muzlik's SMP Starter ships with a safe, balanced early-game preset out of the box:

- Large but controlled initial border for fair spread
- Peaceful difficulty pre-start
- Mob spawning and mob damage disabled before the SMP begins
- 30 minutes of PvP protection after launch

These defaults are fully customizable in `config.yml`.

---

## Permissions

| Permission | Description |
| --- | --- |
| `smpstart.*` | All permissions |
| `smpstart.use` | Start the countdown and view status |
| `smpstart.config` | Configure settings |
| `smpstart.cancel` | Cancel countdown |
| `smpstart.reset` | Reset SMP state |
| `smpstart.reload` | Reload config |
| `smpstart.admin` | Admin messages and full access |

---

## Compatibility

- Minecraft 1.19+
- Java 17+
- Spigot, Paper, or compatible forks

---
