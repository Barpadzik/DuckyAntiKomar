package pl.barpad.duckyantikomar.checks;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.main.ViolationAlerts;

import java.util.HashMap;
import java.util.UUID;

public class FireworkHitDelay implements Listener {

    private final Main plugin;
    private final ViolationAlerts violationAlerts;
    private final HashMap<UUID, Long> fireworkUsageTimes = new HashMap<>();
    private volatile int maxFireworkDelay;
    private volatile boolean debugMode;

    public FireworkHitDelay(Main plugin, ViolationAlerts violationAlerts) {
        this.plugin = plugin;
        this.violationAlerts = violationAlerts;
        reloadConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void reloadConfig() {
        FileConfiguration config = plugin.getConfig();
        this.maxFireworkDelay = config.getInt("Max-Firework-Delay", 100);
        this.debugMode = config.getBoolean("KomarA-Debug-Mode", false);

        if (debugMode) {
            Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) Reloaded config: maxFireworkDelay = " + maxFireworkDelay);
        }
    }

    @EventHandler
    public void onFireworkUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getItemMeta() instanceof FireworkMeta) {
            if (player.isGliding()) {
                fireworkUsageTimes.put(player.getUniqueId(), System.currentTimeMillis());

                if (debugMode) {
                    Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) Player " + player.getName() + " used a firework while gliding.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager) || !(event.getEntity() instanceof Player victim)) {
            return;
        }

        UUID damagerUUID = damager.getUniqueId();

        if (!fireworkUsageTimes.containsKey(damagerUUID)) {
            return;
        }

        long fireworkTime = fireworkUsageTimes.get(damagerUUID);
        long currentTime = System.currentTimeMillis();
        long delay = currentTime - fireworkTime;

        if (debugMode) {
            Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) Player " + damager.getName() + " hit " + victim.getName() +
                    " | Firework delay: " + delay + "ms | Max allowed: " + maxFireworkDelay + "ms");
        }

        if (delay <= maxFireworkDelay) {
            violationAlerts.reportViolation(damager.getName(), "KomarA");

            if (debugMode) {
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) Violation detected for player " + damager.getName() + ". Reported as KomarA.");
            }
        }
    }
}