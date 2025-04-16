package pl.barpad.duckyantikomar.checks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.main.ConfigManager;
import pl.barpad.duckyantikomar.main.DiscordHook;
import pl.barpad.duckyantikomar.main.ViolationAlerts;

import java.util.HashMap;
import java.util.UUID;

public class FireworkHitDelay implements Listener {

    private final ConfigManager configManager;
    private final ViolationAlerts violationAlerts;
    private final DiscordHook discordHook;

    private final HashMap<UUID, Long> fireworkUsageTimes = new HashMap<>();

    public FireworkHitDelay(Main plugin, ConfigManager configManager, ViolationAlerts violationAlerts, DiscordHook discordHook) {
        this.configManager = configManager;
        this.violationAlerts = violationAlerts;
        this.discordHook = discordHook;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFireworkUse(PlayerInteractEvent event) {
        if (!configManager.isKomarAEnabled()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.FIREWORK_ROCKET && player.isGliding()) {
            fireworkUsageTimes.put(player.getUniqueId(), System.currentTimeMillis());

            if (configManager.isKomarADebugMode()) {
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) " + player.getName() + " used a firework while gliding.");
            }
        }
    }

    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!configManager.isKomarAEnabled()) return;

        if (!(event.getDamager() instanceof Player damager) || !(event.getEntity() instanceof Player victim)) return;

        UUID damagerUUID = damager.getUniqueId();
        String playerName = damager.getName();

        Long fireworkTime = fireworkUsageTimes.remove(damagerUUID);

        if (fireworkTime == null) return;

        long delay = System.currentTimeMillis() - fireworkTime;

        if (configManager.isKomarADebugMode()) {
            Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) " + playerName + " hit " + victim.getName() +
                    " | Delay: " + delay + "ms | Max: " + configManager.getMaxFireworkDelay() + "ms");
        }

        if (delay <= configManager.getMaxFireworkDelay()) {
            violationAlerts.reportViolation(playerName, "KomarA");

            if (configManager.isKomarADebugMode()) {
                Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) Violation reported for " + playerName + " (KomarA).");
            }

            int vl = violationAlerts.getViolationCount(playerName, "KomarA");
            if (vl == configManager.getMaxKomarAAlerts()) {
                String punishmentCommand = configManager.getKomarACommand();

                violationAlerts.executePunishment(playerName, "KomarA", punishmentCommand);
                discordHook.sendPunishmentCommand(playerName, punishmentCommand);

                if (configManager.isKomarADebugMode()) {
                    Bukkit.getLogger().info("[DuckyAntiKomar] (KomarA Debug) Punishment executed for " + playerName);
                }
            }
        }
    }
}