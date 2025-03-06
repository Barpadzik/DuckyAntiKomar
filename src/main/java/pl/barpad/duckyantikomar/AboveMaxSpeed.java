package pl.barpad.duckyantikomar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class AboveMaxSpeed implements Listener {

    private final Main plugin;
    private final ViolationAlerts violationAlerts;

    private double maxSpeedGliding;
    private double maxSpeedBoost;
    private double maxSpeedPitch52;
    private double maxSpeedPitch90;
    private int maxKomarBAlerts;
    private String komarBCommand;

    public AboveMaxSpeed(Main plugin, ViolationAlerts violationAlerts) {
        this.plugin = plugin;
        this.violationAlerts = violationAlerts;
        loadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private void loadConfig() {
        maxSpeedGliding = plugin.getConfig().getDouble("above-max-speed-gliding", 285.0);
        maxSpeedBoost = plugin.getConfig().getDouble("above-max-speed-boost", 200.0);
        maxSpeedPitch52 = plugin.getConfig().getDouble("above-max-speed-pitch-52", 300.0);
        maxSpeedPitch90 = plugin.getConfig().getDouble("above-max-speed-pitch-90", 400.0);
        maxKomarBAlerts = plugin.getConfig().getInt("Max-KomarB-Alerts", 15);
        komarBCommand = plugin.getConfig().getString("KomarB-Command", "ban %player% AntiKomarSystem [KomarB]");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!player.isGliding()) return;

        Vector velocity = player.getVelocity();
        double speed = velocity.length() * 72.0;

        float pitch = player.getLocation().getPitch();
        double maxAllowedSpeed = getMaxAllowedSpeed(pitch, player);

        if (speed > maxAllowedSpeed) {
            violationAlerts.reportViolation(player.getName(), "KomarB");

            if (violationAlerts.getViolationCount(player.getName(), "KomarB") > maxKomarBAlerts) {
                violationAlerts.executePunishment(player.getName(), komarBCommand);
            }
        }
    }

    private double getMaxAllowedSpeed(float pitch, Player player) {
        if (player.isGliding() && player.isSprinting()) {
            return maxSpeedBoost;
        }
        if (pitch >= 50 && pitch <= 54) {
            return maxSpeedPitch52;
        }
        if (pitch >= 88 && pitch <= 92) {
            return maxSpeedPitch90;
        }
        return maxSpeedGliding;
    }
}