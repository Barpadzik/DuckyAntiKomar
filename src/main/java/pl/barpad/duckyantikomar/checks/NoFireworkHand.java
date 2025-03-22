package pl.barpad.duckyantikomar.checks;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientUseItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.main.ViolationAlerts;

import java.util.HashMap;
import java.util.UUID;

public class NoFireworkHand extends PacketListenerAbstract {

    private final ViolationAlerts violationAlerts;
    private final int maxKomarCAlerts;
    private final int pendingViolations;
    private final String komarCCommand;
    private final boolean debugMode;
    private final boolean enabled;

    private final HashMap<UUID, Integer> pendingReports = new HashMap<>();
    private final HashMap<UUID, Integer> violationCounts = new HashMap<>();

    public NoFireworkHand(Main plugin, ViolationAlerts violationAlerts) {
        this.violationAlerts = violationAlerts;

        FileConfiguration config = plugin.getConfig();
        this.enabled = config.getBoolean("KomarC-Enable", true);
        this.maxKomarCAlerts = config.getInt("Max-KomarC-Alerts", 5);
        this.pendingViolations = config.getInt("KomarC-Pending-Violations", 2);
        this.komarCCommand = config.getString("KomarC-Command", "ban %player% AntiKomarSystem [KomarC]");
        this.debugMode = config.getBoolean("KomarC-Debug-Mode", false);

        PacketEvents.getAPI().getEventManager().registerListener(this);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!enabled) return;

        if (event.getPacketType() != PacketType.Play.Client.USE_ITEM) return;

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!player.isGliding()) return;

        WrapperPlayClientUseItem packet = new WrapperPlayClientUseItem(event);

        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        boolean hadFireworkInMainHand = mainHand.getType() == Material.FIREWORK_ROCKET;
        boolean hadFireworkInOffHand = offHand.getType() == Material.FIREWORK_ROCKET;

        if (debugMode) {
            Bukkit.getLogger().info("[DuckyAntiKomar] Debug: " + player.getName() +
                    " used a firework! MainHand: " + mainHand.getType() +
                    " (Slot " + player.getInventory().getHeldItemSlot() + "), OffHand: " + offHand.getType());
        }

        if (!hadFireworkInMainHand && !hadFireworkInOffHand) return;

        int mainHandAmount = hadFireworkInMainHand ? mainHand.getAmount() : 0;
        int offHandAmount = hadFireworkInOffHand ? offHand.getAmount() : 0;

        if (mainHandAmount > 1 || offHandAmount > 1) return;

        if (mainHandAmount == 1 || offHandAmount == 1) return;

        int currentPending = pendingReports.getOrDefault(playerId, 0) + 1;
        pendingReports.put(playerId, currentPending);

        if (debugMode) {
            Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) " + player.getName() +
                    " has " + currentPending + "/" + pendingViolations + " pending reports.");
        }

        if (currentPending < pendingViolations) return;

        pendingReports.put(playerId, 0);

        int currentViolations = violationCounts.getOrDefault(playerId, 0) + 1;
        violationCounts.put(playerId, currentViolations);

        violationAlerts.reportViolation(player.getName(), "KomarC");

        if (debugMode) {
            Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) " + player.getName() +
                    " used a firework in flight without holding it! VL: " + currentViolations + "/" + maxKomarCAlerts);
        }

        if (currentViolations >= maxKomarCAlerts) {
            violationAlerts.executePunishment(player.getName(), komarCCommand.replace("%player%", player.getName()));

            if (debugMode) {
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarC Debug) Executed punishment for " + player.getName());
            }

            violationCounts.put(playerId, 0);
        }
    }
}