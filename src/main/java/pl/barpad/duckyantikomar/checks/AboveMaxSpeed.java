package pl.barpad.duckyantikomar.checks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.main.ViolationAlerts;

public class AboveMaxSpeed implements Listener {

    private final Main plugin;
    private final ViolationAlerts violationAlerts;

    private double maxSpeedGliding;
    private double maxSpeedBoost;
    private double maxSpeedPitch10;
    private double maxSpeedPitch50;
    private double maxSpeedPitch54;
    private double maxSpeedPitch87;
    private double maxSpeedPitch90;
    private double maxSpeedPitch92;
    private double maxSpeedPitch100;
    private int maxKomarBAlerts;
    private String komarBCommand;
    private boolean debugMode;
    private boolean komarBEnabled;

    public AboveMaxSpeed(Main plugin, ViolationAlerts violationAlerts) {
        this.plugin = plugin;
        this.violationAlerts = violationAlerts;
        loadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void loadConfig() {
        maxSpeedGliding = plugin.getConfig().getDouble("above-max-speed-gliding", 282.6);
        maxSpeedBoost = plugin.getConfig().getDouble("above-max-speed-boost", 128.6);
        maxSpeedPitch10 = plugin.getConfig().getDouble("above-max-speed-pitch-10", 240.0);
        maxSpeedPitch50 = plugin.getConfig().getDouble("above-max-speed-pitch-50", 200.0);
        maxSpeedPitch54 = plugin.getConfig().getDouble("above-max-speed-pitch-54", 242.3);
        maxSpeedPitch87 = plugin.getConfig().getDouble("above-max-speed-pitch-87", 282.4);
        maxSpeedPitch90 = plugin.getConfig().getDouble("above-max-speed-pitch-90", 282.6);
        maxSpeedPitch92 = plugin.getConfig().getDouble("above-max-speed-pitch-92", 300.0);
        maxSpeedPitch100 = plugin.getConfig().getDouble("above-max-speed-pitch-100", 320.0);
        maxKomarBAlerts = plugin.getConfig().getInt("Max-KomarB-Alerts", 10);
        komarBCommand = plugin.getConfig().getString("KomarB-Command", "ban %player% AntiKomarSystem [KomarB]");
        debugMode = plugin.getConfig().getBoolean("KomarB-Debug-Mode", false);
        komarBEnabled = plugin.getConfig().getBoolean("KomarB-Enable", true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!komarBEnabled) {
            if (debugMode) {
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarB Debug) Check is disabled in config.yml.");
            }
            return;
        }

        Player player = event.getPlayer();
        if (!player.isGliding()) return;

        Vector velocity = player.getVelocity();
        double speed = velocity.length() * 72.0;
        float pitch = player.getLocation().getPitch();
        double maxAllowedSpeed = getMaxAllowedSpeed(pitch, player);

        if (debugMode) {
            Bukkit.getLogger().info(String.format("[DuckyAntiKomar] (KomarB Debug) %s - Speed: %.2f km/h, Pitch: %.2f, MaxAllowed: %.2f km/h",
                    player.getName(), speed, pitch, maxAllowedSpeed));
        }

        if (speed > maxAllowedSpeed) {
            violationAlerts.reportViolation(player.getName(), "KomarB");

            int violationCount = violationAlerts.getViolationCount(player.getName(), "KomarB");

            if (debugMode) {
                Bukkit.getLogger().warning(String.format("[DuckyAntiKomar] (KomarB Debug) %s exceeded max speed! (%.2f km/h > %.2f km/h) | Violations: %d",
                        player.getName(), speed, maxAllowedSpeed, violationCount));
            }

            if (violationCount > maxKomarBAlerts) {
                if (debugMode) {
                    Bukkit.getLogger().warning(String.format("[DuckyAntiKomar] (KomarB Debug) Executing punishment for %s: %s",
                            player.getName(), komarBCommand.replace("%player%", player.getName())));
                }
                violationAlerts.executePunishment(player.getName(), komarBCommand);
            }
        }
    }

    private double getMaxAllowedSpeed(float pitch, Player player) {
        if (player.isGliding() && player.isSprinting()) {
            return maxSpeedBoost;
        }

        if (pitch >= -10 && pitch <= 10) {
            return maxSpeedGliding;
        }
        if (pitch > 10 && pitch < 50) {
            return maxSpeedPitch10;
        }
        if (pitch >= 50 && pitch <= 54) {
            return maxSpeedPitch50;
        }
        if (pitch > 54 && pitch < 87) {
            return maxSpeedPitch54;
        }
        if (pitch >= 87 && pitch <= 90) {
            return maxSpeedPitch87;
        }
        if (pitch > 90 && pitch <= 92) {
            return maxSpeedPitch90;
        }
        if (pitch > 92 && pitch <= 100) {
            return maxSpeedPitch92;
        }
        if (pitch > 100) {
            return maxSpeedPitch100;
        }

        return maxSpeedGliding;
    }
}