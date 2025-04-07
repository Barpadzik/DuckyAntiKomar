package pl.barpad.duckyantikomar.animations;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.barpad.duckyantikomar.Main;

import java.util.Objects;

public class AnimationLava {

    private final Main plugin;

    public AnimationLava(Main plugin) {
        this.plugin = plugin;
    }

    public void startLavaAnimation(Player player) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 100 || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location loc = player.getLocation();
                spawnLavaParticles(loc);
                playLavaSound(player);

                ticks++;
            }
        }.runTaskTimer(plugin, 0, 2);
    }

    private void spawnLavaParticles(Location location) {
        for (int i = 0; i < 15; i++) {
            double x = (Math.random() - 0.5) * 2;
            double y = Math.random() * 1.5;
            double z = (Math.random() - 0.5) * 2;
            Objects.requireNonNull(location.getWorld()).spawnParticle(Particle.LAVA, location.clone().add(x, y, z), 1);
        }
    }

    private void playLavaSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1.2f, 1.8f);
    }
}