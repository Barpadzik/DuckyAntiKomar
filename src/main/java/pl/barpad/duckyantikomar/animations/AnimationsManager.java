package pl.barpad.duckyantikomar.animations;

import org.bukkit.entity.Player;
import pl.barpad.duckyantikomar.Main;
import pl.barpad.duckyantikomar.main.ConfigManager;

import java.util.*;

public class AnimationsManager {

    public enum AnimationType {
        EXPLOSION, SCARY, THUNDERBOLT, GUARDIANS, LAVA, RANDOM, NONE
    }

    private final Main plugin;
    private final ConfigManager configManager;
    private final AnimationThunderBolt animationThunderBolt;
    private final AnimationScary animationScary;
    private final AnimationExplosion animationExplosion;
    private final AnimationGuardians animationGuardians;
    private final AnimationLava animationLava;
    private final Random random;

    private final Map<UUID, Long> animationCooldowns = new HashMap<>();

    public AnimationsManager(Main plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.animationThunderBolt = new AnimationThunderBolt(plugin);
        this.animationScary = new AnimationScary(plugin);
        this.animationExplosion = new AnimationExplosion(plugin);
        this.animationGuardians = new AnimationGuardians(plugin);
        this.animationLava = new AnimationLava(plugin);
        this.random = new Random();
    }

    public boolean isAnimationEnabled(String check) {
        return configManager.getBoolean(check + "-Animation-Enable", false);
    }

    public AnimationType getAnimationType(String check) {
        try {
            return AnimationType.valueOf(configManager.getString(check + "-Animation-Type", "NONE").toUpperCase());
        } catch (IllegalArgumentException e) {
            return AnimationType.NONE;
        }
    }

    public void playAnimation(Player player, String check) {
        if (!isAnimationEnabled(check)) return;

        if (isInCooldown(player)) {
            if (configManager.isDebug()) {
                plugin.getLogger().info("Blocked animation for " + player.getName() + " (cooldown active)");
            }
            return;
        }

        AnimationType type = getAnimationType(check);

        try {
            if (type == AnimationType.RANDOM) {
                playRandomAnimation(player);
                return;
            }

            switch (type) {
                case THUNDERBOLT:
                    animationThunderBolt.play(player);
                    break;
                case SCARY:
                    animationScary.play(player);
                    break;
                case EXPLOSION:
                    animationExplosion.play(player);
                    break;
                case GUARDIANS:
                    animationGuardians.play(player);
                    break;
                case LAVA:
                    animationLava.startLavaAnimation(player);
                    break;
                default:
                    return;
            }

            updateCooldown(player);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to play animation for check: " + check);
            e.printStackTrace();
        }
    }

    private void playRandomAnimation(Player player) {
        if (isInCooldown(player)) {
            if (configManager.isDebug()) {
                plugin.getLogger().info("Blocked random animation for " + player.getName() + " (cooldown active)");
            }
            return;
        }

        List<AnimationType> availableAnimations = Arrays.asList(
                AnimationType.THUNDERBOLT,
                AnimationType.SCARY,
                AnimationType.EXPLOSION,
                AnimationType.GUARDIANS,
                AnimationType.LAVA
        );

        AnimationType randomType = availableAnimations.get(random.nextInt(availableAnimations.size()));

        switch (randomType) {
            case SCARY:
                animationScary.play(player);
                break;
            case EXPLOSION:
                animationExplosion.play(player);
                break;
            case GUARDIANS:
                animationGuardians.play(player);
                break;
            case LAVA:
                animationLava.startLavaAnimation(player);
                break;
            default:
                animationThunderBolt.play(player);
        }

        updateCooldown(player);

        if (configManager.isDebug()) {
            plugin.getLogger().info("Random animation selected: " + randomType.name() + " for player " + player.getName());
        }
    }

    private boolean isInCooldown(Player player) {
        long now = System.currentTimeMillis();
        long ANIMATION_COOLDOWN_MILLIS = 10 * 1000;
        return animationCooldowns.getOrDefault(player.getUniqueId(), 0L) + ANIMATION_COOLDOWN_MILLIS > now;
    }

    private void updateCooldown(Player player) {
        animationCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}