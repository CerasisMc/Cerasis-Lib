package net.rodald.director.camera;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Camera {
    private final String id;
    private CameraProfile cameraProfile;
    private final Location startLocation;
    private ItemDisplay entity;
    private final List<Player> viewers = new ArrayList<>();

    public Camera(@NotNull String id, @NotNull Location startLocation) {
        this.id = id;
        this.startLocation = startLocation;
    }

    public Camera(@NotNull String id, @NotNull Location startLocation, @Nullable CameraProfile cameraProfile) {
        this.id = id;
        this.startLocation = startLocation;
        this.cameraProfile = cameraProfile;
    }

    /**
     * Teleports the camera entity to the specified location.
     * Also synchronizes all viewers so they cant leave camera
     *
     * @param location The location to teleport the camera entity to
     */
    public void teleport(@NotNull Location location) {
        entity.teleport(location);

        for (Player player : viewers) {
            if (player.isOnline()) {
                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(entity);
            }
        }

    }

    /**
     * Spawns the camera entity at the start location.
     */
    public void spawn() {
        if (isSpawned()) return;
        Bukkit.broadcastMessage("create camera");

        this.entity = startLocation.getWorld().spawn(startLocation, ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(new ItemStack(Material.SPYGLASS));
            itemDisplay.setTeleportDuration(59);
//            itemDisplay.setInterpolationDuration(1);

            Transformation transformation = new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf().rotationX((float) Math.toRadians(-90)),
                    new Vector3f(2, 2, 2),
                    new Quaternionf()
            );

            itemDisplay.setTransformation(transformation);
        });
    }

    /**
     * Destroys the camera entity.
     */
    public void destroy() {
        Bukkit.broadcastMessage("destroy camera");
        if (entity != null) {
            for (Player player : viewers) {
                player.setSpectatorTarget(null);
            }
            entity.remove();
            entity = null;
        }
    }

    /**
     * Returns whether the camera entity is currently spawned.
     */
    public boolean isSpawned() {
        return entity != null && entity.isValid();
    }

    public @NotNull String getId() {
        return id;
    }

    public @Nullable CameraProfile getCameraProfile() {
        return cameraProfile;
    }

    /**
     * Returns the current location of the camera entity.
     * Returns the start location if the entity is not spawned.
     */
    public @NotNull Location getLocation() {
        return entity != null ? entity.getLocation() : startLocation.clone();
    }

    public void setCameraProfile(@Nullable CameraProfile cameraProfile) {
        this.cameraProfile = cameraProfile;
    }

    /**
     * Teleports the camera entity to the given location.
     * Has no effect if the entity is not spawned.
     */
    public void setLocation(@NotNull Location location) {
        if (entity != null) {
            entity.teleport(location);
        }
    }

    public void addViewer(@NotNull Player player) {
        if (!viewers.contains(player)) {
            viewers.add(player);
        }
    }

    /**
     * Removes a viewer from this camera and restores their gamemode.
     */
    public void removeViewer(@NotNull Player player) {
        if (viewers.remove(player)) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setSpectatorTarget(null);
        }
    }

    public @NotNull List<Player> getViewers() {
        return Collections.unmodifiableList(viewers);
    }

    public ItemDisplay getEntity() {
        return entity;
    }
}