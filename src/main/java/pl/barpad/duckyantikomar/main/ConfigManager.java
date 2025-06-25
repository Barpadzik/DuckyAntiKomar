package pl.barpad.duckyantikomar.main;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public boolean getBoolean(String path, boolean def) {
        return config.contains(path) ? config.getBoolean(path) : def;
    }

    public String getString(String path, String def) {
        return config.contains(path) ? config.getString(path) : def;
    }

    // === ALERT SYSTEM ===

    public int getAlertTimeoutSeconds() {
        return config.getInt("alert-timeout", 300);
    }

    // === KOMAR A ===

    public boolean isKomarAEnabled() {
        return config.getBoolean("KomarA-Enable", true);
    }

    public int getMaxFireworkDelay() {
        return config.getInt("Max-Firework-Delay", 100);
    }

    public int getMaxKomarAAlerts() {
        return config.getInt("Max-KomarA-Alerts", 5);
    }

    public String getKomarACommand() {
        return config.getString("KomarA-Command", "ban %player% AntiKomarSystem [KomarA]");
    }

    public boolean isKomarADebugMode() {
        return config.getBoolean("KomarA-Debug-Mode", false);
    }

    // === KOMAR B ===

    public boolean isKomarBEnabled() {
        return config.getBoolean("KomarB-Enable", false);
    }

    public int getMaxKomarBAlerts() {
        return config.getInt("Max-KomarB-Alerts", 999);
    }

    public String getKomarBCommand() {
        return config.getString("KomarB-Command", "ban %player% AntiKomarSystem [KomarB]");
    }

    public boolean isKomarBDebugMode() {
        return config.getBoolean("KomarB-Debug-Mode", false);
    }

    // === KOMAR C ===

    public boolean isKomarCEnabled() {
        return config.getBoolean("KomarC-Enable", true);
    }

    public int getKomarCCriticalHitsRequired() {
        return config.getInt("KomarC-CriticalHitsRequired", 2);
    }

    public int getKomarCTimeframe() {
        return config.getInt("KomarC-Timeframe", 500);
    }

    public int getMaxKomarCAlerts() {
        return config.getInt("Max-KomarC-Alerts", 5);
    }

    public String getKomarCCommand() {
        return config.getString("KomarC-Command", "ban %player% AntiKomarSystem [KomarC]");
    }

    public boolean isKomarCDebugMode() {
        return config.getBoolean("KomarC-Debug-Mode", false);
    }

    public boolean isDebug() {
        return config.getBoolean("Debug", false);
    }

    // === DISCORD WEBHOOK ===

    public boolean isDiscordEnabled() {
        return config.getBoolean("discord.enabled", false);
    }

    public String getDiscordWebhookUrl() {
        return config.getString("discord.discord-webhook-url", "");
    }

    public String getDiscordUsername() {
        return config.getString("discord.username", "DuckyAntiKomar");
    }

    public String getDiscordAvatarUrl() {
        return config.getString("discord.avatar-url", "https://i.imgur.com/wPfoYdI.png");
    }

    public String getDiscordViolationMessageTemplate() {
        return config.getString("discord.violation-message-template",
                "**AntiKomarSystem**\nPlayer: **%player%**\nCheck: **%check%**\nViolation: **%vl%**");
    }

    public String getDiscordAnimationMessageTemplate() {
        return config.getString("discord.animation-play-message-template",
                "**Animation Played**\nPlayer: **%player%**\nAnimation: **%animation%**");
    }

    public String getDiscordPunishmentMessageTemplate() {
        return config.getString("discord.punishment-message-template",
                "**Punishment Executed**\nPlayer: **%player%**\nCommand: `%command%`");
    }

    // === PERMISSIONS ===

    public String getAlertsPermission() {
        return config.getString("permissions.alerts", "antikomar.alerts");
    }

    public String getBypassPermission() {
        return config.getString("permissions.bypass", "antikomar.bypass");
    }

    public String getReloadPermission() {
        return config.getString("permissions.reload", "antikomar.reload");
    }

    // === MESSAGES ===

    public String getAlertMessage() {
        return config.getString("alert-message", "&6&lANTIKOMAR &8»&f Player &7»&f %player% &7»&6 %check% &7(&c%vl%VL&7)");
    }

    public String getNoPermissionMessage() {
        return config.getString("no-permission", "&6&lANTIKOMAR &8» &cYou don't have permission!");
    }

    public String getConfigReloadedMessage() {
        return config.getString("config-reloaded", "&6&lANTIKOMAR &8» &aConfiguration reloaded.");
    }

    public String getPluginReloadedMessage() {
        return config.getString("plugin-reloaded", "&6&lANTIKOMAR &8» &aPlugin successfully reloaded.");
    }

    public String getIncorrectUsageMessage() {
        return config.getString("incorrect-usage", "&6&lANTIKOMAR &8» &cUsage: /antikomar reload");
    }

    public String getUpdateAvailableMessage() {
        return config.getString("update-available", "&6&lANTIKOMAR &8» &eA new version is available: &c%version%");
    }

    public String getUpdateDownloadMessage() {
        return config.getString("update-download", "&6&lANTIKOMAR &8» &eDownload: &a%url%");
    }

    public String getUpdateCheckFailedMessage() {
        return config.getString("update-check-failed", "&6&lANTIKOMAR &8» &cCould not check for updates.");
    }
}