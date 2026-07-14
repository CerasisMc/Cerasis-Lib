package net.rodald.director.camera;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Camera {
    private final String id;
    private CameraProfile cameraProfile;
    private final Location startLocation;
    private Interaction entity;
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
     * Also synchronizes all viewers to this entity in a 1-tick loop so they cant leave camera
     *
     * @param location The location to teleport the camera entity to
     * @return True if the teleport was successful, false if the entity is not spawned
     */
    public boolean teleport(@NotNull Location location) {
        if (entity == null) return false;
        boolean success = entity.teleport(location);

        for (Player player : viewers) {
            if (player.isOnline() && player.getSpectatorTarget() != entity) {
                player.setSpectatorTarget(entity);
            }
        }

        return success;
    }

    /**
     * Spawns the camera entity at the start location.
     */
    public void spawn() {
        if (isSpawned()) return;
        this.entity = startLocation.getWorld().spawn(startLocation, Interaction.class, interaction -> {
            interaction.setInteractionHeight(0);
            interaction.setInteractionWidth(0);
        });
    }

    /**
     * Destroys the camera entity.
     */
    public void destroy() {
        if (entity != null) {
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
     * Starts the camera for all viewers. Spawns the entity if it isn't already spawned.
     */
    public void play() {
        if (!isSpawned()) {
            this.spawn();
        }

        for (Player player : viewers) {
            player.setGameMode(GameMode.SPECTATOR);
            player.setSpectatorTarget(entity);
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
}