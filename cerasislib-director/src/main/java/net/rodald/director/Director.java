package net.rodald.director;

import net.rodald.director.camera.Camera;
import net.rodald.director.interpolate.CutsceneEvent;
import net.rodald.director.interpolate.KeyFrame;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Director {

    private final JavaPlugin plugin;
    private final Map<String, PlaybackSession> activeSessions = new HashMap<>();

    public Director(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts playback of the given timeline.
     * Returns the session ID for tracking.
     */
    public String direct(Timeline timeline) {
        String sessionId = timeline.getId() + "_" + System.currentTimeMillis();
        PlaybackSession session = new PlaybackSession(timeline, sessionId);
        activeSessions.put(sessionId, session);
        session.start();
        return sessionId;
    }

    /**
     * Stops the playback session by ID.
     */
    public void stop(String sessionId) {
        PlaybackSession session = activeSessions.remove(sessionId);
        if (session != null) {
            session.stop();
        }
    }

    /**
     * Checks if a session is currently active.
     */
    public boolean isActive(String sessionId) {
        return activeSessions.containsKey(sessionId);
    }

    /**
     * Internal playback session that manages the timeline execution.
     */
    private class PlaybackSession {

        private final Timeline timeline;
        private final String sessionId;
        private BukkitTask task;

        private int currentSceneIndex = 0;
        private int currentShotIndex = 0;
        private int currentTick = 0;

        private Camera activeCamera;
        private Shot activeShot;
        private boolean eventsTriggeredForCurrentShot = false;

        public PlaybackSession(Timeline timeline, String sessionId) {
            this.timeline = timeline;
            this.sessionId = sessionId;
        }

        public void start() {
            if (timeline.getScenes().isEmpty()) {
                activeSessions.remove(sessionId);
                return;
            }

            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!tick()) {
                        stop();
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }

        /**
         * Executes one tick of playback.
         * Returns false when the timeline is complete.
         */
        private boolean tick() {
            if (currentSceneIndex >= timeline.getScenes().size()) {
                return false;
            }

            Scene currentScene = timeline.getScenes().get(currentSceneIndex);

            if (currentShotIndex >= currentScene.getShots().size()) {
                currentSceneIndex++;
                currentShotIndex = 0;
                currentTick = 0;
                eventsTriggeredForCurrentShot = false;

                if (activeCamera != null) {
                    cleanupCamera(activeCamera);
                    activeCamera = null;
                }

                return currentSceneIndex < timeline.getScenes().size();
            }

            activeShot = currentScene.getShots().get(currentShotIndex);

            if (activeCamera != activeShot.getCamera()) {
                if (activeCamera != null) {
                    cleanupCamera(activeCamera);
                }
                activeCamera = activeShot.getCamera();
                initializeCamera(activeCamera);
                eventsTriggeredForCurrentShot = false;
                currentTick = 0;
            }

            List<KeyFrame> keyFrames = activeShot.getKeyFrames();

            if (keyFrames.isEmpty()) {
                currentShotIndex++;
                currentTick = 0;
                eventsTriggeredForCurrentShot = false;
                return true;
            }

            if (currentTick == 0 && !eventsTriggeredForCurrentShot) {
                KeyFrame firstFrame = keyFrames.get(0);
                for (CutsceneEvent event : firstFrame.events()) {
                    for (Player viewer : activeCamera.getViewers()) {
                        event.trigger(viewer);
                    }
                }
                eventsTriggeredForCurrentShot = true;
            }

            int shotDuration = activeShot.getDuration();

            if (currentTick >= shotDuration) {
                currentShotIndex++;
                currentTick = 0;
                eventsTriggeredForCurrentShot = false;
                return true;
            }

            KeyFrame startFrame = null;
            KeyFrame endFrame = null;

            for (int i = 0; i < keyFrames.size() - 1; i++) {
                KeyFrame current = keyFrames.get(i);
                KeyFrame next = keyFrames.get(i + 1);

                if (currentTick >= current.tick() && currentTick < next.tick()) {
                    startFrame = current;
                    endFrame = next;

                    if (currentTick == next.tick() - 1) {
                        for (CutsceneEvent event : next.events()) {
                            for (Player viewer : activeCamera.getViewers()) {
                                event.trigger(viewer);
                            }
                        }
                    }
                    break;
                }
            }

            if (startFrame != null && endFrame != null) {
                int duration = endFrame.tick() - startFrame.tick();
                int elapsed = currentTick - startFrame.tick();
                double t = duration > 0 ? (double) elapsed / duration : 1.0;
                double eased = endFrame.easing().apply(t);

                Location interpolated = interpolate(startFrame.location(), endFrame.location(), eased);
                activeCamera.teleport(interpolated);
            }

            currentTick++;
            return true;
        }

        /**
         * Initializes the camera for playback.
         */
        private void initializeCamera(Camera camera) {
            if (!camera.isSpawned()) {
                camera.spawn();
            }

            for (Player viewer : camera.getViewers()) {
                viewer.setGameMode(GameMode.SPECTATOR);
                viewer.setSpectatorTarget(camera.getLocation().getWorld().spawn(camera.getLocation(), org.bukkit.entity.Interaction.class));
            }

            camera.play();
        }

        /**
         * Cleans up the camera after playback.
         */
        private void cleanupCamera(Camera camera) {
            for (Player viewer : camera.getViewers()) {
                viewer.setGameMode(GameMode.SURVIVAL);
                viewer.setSpectatorTarget(null);
            }
        }

        /**
         * Interpolates between two locations with proper yaw wrapping fix.
         */
        private Location interpolate(Location from, Location to, double t) {
            double x = from.getX() + (to.getX() - from.getX()) * t;
            double y = from.getY() + (to.getY() - from.getY()) * t;
            double z = from.getZ() + (to.getZ() - from.getZ()) * t;

            float yawDiff = to.getYaw() - from.getYaw();
            while (yawDiff < -180) yawDiff += 360;
            while (yawDiff > 180) yawDiff -= 360;
            float yaw = from.getYaw() + yawDiff * (float) t;

            float pitchDiff = to.getPitch() - from.getPitch();
            while (pitchDiff < -180) pitchDiff += 360;
            while (pitchDiff > 180) pitchDiff -= 360;
            float pitch = from.getPitch() + pitchDiff * (float) t;

            return new Location(from.getWorld(), x, y, z, yaw, pitch);
        }

        /**
         * Stops the playback session and cleans up resources.
         */
        public void stop() {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }

            if (activeCamera != null) {
                cleanupCamera(activeCamera);
                activeCamera.destroy();
            }

            activeSessions.remove(sessionId);
        }
    }
}