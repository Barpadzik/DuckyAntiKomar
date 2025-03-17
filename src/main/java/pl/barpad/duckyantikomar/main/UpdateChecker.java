package pl.barpad.duckyantikomar.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.barpad.duckyantikomar.Main;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    private final Main plugin;
    private FileConfiguration messagesConfig;
    private static final String GITHUB_API_URL = "https://api.github.com/repos/Barpadzik/DuckyAntiKomar/releases/latest ";

    public UpdateChecker(Main plugin) {
        this.plugin = plugin;
        loadMessagesConfig();
    }

    private void loadMessagesConfig() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(GITHUB_API_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                JSONParser parser = new JSONParser();
                JSONObject response = (JSONObject) parser.parse(new InputStreamReader(connection.getInputStream()));
                String latestVersion = (String) response.get("tag_name");
                String downloadUrl = (String) response.get("html_url");

                if (!plugin.getDescription().getVersion().equalsIgnoreCase(latestVersion)) {
                    String updateMessage = ChatColor.translateAlternateColorCodes('&',
                                    messagesConfig.getString("update-available", "§6§lANTIKOMAR §8» &eA new version is available: &c%version%"))
                            .replace("%version%", latestVersion);

                    String downloadMessage = ChatColor.translateAlternateColorCodes('&',
                                    messagesConfig.getString("update-download", "§6§lANTIKOMAR §8» &eDownload: &a%url%"))
                            .replace("%url%", downloadUrl);

                    Bukkit.getConsoleSender().sendMessage(updateMessage);
                    Bukkit.getConsoleSender().sendMessage(downloadMessage);

                    Bukkit.getOnlinePlayers().stream()
                            .filter(player -> player.hasPermission("antikomar.update"))
                            .forEach(player -> {
                                player.sendMessage(updateMessage);
                                player.sendMessage(downloadMessage);
                            });
                }
            } catch (Exception e) {
                String errorMessage = ChatColor.translateAlternateColorCodes('&',
                        messagesConfig.getString("update-check-failed", "§6§lANTIKOMAR §8» &cCould not check for updates."));
                Bukkit.getConsoleSender().sendMessage(errorMessage);
            }
        });
    }
}