package pl.barpad.duckyantikomar.checks;

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

    private final ViolationAlerts violationAlerts;
    private final HashMap<UUID, Long> fireworkUsageTimes = new HashMap<>();
    private final int maxFireworkDelay;

    public FireworkHitDelay(Main plugin, ViolationAlerts violationAlerts) {
        this.violationAlerts = violationAlerts;
        FileConfiguration config = plugin.getConfig();
        this.maxFireworkDelay = config.getInt("max-firework-delay", 100);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFireworkUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getItemMeta() instanceof FireworkMeta) {
            if (player.isGliding()) {
                fireworkUsageTimes.put(player.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager) || !(event.getEntity() instanceof Player)) {
            return;
        }

        UUID damagerUUID = damager.getUniqueId();

        if (!fireworkUsageTimes.containsKey(damagerUUID)) {
            return;
        }

        long fireworkTime = fireworkUsageTimes.get(damagerUUID);
        long currentTime = System.currentTimeMillis();
        long delay = currentTime - fireworkTime;


        if (delay <= maxFireworkDelay) {
            Player victim = (Player) event.getEntity();
            violationAlerts.reportViolation(damager.getName(), "KomarA");
        }
    }
}