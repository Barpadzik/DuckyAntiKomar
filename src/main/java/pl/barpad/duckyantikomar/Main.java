package pl.barpad.duckyantikomar;

import org.bukkit.plugin.java.JavaPlugin;
import pl.barpad.duckyantikomar.checks.FireworkHitDelay;
import pl.barpad.duckyantikomar.checks.AboveMaxSpeed;
import pl.barpad.duckyantikomar.checks.NoFireworkHand;
import pl.barpad.duckyantikomar.main.DiscordHook;
import pl.barpad.duckyantikomar.main.Reload;
import pl.barpad.duckyantikomar.main.ViolationAlerts;
import pl.barpad.duckyantikomar.metrics.MetricsLite;;

public final class Main extends JavaPlugin {

    private ViolationAlerts violationAlerts;
    private DiscordHook discordHook;

    @Override
    public void onEnable() {
        getLogger().info("DuckyAntiKomar Enabled | Author: Barpad");
        saveDefaultConfig();
        int serviceId = 24978;
        MetricsLite metricsLite = new MetricsLite(this, serviceId);
        discordHook = new DiscordHook(this);
        violationAlerts = new ViolationAlerts(this, discordHook);
        new NoFireworkHand(this, violationAlerts);
        new AboveMaxSpeed(this, violationAlerts);
        new FireworkHitDelay(this, violationAlerts);
        new Reload(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("DuckyAntiKomar Disabled | Author: Barpad");
    }
}
