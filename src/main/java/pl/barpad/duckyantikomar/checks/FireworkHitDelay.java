package pl.barpad.duckyantikomar.checks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.main.DiscordHook;
import pl.barpad.duckyantikomar.main.ViolationAlerts;

import java.util.HashMap;
import java.util.UUID;

public class FireworkHitDelay implements Listener {

    private final Main plugin;
    private final ViolationAlerts violationAlerts;
    private final DiscordHook discordHook;
    private final HashMap<UUID, Long> fireworkUsageTimes = new HashMap<>();
    private int maxAlerts;
    private String punishmentCommand;
    private int maxFireworkDelay;
    private boolean debugMode;
    private boolean enabled;

    public FireworkHitDelay(Main plugin, ViolationAlerts violationAlerts, DiscordHook discordHook) {
        this.plugin = plugin;
        this.violationAlerts = violationAlerts;
        this.discordHook = discordHook;
        loadConfig();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        this.enabled = config.getBoolean("KomarA-Enable", true);
        this.maxFireworkDelay = config.getInt("Max-Firework-Delay", 100);
        this.debugMode = config.getBoolean("KomarA-Debug-Mode", false);
        this.maxAlerts = config.getInt("Max-KomarA-Alerts", 5);
        this.punishmentCommand = config.getString("KomarA-Command", "ban %player% AntiKomarSystem [KomarA]");

        if (debugMode) {
            Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) Config reloaded: enabled=" + enabled + ", delay=" + maxFireworkDelay + "ms");
        }
    }

    @EventHandler
    public void onFireworkUse(PlayerInteractEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FIREWORK_ROCKET && player.isGliding()) {
            fireworkUsageTimes.put(player.getUniqueId(), System.currentTimeMillis());

            if (debugMode) {
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) " + player.getName() + " used a firework while gliding.");
            }
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!enabled) return;

        if (!(event.getDamager() instanceof Player damager) || !(event.getEntity() instanceof Player victim)) return;

        UUID damagerUUID = damager.getUniqueId();
        String playerName = damager.getName();

        Long fireworkTime = fireworkUsageTimes.remove(damagerUUID);

        if (fireworkTime == null) return;

        long delay = System.currentTimeMillis() - fireworkTime;

        if (debugMode) {
            Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) " + playerName + " hit " + victim.getName() +
                    " | Delay: " + delay + "ms | Max: " + maxFireworkDelay + "ms");
        }

        if (delay <= maxFireworkDelay) {
            violationAlerts.reportViolation(playerName, "KomarA");

            if (debugMode) {
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) Violation reported for " + playerName + " (KomarA).");
            }

            int vl = violationAlerts.getViolationCount(playerName, "KomarA");
            if (vl == maxAlerts) {
                violationAlerts.executePunishment(playerName, "KomarA", punishmentCommand);
                discordHook.sendPunishmentCommand(playerName, punishmentCommand);


                if (debugMode) {
                    Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) Punishment executed for " + playerName);
                }
            }
        }
    }
}