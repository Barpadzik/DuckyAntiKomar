# 🛡️ DuckyAntiKomar - Advanced Minecraft Anti-Cheat

**DuckyAntiKomar** is a powerful anti-cheat plugin focused on detecting and preventing unfair Elytra-based exploits, including firework abuse, suspicious speed, and illegal critical hits.  
Compatible with **Minecraft 1.16.5 – 1.21+**.

---

## 🚀 Features

### 🎆 Firework Hit Delay (`KomarA`)
- Blocks attacks performed too soon after using fireworks during Elytra flight.
- Customizable delay, punishment, and alert system.

### 🚧 Elytra Speed Checks (`KomarB`) — *Coming in 2.0*
- Flags players flying faster than allowed based on pitch and velocity.
- Prevents unnatural acceleration via exploit clients or firework spam.

### 🗡️ Elytra Criticals (`KomarC`)
- Detects players performing multiple critical hits in a short timeframe during Elytra flight.
- Helps prevent hacked-client critical abuse.

### ⚠️ Violation Alerts
- Real-time admin notifications in **chat** and **console**.
- Auto-clears alerts after a configurable timeout (default: 5 minutes).
- Discord Webhook support for remote logging.

### 🪝 Discord Webhook Integration
- Sends alerts, animations, and punishment logs to Discord.
- Fully customizable content and formatting.

---

## ⚙️ Configuration

All settings are fully customizable:

- Enable/disable individual checks
- Define thresholds and timeframes
- Choose punishment commands and visual effects
- Translate all messages
- Integrate with Discord

<summary><strong>📁 Example: config.yml</strong></summary>

```
# ===============================
#        AntiKomar - Config
# ===============================

# Time (in seconds), after which alerts for a given player will be reset
alert-timeout: 300

# ===============================
#           KomarA
# ===============================
KomarA-Enable: true # Should check be turned on?
Max-Firework-Delay: 100 # Max-Firework-Delay specifies the maximum time (in milliseconds) that can elapse between using a firework while flying an elytra and hitting another player.
Max-KomarA-Alerts: 5 # Maximum number of demerits, after that the command below will be called
KomarA-Command: "ban %player% AntiKomarSystem [KomarA]"
KomarA-Animation-Enable: false # Should animation be turned on?
KomarA-Animation-Type: NONE # ANIMATIONS: EXPLOSION | SCARY | THUNDERBOLT | GUARDIANS | LAVA | RANDOM | NONE
KomarA-Debug-Mode: false # Caution! This is only for devs only! This will spam your console and may negatively impact server performance!

# ===============================
# KomarB [DISABLED! WORK IN PROGRESS]
# ===============================
KomarB-Enable: false # This check need rewrite, and it's disabled from code [Can't enable it]
Max-KomarB-Alerts: 999
KomarB-Command: "ban %player% AntiKomarSystem [KomarB]"
KomarB-Animation-Enable: false
KomarB-Animation-Type: NONE
KomarB-Debug-Mode: false # Caution! This is only for devs only! This will spam your console and may negatively impact server performance!

# ===============================
#           KomarC
# ===============================
KomarC-Enable: true
KomarC-CriticalHitsRequired: 2
KomarC-Timeframe: 500 # Time (in milliseconds) within which critical hits must occur
Max-KomarC-Alerts: 5
KomarC-Command: "ban %player% AntiKomarSystem [KomarC]"
KomarC-Animation-Enable: false
KomarC-Animation-Type: NONE
KomarC-Debug-Mode: false  # Caution! This is only for devs only! This will spam your console and may negatively impact server performance!

# ===============================
#        Discord WebHook
# ===============================
discord:
  enabled: false
  discord-webhook-url: "https://discord.com/api/webhooks/..."
  username: "DuckyAntiKomar"
  avatar-url: "https://i.imgur.com/wPfoYdI.png"
  violation-message-template: "**AntiKomarSystem**\nPlayer: **%player%**\nCheck: **%check%**\nViolation: **%vl%**"
  animation-play-message-template: "**Animation Played**\nPlayer: **%player%**\nAnimation: **%animation%**"
  punishment-message-template: "**Punishment Executed**\nPlayer: **%player%**\nCommand: `%command%`"

# ===============================
#           Permissions
# ===============================
permissions:
  alerts: "antikomar.alerts"
  bypass: "antikomar.bypass"
  reload: "anitkomar.reload"
# Please note that the permissions section is for information purposes only.
# Changing this will not change the permissions in the plugin!
```

<summary><strong>📁 Example: messages.yml</strong></summary>

```
alert-message: '§6§lANTIKOMAR §8»§f Player §7»§f %player% §7»§6 %check% §7(§c%vl%VL§7)'
no-permission: '§6§lANTIKOMAR §8» &cYou don''t have permission!'
config-reloaded: '§6§lANTIKOMAR §8» &aConfiguration reloaded.'
plugin-reloaded: '§6§lANTIKOMAR §8» &aPlugin successfully reloaded.'
incorrect-usage: '§6§lANTIKOMAR §8» &cUsage: /antikomar reload'
update-available: "§6§lANTIKOMAR §8» &eA new version is available: &c%version%"
update-download: "§6§lANTIKOMAR §8» &eDownload: &a%url%"
update-check-failed: "§6§lANTIKOMAR §8» &cCould not check for updates."
```
