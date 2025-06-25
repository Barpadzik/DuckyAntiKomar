package pl.barpad.duckyantikomar.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.barpad.duckyantikomar.Main;

import java.util.Collections;
import java.util.List;

public class Reload extends AbstractCommand {

    private final Main plugin;
    private final ConfigManager configManager;
    private final ViolationAlerts violationAlerts;

    public Reload(Main plugin, ConfigManager configManager, ViolationAlerts violationAlerts) {
        super("duckyantikomar", "Plugin Reload", "/duckyantikomar reload", "§f§l≫ §cUnknown Command");
        this.plugin = plugin;
        this.configManager = configManager;
        this.violationAlerts = violationAlerts;
        this.register();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String [] args) {
        if (!sender.hasPermission("antikomar.reload")) {
            sender.sendMessage(color(configManager.getNoPermissionMessage()));
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            configManager.reload();
            violationAlerts.clearAllViolations();

            sender.sendMessage(color(configManager.getConfigReloadedMessage()));
            sender.sendMessage(color(configManager.getPluginReloadedMessage()));
            return true;
        }

        sender.sendMessage(color(configManager.getIncorrectUsageMessage()));
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