# 🛡️ Minecraft Anti-Cheat Plugin
Our resource is an advanced anti-cheat plugin designed to detect and prevent unfair gameplay advantages in Minecraft servers. It specifically focuses on elytra flight abuse, firework-based exploits, and speed violations to ensure fair play.

# ⚠️ Important Info
This plugin is still in testing. This plugin need Java 21 to run. 

1.18.2 > 1.19.4 > 1.20.4 On our [Guardly Community Discord](https://discord.gg/guardly)!

## 🔧 **Features**
**Elytra Speed Checks** 🚀

Detects players exceeding configured speed limits based on flight angles.
Prevents unfair speed boosts using fireworks.

**Firework Hit Delay** 🎆

Monitors firework usage during elytra flight.
Prevents players from attacking immediately after boosting with fireworks.

**Firework Use Spoof** 🎒

Monitors firework usage during elytra flight.
Prevents players from using firework from inventory or without holding it.

**Violation Alerts** ⚠️

Notifies admins about detected rule violations.
Alerts are sent in both chat and console.
Auto-clears alerts after 5 minutes.

## ⚙️ **Configuration**
All settings, such as maximum allowed speed and firework hit delay, can be adjusted in config.yml.

All messages, can be tranlated via messages.yml.

## 🎮 **Permissions**
antikomar.alerts – Grants access to violation alerts.

antikomar.bypass – Allows a player to bypass all checks, making them immune to detection.

anitkomar.reload - Allows player to reload plugin in game via /antinuker reload

## 📞 Support & Issues
If you have any questions, bug reports, or feature requests, feel free to join our [Discord server](https://discord.gg/guardly) for support!

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
Max-KomarA-Alerts: 5
KomarA-Command: "ban %player% AntiKomarSystem [KomarA]"

# ===============================
#           KomarB
# ===============================
Max-KomarB-Alerts: 15
KomarB-Command: "ban %player% AntiKomarSystem [KomarB]"

# ===============================
#           KomarC
# ===============================
Max-KomarC-Alerts: 5
KomarC-Command: "ban %player% AntiKomarSystem [KomarC]"

# ===============================
#   Elytra maximum speeds
# ===============================
elytra-speeds:
above-max-speed-gliding: 285.0
above-max-speed-boost: 200.0
above-max-speed-pitch-52: 300.0
above-max-speed-pitch-90: 400.0
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
alert-message: §6§lANTIKOMAR §8»§f Player §7»§f %player% §7»§6 %check% §7(§c%vl%VL§7)
no-permission: §6§lANTIKOMAR §8» &cYou don't have permission!
config-reloaded: §6§lANTIKOMAR §8» &aConfiguration reloaded.
plugin-reloaded: §6§lANTIKOMAR §8» &aPlugin successfully reloaded.
incorrect-usage: '§6§lANTIKOMAR §8» &cUsage: /antikomar reload'

```
