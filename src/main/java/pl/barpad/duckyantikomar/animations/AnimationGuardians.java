package pl.barpad.duckyantikomar.animations;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.barpad.duckyantikomar.Main;

import java.util.ArrayList;
import java.util.List;

public class AnimationGuardians {

    private final Main plugin;

    public AnimationGuardians(Main plugin) {
        this.plugin = plugin;
    }

    public void play(final Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        final List<Guardian> guardians = new ArrayList<>();
        final List<ArmorStand> armorStands = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            double angle = (Math.PI * 2 / 4) * i;
            double x = Math.cos(angle) * 2.5;
            double z = Math.sin(angle) * 2.5;

            Location spawnLoc = player.getLocation().clone().add(x, 1.5, z);

            ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setInvulnerable(true);
            stand.setSmall(true);
            stand.setMarker(true);

            Guardian guardian = (Guardian) player.getWorld().spawnEntity(spawnLoc, EntityType.GUARDIAN);
            guardian.setAI(false);
            guardian.setInvulnerable(true);
            guardian.setGlowing(true);
            guardian.setSilent(true);

            guardians.add(guardian);
            armorStands.add(stand);
        }

        // Create the animation task
        BukkitRunnable animationTask = new BukkitRunnable() {
            int ticks = 0;
            double radius = 2.5;
            double height = 1.5;
            double anglePerTick = 0.1;

            @Override
            public void run() {
                // Stop conditions
                if (ticks >= 100 || player == null || !player.isOnline()) {
                    // Clean up
                    for (Guardian guardian : guardians) {
                        if (guardian != null && !guardian.isDead()) {
                            guardian.remove();
                        }
                    }
                    for (ArmorStand stand : armorStands) {
                        if (stand != null && !stand.isDead()) {
                            stand.remove();
                        }
                    }
                    guardians.clear();
                    armorStands.clear();
                    cancel();
                    return;
                }

                // Get current player location
                Location playerLoc = player.getLocation();

                // Calculate base rotation angle
                double baseAngle = ticks * anglePerTick;

                // Update each guardian and armor stand position
                for (int i = 0; i < armorStands.size(); i++) {
                    ArmorStand stand = armorStands.get(i);
                    Guardian guardian = guardians.get(i);

                    if (stand == null || stand.isDead() || guardian == null || guardian.isDead()) continue;

                    // Calculate position in circle around player
                    double angle = baseAngle + ((Math.PI * 2) / armorStands.size() * i);
                    double x = Math.cos(angle) * radius;
                    double z = Math.sin(angle) * radius;

                    Location newLoc = playerLoc.clone().add(x, height, z);

                    Vector lookDir = playerLoc.clone().add(0, 1, 0).toVector()
                            .subtract(newLoc.toVector()).normalize();
                    newLoc.setDirection(lookDir);

                    stand.teleport(newLoc);
                    guardian.teleport(newLoc);

                    player.getWorld().spawnParticle(
                            Particle.SOUL_FIRE_FLAME,
                            newLoc.clone().add(0, 0.5, 0),
                            2, 0.1, 0.1, 0.1, 0.01
                    );
                }

                for (int i = 0; i < 8; i++) {
                    double particleAngle = (Math.PI * 2 / 8) * i + (ticks * 0.1);
                    double particleX = Math.cos(particleAngle) * radius;
                    double particleZ = Math.sin(particleAngle) * radius;

                    Location particleLoc = playerLoc.clone().add(particleX, height, particleZ);
                    player.getWorld().spawnParticle(
                            Particle.END_ROD,
                            particleLoc,
                            1, 0, 0, 0, 0
                    );
                }

                if (ticks % 10 == 0) {
                    player.playSound(playerLoc, Sound.BLOCK_ANVIL_PLACE, 1.0f, 0.5f);
                }

                ticks++;
            }
        };

        animationTask.runTaskTimer(plugin, 0L, 1L);
    }
}