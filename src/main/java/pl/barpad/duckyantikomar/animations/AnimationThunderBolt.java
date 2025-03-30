package pl.barpad.duckyantikomar.animations;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.barpad.duckyantikomar.Main;

public class AnimationThunderBolt {

    private final Main plugin;

    public AnimationThunderBolt(Main plugin) {
        this.plugin = plugin;
    }

    public void play(Player player) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 100 || !player.isOnline()) {
                    cancel();
                    return;
                }

                Location loc = player.getLocation();
                player.getWorld().strikeLightningEffect(loc);

                ticks += 1;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}