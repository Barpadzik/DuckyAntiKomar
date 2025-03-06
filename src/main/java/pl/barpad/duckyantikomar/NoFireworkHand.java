package pl.barpad.duckyantikomar;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class NoFireworkHand implements Listener {

    private final ViolationAlerts violationAlerts;
    private final int maxKomarCAlerts;
    private final String komarCCommand;

    public NoFireworkHand(Main plugin, ViolationAlerts violationAlerts) {
        this.violationAlerts = violationAlerts;

        FileConfiguration config = plugin.getConfig();
        this.maxKomarCAlerts = config.getInt("Max-KomarC-Alerts", 5);
        this.komarCCommand = config.getString("KomarC-Command", "ban %player% AntiKomarSystem [KomarC]");

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFireworkUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FIREWORK_ROCKET) {
            if (event.getHand() == EquipmentSlot.HAND || event.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }

            event.setCancelled(false);
            violationAlerts.reportViolation(player.getName(), "KomarC");

            if (violationAlerts.getViolationCount(player.getName(), "KomarC") > maxKomarCAlerts) {
                violationAlerts.executePunishment(player.getName(), komarCCommand);
            }
        }
    }
}