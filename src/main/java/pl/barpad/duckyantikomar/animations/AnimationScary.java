package pl.barpad.duckyantikomar.animations;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.barpad.duckyantikomar.Main;

public class AnimationScary {

    private final Main plugin;

    public AnimationScary(Main plugin) {
        this.plugin = plugin;
    }

    public void play(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 140, 1));

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 50 || !player.isOnline()) {
                    cancel();
                    return;
                }

                player.playSound(player.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1.0f, 0.8f);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 2L);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 100 || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location currentLoc = player.getLocation();

                player.getWorld().spawnParticle(
                        Particle.ELDER_GUARDIAN,
                        currentLoc.getX(), currentLoc.getY(), currentLoc.getZ(),
                        20,
                        0.1, 0.1, 0.1,
                        0.1
                );

                player.getWorld().spawnParticle(
                        Particle.SOUL,
                        currentLoc.getX(), currentLoc.getY(), currentLoc.getZ(),
                        15,
                        0.5, 0.5, 0.5,
                        0.1
                );

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}