package net.rodald.display;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArmorStandParticle {

    private final World world;
    private final Location location;
    private final Component text;
    private final Vector offset;
    private final int count;
    private final float speed;
    private long lifetime;

    public ArmorStandParticle(World world, Location location, Component text, Vector offset, int count, float speed, long lifetime) {
        this.world = world;
        this.location = location;
        this.text = text;
        this.offset = offset;
        this.count = count;
        this.speed = speed;
        this.lifetime = lifetime;

        this.spawn();
    }

    private void spawn() {
        for (int i = 0; i < count; i++) {
            ArmorStand armorStand = world.spawn(
                    location.clone().add(generateRandomOffset(offset)),
                    ArmorStand.class, CreatureSpawnEvent.SpawnReason.CUSTOM,
                    entity -> {
                        entity.setInvisible(true);
                        entity.setCustomNameVisible(true);
                        entity.getAttribute(Attribute.SCALE).setBaseValue(0);
                        entity.customName(text);
                        entity.setVelocity(generateRandomMotion(-speed, speed));
                        entity.setNoPhysics(true);
                    }
            );

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (lifetime <= 0 || armorStand.isDead()) {
                        armorStand.remove();
                        cancel();
                        return;
                    }

                    lifetime--;
                }
            }.runTaskTimer(DisplayLibService.getInstance(), 0L, 1L);
        }
    }

    private Vector generateRandomMotion(double origin, double bound) {
        Random random = new Random();

        double x = random.nextDouble(origin, bound);
        double y = Math.abs(bound);
        double z = random.nextDouble(origin, bound);

        return new Vector(x, y, z);
    }

    private Vector generateRandomOffset(Vector offset) {
        Random random = new Random();

        double x = offset.getX() == 0 ? 0 : random.nextDouble(-Math.abs(offset.getX()), Math.abs(offset.getX()));
        double y = offset.getY() == 0 ? 0 : random.nextDouble(-Math.abs(offset.getY()), Math.abs(offset.getY()));
        double z = offset.getZ() == 0 ? 0 : random.nextDouble(-Math.abs(offset.getZ()), Math.abs(offset.getZ()));

        return new Vector(x, y, z);
    }
}