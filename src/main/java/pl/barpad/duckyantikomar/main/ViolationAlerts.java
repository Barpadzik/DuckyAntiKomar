package pl.barpad.duckyantikomar.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.animations.AnimationsManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ViolationAlerts {

    private final Main plugin;
    private final DiscordHook discordHook;
    private final AnimationsManager animationsManager;
    private final HashMap<String, Integer> violations = new HashMap<>();
    private final HashMap<String, Long> lastViolationTime = new HashMap<>();

    private final ConfigManager configManager;

    public ViolationAlerts(Main plugin, DiscordHook discordHook, AnimationsManager animationsManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.discordHook = discordHook;
        this.animationsManager = animationsManager;
        this.configManager = configManager;
        startCleanupTask();
    }

    public void reportViolation(String playerName, String checkType) {
        String key = playerName + ":" + checkType;
        int count = violations.getOrDefault(key, 0) + 1;
        violations.put(key, count);
        lastViolationTime.put(key, System.currentTimeMillis());

        String message = configManager.getAlertMessage().replace("%player%", playerName)
                .replace("%check%", checkType)
                .replace("%vl%", String.valueOf(count));

        Bukkit.getConsoleSender().sendMessage(message);
        discordHook.sendViolationAlert(playerName, checkType, count);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("antikomar.alerts")) {
                player.sendMessage(color(message));
            }
        }
    }

    public void executePunishment(String playerName, String check, String command) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return;
        }

        if (animationsManager.isAnimationEnabled(check)) {
            try {
                animationsManager.playAnimation(player, check);

                String animationName = animationsManager.getAnimationType(check).name();

                discordHook.sendAnimationPlay(playerName, animationName);

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", playerName));
                    violations.entrySet().removeIf(entry -> entry.getKey().startsWith(playerName + ":"));
                    lastViolationTime.entrySet().removeIf(entry -> entry.getKey().startsWith(playerName + ":"));
                    removeOldViolations();
                }, 100L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", playerName));
                violations.entrySet().removeIf(entry -> entry.getKey().startsWith(playerName + ":"));
                lastViolationTime.entrySet().removeIf(entry -> entry.getKey().startsWith(playerName + ":"));
                removeOldViolations();
            });
        }
    }

    public int getViolationCount(String playerName, String checkType) {
        String key = playerName + ":" + checkType;
        return violations.getOrDefault(key, 0);
    }

    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::removeOldViolations, 1200L, 1200L);
    }

    public void clearAllViolations() {
        violations.clear();
        lastViolationTime.clear();
    }

    private String color(String message) {
        return message == null ? "" : message.replace("&", "§");
    }

    private void removeOldViolations() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = lastViolationTime.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (currentTime - entry.getValue() > configManager.getAlertTimeoutSeconds()) {
                violations.remove(entry.getKey());
                iterator.remove();
            }
        }
    }
}