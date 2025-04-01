package pl.barpad.duckyantikomar.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.checks.FireworkHitDelay;
import pl.barpad.duckyantikomar.checks.ElytraCriticals;
import pl.barpad.duckyantikomar.checks.AboveMaxSpeed;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Reload extends AbstractCommand {

    private final Main plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;
    private final FireworkHitDelay fireworkHitDelay;
    private final ElytraCriticals elytraCriticals;

    public Reload(Main plugin, FireworkHitDelay fireworkHitDelay, ElytraCriticals elytraCriticals) {
        super("antikomar", "Plugin Reload", "/antikomar reload", "§f§l≫ §cUnknown Command");
        this.plugin = plugin;
        this.fireworkHitDelay = fireworkHitDelay;
        this.elytraCriticals = elytraCriticals;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (!sender.hasPermission("antikomar.reload")) {
            sender.sendMessage(getMessage("no-permission"));
            return false;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            reloadMessages();

            sender.sendMessage(getMessage("config-reloaded"));
            sender.sendMessage(getMessage("plugin-reloaded"));
            return true;
        }

        sender.sendMessage(getMessage("incorrect-usage"));
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String @NotNull [] args) {
        if (sender.hasPermission("antikomar.reload")) {
            if (args.length == 1) {
                return Collections.singletonList("reload");
            }
        }
        return Collections.emptyList();
    }
}