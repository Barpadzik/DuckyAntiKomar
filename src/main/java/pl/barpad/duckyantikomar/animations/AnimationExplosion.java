package pl.barpad.duckyantikomar.animations;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.barpad.duckyantikomar.Main;

public class AnimationExplosion {

    private final Main plugin;

    public AnimationExplosion(Main plugin) {
        this.plugin = plugin;
    }

    public void play(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

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
                        Particle.EXPLOSION,
                        currentLoc.getX(), currentLoc.getY(), currentLoc.getZ(),
                        10,
                        0.5, 0.5, 0.5,
                        0.1
                );

                player.playSound(currentLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.7f);

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}