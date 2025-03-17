package pl.barpad.duckyantikomar.checks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.main.ViolationAlerts;

import java.util.HashMap;
import java.util.UUID;

public class NoFireworkHand implements Listener {

    private final ViolationAlerts violationAlerts;
    private final int maxKomarCAlerts;
    private final String komarCCommand;
    private final boolean debugMode;
    private final boolean enabled;

    private final HashMap<UUID, Long> fireworkHoldStart = new HashMap<>();
    private final HashMap<UUID, Long> fireworkUsageTime = new HashMap<>();

    public NoFireworkHand(Main plugin, ViolationAlerts violationAlerts) {
        this.violationAlerts = violationAlerts;

        FileConfiguration config = plugin.getConfig();
        this.enabled = config.getBoolean("KomarC-Enable", true);
        this.maxKomarCAlerts = config.getInt("Max-KomarC-Alerts", 5);
        this.komarCCommand = config.getString("KomarC-Command", "ban %player% AntiKomarSystem [KomarC]");
        this.debugMode = config.getBoolean("KomarC-Debug-Mode", false);

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFireworkHold(PlayerInteractEvent event) {
        if (!enabled) return;
        if (event.getHand() != EquipmentSlot.HAND && event.getHand() != EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
            UUID playerId = player.getUniqueId();

            if (!fireworkHoldStart.containsKey(playerId)) {
                fireworkHoldStart.put(playerId, System.currentTimeMillis());

                if (debugMode) {
                    Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) " + player.getName() + " started holding a firework.");
                }
            }
        }
    }

    @EventHandler
    public void onFireworkUse(PlayerInteractEvent event) {
        if (!enabled) return;
        if (event.getHand() != EquipmentSlot.HAND && event.getHand() != EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
            UUID playerId = player.getUniqueId();

            long holdStartTime = fireworkHoldStart.getOrDefault(playerId, System.currentTimeMillis());
            long holdDuration = System.currentTimeMillis() - holdStartTime;

            fireworkUsageTime.put(playerId, System.currentTimeMillis());

            if (debugMode) {
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) " + player.getName() + " used a firework after holding it for " + holdDuration + "ms.");
            }
        }
    }

    @EventHandler
    public void onFireworkAfterUse(PlayerInteractEvent event) {
        if (!enabled) return;
        if (event.getHand() != EquipmentSlot.HAND && event.getHand() != EquipmentSlot.OFF_HAND) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if ((item == null || item.getType() != Material.FIREWORK_ROCKET) && fireworkUsageTime.containsKey(player.getUniqueId())) {
            UUID playerId = player.getUniqueId();

            long usageTime = fireworkUsageTime.getOrDefault(playerId, System.currentTimeMillis());
            long postUsageDuration = System.currentTimeMillis() - usageTime;

            if (postUsageDuration < 200) {
                violationAlerts.reportViolation(player.getName(), "KomarC");

                int violationCount = violationAlerts.getViolationCount(player.getName(), "KomarC");

                if (debugMode) {
                    Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) " + player.getName() + " released firework too quickly! Violation count: " + violationCount + " / " + maxKomarCAlerts);
                }

                if (violationCount > maxKomarCAlerts) {
                    violationAlerts.executePunishment(player.getName(), komarCCommand);

                    if (debugMode) {
                        Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) Executed punishment: " + komarCCommand.replace("%player%", player.getName()));
                    }
                }
            }

            fireworkHoldStart.remove(playerId);
            fireworkUsageTime.remove(playerId);
        }
    }
}