package pl.barpad.duckyantikomar.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.barpad.duckyantikomar.Main;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Reload extends AbstractCommand {

    private final Main plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private final ConfigManager configManager;
    private final ViolationAlerts violationAlerts;

    public Reload(Main plugin, ConfigManager configManager, ViolationAlerts violationAlerts) {
        super("duckyantikomar", "Plugin Reload", "/duckyantikomar reload", "§f§l≫ §cUnknown Command");
        this.plugin = plugin;
        this.configManager = configManager;
        this.violationAlerts = violationAlerts;
        this.register();
        loadMessages();
    }

    private void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private String getMessage(String key) {
        return messagesConfig.getString(key, "§c[DuckyAntiKomar] Missing message: " + key).replace("&", "§");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!sender.hasPermission("antikomar.reload")) {
            sender.sendMessage(color(getMessage("no-permission")));
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            configManager.reload();
            reloadMessages();
            violationAlerts.clearAllViolations();

            sender.sendMessage(color(getMessage("config-reloaded")));
            sender.sendMessage(color(getMessage("plugin-reloaded")));
            return true;
        }

        sender.sendMessage(color(getMessage("incorrect-usage")));
        return false;
    }

    private String color(String message) {
        return message == null ? "" : message.replace("&", "§");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String [] args) {
        if (sender.hasPermission("antikomar.reload")) {
            if (args.length == 1) {
                return Collections.singletonList("reload");
            }
        }
        return Collections.emptyList();
    }
}