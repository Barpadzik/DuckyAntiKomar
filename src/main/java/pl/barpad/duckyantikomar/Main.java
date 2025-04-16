package pl.barpad.duckyantikomar;

import org.bukkit.plugin.java.JavaPlugin;
import pl.barpad.duckyantikomar.animations.AnimationsManager;
import pl.barpad.duckyantikomar.checks.ElytraCriticals;
import pl.barpad.duckyantikomar.checks.FireworkHitDelay;
import pl.barpad.duckyantikomar.main.*;
import pl.barpad.duckyantikomar.metrics.MetricsLite;
import pl.barpad.duckyantikomar.main.ConfigManager;

public final class Main extends JavaPlugin {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        getLogger().info("DuckyAntiKomar Enabled | Author: Barpad");
        saveDefaultConfig();

        int serviceId = 24978;
        new MetricsLite(this, serviceId);
        this.configManager = new ConfigManager(this);
        AnimationsManager animationsManager = new AnimationsManager(this, configManager);
        DiscordHook discordHook = new DiscordHook(this, configManager);
        ViolationAlerts violationAlerts = new ViolationAlerts(this, discordHook, animationsManager, configManager);

        new FireworkHitDelay(this, getConfigManager(), violationAlerts, discordHook);
        new ElytraCriticals(this, violationAlerts, discordHook, getConfigManager());

        new Reload(this, configManager, violationAlerts);
        new UpdateChecker(this).checkForUpdates();

        getLogger().info("DuckyAntiKomar has been successfully enabled!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    @Override
    public void onDisable() {
        getLogger().info("DuckyAntiKomar Disabled | Author: Barpad");
    }
}