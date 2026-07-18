package net.rodald.director.camera;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public record Camera(CameraContext cameraContext) {
    public Camera(@NotNull String id, @NotNull Location startLocation) {
        this(id, startLocation, CameraProfile.DEFAULT);
    }

    public Camera(@NotNull String id, @NotNull Location startLocation, @NotNull CameraProfile cameraProfile) {
        this(new CameraContext(id, cameraProfile, startLocation, new AtomicReference<>(), new ArrayList<>()));
    }

    public Camera(@NotNull CameraContext cameraContext) {
        this.cameraContext = cameraContext;
    }

    /**
     * Teleports the camera entity to the specified location.
     * Also synchronizes all viewers so they can't leave the camera
     *
     * @param location The location to teleport the camera entity to
     */
    public void teleport(@NotNull Location location) {
        if (!isSpawned()) return;

        // thead safety & optimization
        ItemDisplay entity = getEntity();
        entity.teleport(location);

        for (Player player : cameraContext.viewers()) {
            if (player.isOnline()) {
                player.setGameMode(GameMode.SPECTATOR);
                player.setSpectatorTarget(entity);
            }
        }
    }

    /**
     * Spawns the camera entity at the start location.
     */
    public void spawn(final float yaw, final float pitch) {
        if (isSpawned()) return;
        cameraContext.startLocation().setRotation(yaw, pitch);
        setEntity(cameraContext.startLocation().getWorld().spawn(cameraContext.startLocation(), ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(new ItemStack(Material.SPYGLASS));
            itemDisplay.setTeleportDuration(cameraContext.cameraProfile().pathStabilization());

            Transformation transformation = new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf().rotationX((float) Math.toRadians(-90)),
                    new Vector3f(2, 2, 2),
                    new Quaternionf()
            );

            itemDisplay.setTransformation(transformation);
        }));
    }

    /**
     * Destroys the camera entity.
     */
    public void destroy() {
        if (getEntity() != null) {
            for (Player player : cameraContext.viewers()) {
                player.setSpectatorTarget(null);
            }
            getEntity().remove();
            setEntity(null);
        }
    }

    /**
     * Returns whether the camera entity is currently spawned.
     */
    public boolean isSpawned() {
        return getEntity() != null && getEntity().isValid();
    }

    public @NotNull String getId() {
        return cameraContext.id();
    }

    public @NotNull CameraProfile getCameraProfile() {
        return cameraContext.cameraProfile();
    }

    /**
     * Returns the current location of the camera entity.
     * Returns the start location if the entity is not spawned.
     */
    public @NotNull Location getLocation() {
        return isSpawned() ? getEntity().getLocation() : cameraContext.startLocation().clone();
    }

    public void addViewer(@NotNull Player player) {
        if (!cameraContext.viewers().contains(player)) {
            cameraContext.viewers().add(player);
        }
    }

    /**
     * Removes a viewer from this camera and restores their gamemode.
     */
    public void removeViewer(@NotNull Player player) {
        if (cameraContext.viewers().remove(player)) {
            player.setSpectatorTarget(null);
        }
    }

    public @NotNull List<Player> getViewers() {
        return Collections.unmodifiableList(cameraContext.viewers());
    }

    public @Nullable ItemDisplay getEntity() {
        return cameraContext.entity().get();
    }

    public void setEntity(@Nullable ItemDisplay entity) {
        cameraContext.entity().set(entity);
    }
}