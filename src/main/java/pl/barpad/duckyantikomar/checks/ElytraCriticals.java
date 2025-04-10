package pl.barpad.duckyantikomar.checks;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.barpad.duckyantikomar.main.DiscordHook;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.main.ViolationAlerts;

import java.util.HashMap;
import java.util.Map;

public class ElytraCriticals implements Listener {

    private final Main plugin;
    private final ViolationAlerts violationAlerts;
    private final DiscordHook discordHook;
    private boolean enabled;
    private int maxAlerts;
    private String punishmentCommand;
    private boolean debugMode;
    private int criticalHitsRequired;
    private long timeframe;
    private final Map<String, Integer> playerCriticalHits = new HashMap<>();
    private final Map<String, Long> lastHitTime = new HashMap<>();
    private final Map<String, Integer> pendingViolations = new HashMap<>();

    public ElytraCriticals(Main plugin, ViolationAlerts violationAlerts, DiscordHook discordHook) {
        this.plugin = plugin;
        this.violationAlerts = violationAlerts;
        this.discordHook = discordHook;
        loadConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        enabled = config.getBoolean("KomarC-Enable", true);
        maxAlerts = config.getInt("Max-KomarC-Alerts", 5);
        punishmentCommand = config.getString("KomarC-Command", "ban %player% AntiKomarSystem [KomarC]");
        debugMode = config.getBoolean("KomarC-Debug-Mode", false);
        criticalHitsRequired = config.getInt("KomarC-CriticalHitsRequired", 2);
        timeframe = config.getLong("KomarC-Timeframe", 500);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!enabled) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player)) return;

        if (attacker.isGliding() && attacker.getFallDistance() > 0.0) {
            String playerName = attacker.getName();
            long currentTime = System.currentTimeMillis();
            boolean isCritical =
                    attacker.getFallDistance() > 0 &&
                            !attacker.isOnGround() &&
                            !attacker.isInsideVehicle() &&
                            !attacker.hasPotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS) &&
                            attacker.getVelocity().getY() < 0;

            if (isCritical) {
                long lastTime = lastHitTime.getOrDefault(playerName, 0L);
                if (currentTime - lastTime > timeframe) {
                    playerCriticalHits.put(playerName, 1);
                } else {
                    playerCriticalHits.put(playerName, playerCriticalHits.getOrDefault(playerName, 0) + 1);
                }
                lastHitTime.put(playerName, currentTime);

                if (debugMode) {
                    Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) " + playerName + " landed a critical hit while gliding. (" + playerCriticalHits.get(playerName) + "/" + criticalHitsRequired + ")");
                }
            } else {
                playerCriticalHits.put(playerName, 0);
            }

            if (playerCriticalHits.getOrDefault(playerName, 0) >= criticalHitsRequired) {
                pendingViolations.put(playerName, pendingViolations.getOrDefault(playerName, 0) + 1);

                if (pendingViolations.get(playerName) >= 2) {
                    violationAlerts.reportViolation(playerName, "KomarC");
                    int vl = violationAlerts.getViolationCount(playerName, "KomarC");

                    if (debugMode) {
                        Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) " + playerName + " triggered violation after 2 reports (VL: " + vl + ")");
                    }

                    if (vl == maxAlerts) {
                        violationAlerts.executePunishment(playerName, "KomarC", punishmentCommand);
                        discordHook.sendPunishmentCommand(playerName, punishmentCommand);


                        if (debugMode) {
                            Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) Penalty executed for " + playerName);
                        }
                    }

                    pendingViolations.put(playerName, 0);
                }

                playerCriticalHits.put(playerName, 0);
            }
        }
    }
}