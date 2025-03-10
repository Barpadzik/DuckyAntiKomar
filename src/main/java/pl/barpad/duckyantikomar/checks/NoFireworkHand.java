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

public class NoFireworkHand implements Listener {

    private final ViolationAlerts violationAlerts;
    private final int maxKomarCAlerts;
    private final String komarCCommand;
    private final boolean debugMode;
    private final boolean enabled;

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
    public void onFireworkUse(PlayerInteractEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
            if (event.getHand() == EquipmentSlot.HAND || event.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }

            event.setCancelled(false);
            violationAlerts.reportViolation(player.getName(), "KomarC");

            int violationCount = violationAlerts.getViolationCount(player.getName(), "KomarC");

            if (debugMode) {
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) Player: " + player.getName());
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) Equipment Slot: " + event.getHand());
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) Violation Count: " + violationCount + " / " + maxKomarCAlerts);
            }

            if (violationCount > maxKomarCAlerts) {
                violationAlerts.executePunishment(player.getName(), komarCCommand);

                if (debugMode) {
                    Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) Executed punishment: " + komarCCommand.replace("%player%", player.getName()));
                }
            }
        }
    }
}