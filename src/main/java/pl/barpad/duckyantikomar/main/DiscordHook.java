package pl.barpad.duckyantikomar.main;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import pl.barpad.duckyantikomar.Main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordHook {

    private final Main plugin;
    private String webhookUrl;
    private String username;
    private String avatarUrl;
    private String messageTemplate;
    private boolean discordEnabled;

    public DiscordHook(Main plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        discordEnabled = config.getBoolean("discord.enabled", false);
        webhookUrl = config.getString("discord.discord-webhook-url", "");
        username = config.getString("discord.username", "DuckyAntiKomar");
        avatarUrl = config.getString("discord.avatar-url", "https://i.imgur.com/wPfoYdI.png");
        messageTemplate = config.getString("discord.message-template", "**AntiKomarSystem!**\nPlayer: **%player%**\nCheck: **%check%**\nViolation: **%vl%**");
    }

    public void sendViolationAlert(String playerName, String checkType, int violationCount) {
        if (!discordEnabled) return;
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            Bukkit.getLogger().warning("[DuckyAntiKomar] Webhook URL is not set in config.yml!");
            return;
        }

        String content = messageTemplate
                .replace("%player%", playerName)
                .replace("%check%", checkType)
                .replace("%vl%", String.valueOf(violationCount));

        String jsonPayload = String.format(
                "{ \"username\": \"%s\", \"avatar_url\": \"%s\", \"embeds\": [ { " +
                        "\"title\": \"🚨 AntiKomar System Alert 🚨\", " +
                        "\"description\": \"%s\", " +
                        "\"color\": 16711680, " +
                        "\"footer\": { \"text\": \"DuckyAntiKomar\" }, " +
                        "\"timestamp\": \"%s\" } ] }",
                escapeJson(username), escapeJson(avatarUrl), escapeJson(content), getCurrentTimestamp()
        );

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sendDiscordMessage(jsonPayload));
    }

    private void sendDiscordMessage(String jsonPayload) {
        if (!discordEnabled) return;
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "DuckyAntiKomarWebhookClient");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 204) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Bukkit.getLogger().warning("[DuckyAntiKomar] Discord error response: " + response.toString());
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().severe("[DuckyAntiKomar] Error sending webhook: " + e.getMessage());
        }
    }

    private String getCurrentTimestamp() {
        return java.time.OffsetDateTime.now().toString();
    }

    private String escapeJson(String input) {
        return input.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}