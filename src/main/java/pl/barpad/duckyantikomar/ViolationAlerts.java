package pl.barpad.duckyantikomar;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ViolationAlerts {

    private final Main plugin;
    private final HashMap<String, Integer> violations = new HashMap<>();
    private final HashMap<String, Long> lastViolationTime = new HashMap<>();
    private FileConfiguration messagesConfig;
    private long alertTimeout;
    private int maxKomarAAlerts;
    private String komarACommand;
    private String komarBCommand;
    private String komarCCommand;

    public ViolationAlerts(Main plugin) {
        this.plugin = plugin;
        loadMessagesConfig();
        loadConfig();
        startCleanupTask();
    }

    private void loadMessagesConfig() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        messagesConfig.addDefault("alert-message", "§6§lANTIKOMAR §8»§f Player §7»§f %player% §7»§6 %check% §7(§c%vl%VL§7)");
        messagesConfig.options().copyDefaults(true);
        saveMessagesConfig();
    }

    private void saveMessagesConfig() {
        try {
            messagesConfig.save(new File(plugin.getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            Bukkit.getLogger().severe("§6§lANTIKOMAR §8»§c Could not save messages.yml!");
        }
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        alertTimeout = config.getLong("alert-timeout", 300) * 1000;
        maxKomarAAlerts = config.getInt("Max-KomarA-Alerts", 5);
        komarACommand = config.getString("KomarA-Command", "ban %player% AntiKomarSystem [KomarA]");
        komarBCommand = config.getString("KomarB-Command", "ban %player% AntiKomarSystem [KomarB]");
        komarCCommand = config.getString("KomarC-Command", "ban %player% AntiKomarSystem [KomarC]");
    }

    public void reportViolation(String playerName, String checkType) {
        String key = playerName + ":" + checkType;
        int count = violations.getOrDefault(key, 0) + 1;
        violations.put(key, count);
        lastViolationTime.put(key, System.currentTimeMillis());

        String messageTemplate = messagesConfig.getString("alert-message", "§6§lANTIKOMAR §8»§f Player §7»§f %player% §7»§6 %check% §7(§c%vl%VL§7)");
        String message = messageTemplate.replace("%player%", playerName)
                .replace("%check%", checkType)
                .replace("%vl%", String.valueOf(count));

        Bukkit.getConsoleSender().sendMessage(message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("antikomar.alerts")) {
                player.sendMessage(message);
            }
        }

        if (checkType.equalsIgnoreCase("KomarA") && count > maxKomarAAlerts) {
            executePunishment(playerName, komarACommand);
        }
    }

    public void executePunishment(String playerName, String command) {
        String formattedCommand = command.replace("%player%", playerName);
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formattedCommand));
        violations.entrySet().removeIf(entry -> entry.getKey().startsWith(playerName + ":"));
        lastViolationTime.entrySet().removeIf(entry -> entry.getKey().startsWith(playerName + ":"));
    }

    public int getViolationCount(String playerName, String checkType) {
        String key = playerName + ":" + checkType;
        return violations.getOrDefault(key, 0);
    }

    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::removeOldViolations, 1200L, 1200L);
    }

    private void removeOldViolations() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = lastViolationTime.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (currentTime - entry.getValue() > alertTimeout) {
                violations.remove(entry.getKey());
                iterator.remove();
            }
        }
    }
}