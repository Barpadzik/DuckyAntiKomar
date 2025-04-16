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

    public boolean isKomarAAnimationEnabled() {
        return config.getBoolean("KomarA-Animation-Enable", false);
    }

    public String getKomarAAnimationType() {
        return config.getString("KomarA-Animation-Type", "NONE");
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

    public boolean isKomarBAnimationEnabled() {
        return config.getBoolean("KomarB-Animation-Enable", false);
    }

    public String getKomarBAnimationType() {
        return config.getString("KomarB-Animation-Type", "NONE");
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

    public boolean isKomarCAnimationEnabled() {
        return config.getBoolean("KomarC-Animation-Enable", false);
    }

    public String getKomarCAnimationType() {
        return config.getString("KomarC-Animation-Type", "NONE");
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
}