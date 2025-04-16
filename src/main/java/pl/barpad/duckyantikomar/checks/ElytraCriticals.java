package pl.barpad.duckyantikomar.checks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.barpad.duckyantikomar.main.ConfigManager;
import pl.barpad.duckyantikomar.main.DiscordHook;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.main.ViolationAlerts;

import java.util.HashMap;
import java.util.Map;

public class ElytraCriticals implements Listener {

    private final ViolationAlerts violationAlerts;
    private final DiscordHook discordHook;
    private final ConfigManager config;

    private final Map<String, Integer> playerCriticalHits = new HashMap<>();
    private final Map<String, Long> lastHitTime = new HashMap<>();
    private final Map<String, Integer> pendingViolations = new HashMap<>();

    public ElytraCriticals(Main plugin, ViolationAlerts violationAlerts, DiscordHook discordHook, ConfigManager config) {
        this.violationAlerts = violationAlerts;
        this.discordHook = discordHook;
        this.config = config;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!config.isKomarCEnabled()) return;
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Player)) return;

        if (attacker.isGliding() && attacker.getFallDistance() > 0.0) {
            String playerName = attacker.getName();
            long currentTime = System.currentTimeMillis();

            boolean isCritical = attacker.getFallDistance() > 0 &&
                    !attacker.isOnGround() &&
                    !attacker.isInsideVehicle() &&
                    !attacker.hasPotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS) &&
                    attacker.getVelocity().getY() < 0;

            if (isCritical) {
                long lastTime = lastHitTime.getOrDefault(playerName, 0L);
                if (currentTime - lastTime > config.getKomarCTimeframe()) {
                    playerCriticalHits.put(playerName, 1);
                } else {
                    playerCriticalHits.put(playerName, playerCriticalHits.getOrDefault(playerName, 0) + 1);
                }
                lastHitTime.put(playerName, currentTime);

                if (config.isKomarCDebugMode()) {
                    Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) " + playerName + " landed a critical hit while gliding. (" + playerCriticalHits.get(playerName) + "/" + config.getKomarCCriticalHitsRequired() + ")");
                }
            } else {
                playerCriticalHits.put(playerName, 0);
            }

            if (playerCriticalHits.getOrDefault(playerName, 0) >= config.getKomarCCriticalHitsRequired()) {
                pendingViolations.put(playerName, pendingViolations.getOrDefault(playerName, 0) + 1);

                if (pendingViolations.get(playerName) >= 2) {
                    violationAlerts.reportViolation(playerName, "KomarC");
                    int vl = violationAlerts.getViolationCount(playerName, "KomarC");

                    if (config.isKomarCDebugMode()) {
                        Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) " + playerName + " triggered violation after 2 reports (VL: " + vl + ")");
                    }

                    if (vl == config.getMaxKomarCAlerts()) {
                        String cmd = config.getKomarCCommand();
                        violationAlerts.executePunishment(playerName, "KomarC", cmd);
                        discordHook.sendPunishmentCommand(playerName, cmd);

                        if (config.isKomarCDebugMode()) {
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