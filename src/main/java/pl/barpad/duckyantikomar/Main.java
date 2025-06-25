package pl.barpad.duckyantikomar;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pl.barpad.duckyantikomar.animations.AnimationsManager;
import pl.barpad.duckyantikomar.checks.ElytraCriticals;
import pl.barpad.duckyantikomar.checks.FireworkHitDelay;
import pl.barpad.duckyantikomar.main.*;
import pl.barpad.duckyantikomar.metrics.MetricsLite;

import java.util.ArrayList;
import java.util.List;

public final class Main extends JavaPlugin {

    private ConfigManager configManager;
    private ViolationAlerts violationAlerts;
    private DiscordHook discordHook;
    private AnimationsManager animationsManager;

    private final List<Listener> registeredChecks = new ArrayList<>();

    @Override
    public void onLoad() {
        getLogger().info("DuckyAntiKomar Loading...");
    }

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            initializeComponents();
            registerChecks();
            initializeCommands();
            initializeExtras();

            getLogger().info("DuckyAntiKomar Enabled - Welcome :] | Author: Barpad");
        } catch (Exception e) {
            getLogger().severe("Failed to enable DuckyAntiKomar: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);

        if (violationAlerts != null) {
            violationAlerts.clearAllViolations();
        }

        getLogger().info("DuckyAntiKomar Disabled - Thank You | Author: Barpad");
    }

    private void initializeComponents() {
        this.configManager = new ConfigManager(this);
        this.discordHook = new DiscordHook(this, configManager);
        this.animationsManager = new AnimationsManager(this, configManager);
        this.violationAlerts = new ViolationAlerts(this, discordHook, animationsManager, configManager);
    }

    private void registerChecks() {
        registerCheck(new FireworkHitDelay(this, configManager, violationAlerts, discordHook));
        registerCheck(new ElytraCriticals(this, violationAlerts, discordHook, configManager));

        getLogger().info("Registered " + registeredChecks.size() + " checks");
    }

    private void registerCheck(Listener check) {
        getServer().getPluginManager().registerEvents(check, this);
        registeredChecks.add(check);
    }

    private void initializeCommands() {
        new Reload(this, configManager, violationAlerts);
    }

    private void initializeExtras() {
        new MetricsLite(this, 24978);

        getServer().getScheduler().runTaskAsynchronously(this, () -> {
            try {
                new UpdateChecker(this, configManager).checkForUpdates();
            } catch (Exception e) {
                getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public ViolationAlerts getViolationAlerts() {
        return violationAlerts;
    }

    public DiscordHook getDiscordHook() {
        return discordHook;
    }

    public AnimationsManager getAnimationsManager() {
        return animationsManager;
    }
}