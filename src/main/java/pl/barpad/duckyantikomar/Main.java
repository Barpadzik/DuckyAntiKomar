package pl.barpad.duckyantikomar;

import org.bukkit.plugin.java.JavaPlugin;
import pl.barpad.duckyantikomar.animations.AnimationsManager;
import pl.barpad.duckyantikomar.checks.ElytraCriticals;
import pl.barpad.duckyantikomar.checks.FireworkHitDelay;
import pl.barpad.duckyantikomar.main.DiscordHook;
import pl.barpad.duckyantikomar.main.Reload;
import pl.barpad.duckyantikomar.main.UpdateChecker;
import pl.barpad.duckyantikomar.main.ViolationAlerts;
import pl.barpad.duckyantikomar.metrics.MetricsLite;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("DuckyAntiKomar Enabled | Author: Barpad");
        saveDefaultConfig();

        int serviceId = 24978;
        new MetricsLite(this, serviceId);

        AnimationsManager animationsManager = new AnimationsManager(this);
        DiscordHook discordHook = new DiscordHook(this);
        ViolationAlerts violationAlerts = new ViolationAlerts(this, discordHook, animationsManager);

        new FireworkHitDelay(this, violationAlerts, discordHook);
        new ElytraCriticals(this, violationAlerts, discordHook);

        new Reload(this);
        new UpdateChecker(this).checkForUpdates();

        getLogger().info("DuckyAntiKomar has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DuckyAntiKomar Disabled | Author: Barpad");
    }
}