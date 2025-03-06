package pl.barpad.duckyantikomar;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private ViolationAlerts violationAlerts;

    @Override
    public void onEnable() {
        getLogger().info("DuckyAntiKomar Enabled | Author: Barpad");
        saveDefaultConfig();
        int serviceId = 24978;
        MetricsLite metricsLite = new MetricsLite(this, serviceId);
        violationAlerts = new ViolationAlerts(this);
        new FireworkHitDelay(this, violationAlerts);
        new AboveMaxSpeed(this, violationAlerts);
        new NoFireworkHand(this, violationAlerts);
        new Reload(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("DuckyAntiKomar Disabled | Author: Barpad");
    }
}
