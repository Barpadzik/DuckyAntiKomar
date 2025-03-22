package pl.barpad.duckyantikomar;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;
import pl.barpad.duckyantikomar.checks.ElytraCriticals;
import pl.barpad.duckyantikomar.checks.FireworkHitDelay;
import pl.barpad.duckyantikomar.checks.NoFireworkHand;
import pl.barpad.duckyantikomar.main.DiscordHook;
import pl.barpad.duckyantikomar.main.Reload;
import pl.barpad.duckyantikomar.main.UpdateChecker;
import pl.barpad.duckyantikomar.main.ViolationAlerts;
import pl.barpad.duckyantikomar.metrics.MetricsLite;

public final class Main extends JavaPlugin {

    private ViolationAlerts violationAlerts;
    private DiscordHook discordHook;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        getLogger().info("DuckyAntiKomar Enabled | Author: Barpad");
        saveDefaultConfig();

        PacketEvents.getAPI().init();

        int serviceId = 24978;
        MetricsLite metricsLite = new MetricsLite(this, serviceId);

        discordHook = new DiscordHook(this);
        violationAlerts = new ViolationAlerts(this, discordHook);

        PacketEvents.getAPI().getEventManager().registerListener(new NoFireworkHand(this, violationAlerts));

        new FireworkHitDelay(this, violationAlerts);
        new ElytraCriticals(this, violationAlerts);
        new Reload(this);
        new UpdateChecker(this).checkForUpdates();

        getLogger().info("DuckyAntiKomar has been successfully enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DuckyAntiKomar Disabled | Author: Barpad");
        PacketEvents.getAPI().terminate();
    }
}