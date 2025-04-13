# 🛡️ Minecraft Anti-Cheat Plugin
Our resource is an advanced anti-cheat plugin designed to detect and prevent unfair gameplay advantages in Minecraft servers. It specifically focuses on elytra flight abuse, firework-based exploits, and speed violations to ensure fair play.

## 🔧 **Features**
**Firework Hit Delay** 🎆

Monitors firework usage during elytra flight.
Prevents players from attacking immediately after boosting with fireworks.

**Elytra Criticals** 🗡️

Detects critical hits that are dealt impossible quickly after using fireworks

**Elytra Speed Checks** 🚀 [Soon - In 2.0]

Detects players exceeding configured speed limits based on flight angles.
Prevents unfair speed boosts using fireworks.

**Violation Alerts** ⚠️

Notifies admins about detected rule violations.
Alerts are sent in both chat and console.
Auto-clears alerts after 5 minutes.

**Discord WebHook** 🪝

Discord Webhook Integration > All alerts on your Discord!

## ⚙️ **Configuration**
All settings, such as maximum allowed speed and firework hit delay, can be adjusted in config.yml.

All messages, can be tranlated via messages.yml.

## 🎮 **Permissions**
antikomar.alerts – Grants access to violation alerts.

antikomar.bypass – Allows a player to bypass all checks, making them immune to detection.

anitkomar.reload - Allows player to reload plugin in game via /antinuker reload

## 📞 Support & Issues
If you have any questions, bug reports, or feature requests, feel free to join our Discord server for support!

### 📜 License
This plugin is licensed under the GNU General Public License v3.0 (GPLv3), meaning it is open-source and can be freely modified and redistributed under the same license.

### config.yml (default)

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

### messages.yml (default)

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
