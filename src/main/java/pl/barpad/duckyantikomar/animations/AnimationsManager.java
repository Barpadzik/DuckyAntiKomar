package pl.barpad.duckyantikomar.animations;

import org.bukkit.entity.Player;
import pl.barpad.duckyantikomar.Main;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class AnimationsManager {

    public enum AnimationType {
        EXPLOSION, SCARY, THUNDERBOLT, GUARDIANS, RANDOM, NONE
    }

    private final Main plugin;
    private final AnimationThunderBolt animationThunderBolt;
    private final AnimationScary animationScary;
    private final AnimationExplosion animationExplosion;
    private final AnimationGuardians animationGuardians;
    private final Random random;

    public AnimationsManager(Main plugin) {
        this.plugin = plugin;
        this.animationThunderBolt = new AnimationThunderBolt(plugin);
        this.animationScary = new AnimationScary(plugin);
        this.animationExplosion = new AnimationExplosion(plugin);
        this.animationGuardians = new AnimationGuardians(plugin);
        this.random = new Random();
    }

    public boolean isAnimationEnabled(String check) {
        return plugin.getConfig().getBoolean(check + "-Animation-Enable", false);
    }

    public AnimationType getAnimationType(String check) {
        try {
            return AnimationType.valueOf(plugin.getConfig().getString(check + "-Animation-Type", "NONE").toUpperCase());
        } catch (IllegalArgumentException e) {
            return AnimationType.NONE;
        }
    }

    public void playAnimation(Player player, String check) {
        if (!isAnimationEnabled(check)) {
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
                    return;
                case SCARY:
                    animationScary.play(player);
                    return;
                case EXPLOSION:
                    animationExplosion.play(player);
                    return;
                case GUARDIANS:
                    animationGuardians.play(player);
                    return;
                default:
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to play animation for check: " + check);
            e.printStackTrace();
        }
    }

    private void playRandomAnimation(Player player) {
        List<AnimationType> availableAnimations = new ArrayList<>();
        availableAnimations.add(AnimationType.THUNDERBOLT);
        availableAnimations.add(AnimationType.SCARY);
        availableAnimations.add(AnimationType.EXPLOSION);
        availableAnimations.add(AnimationType.GUARDIANS);

        AnimationType randomType = availableAnimations.get(random.nextInt(availableAnimations.size()));

        switch (randomType) {
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
            default:
                animationThunderBolt.play(player);
        }

        if (plugin.getConfig().getBoolean("Debug", false)) {
            plugin.getLogger().info("Random animation selected: " + randomType.name() + " for player " + player.getName());
        }
    }
}