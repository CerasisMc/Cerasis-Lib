package net.rodald.director;

import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import net.rodald.director.camera.Camera;
import net.rodald.director.interpolate.CutsceneEvent;
import net.rodald.director.interpolate.KeyFrame;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    private class PlaybackSession implements Listener {

        private final Timeline timeline;
        private final String sessionId;
        private BukkitTask task;

        private int currentSceneIndex = 0;
        private int currentShotIndex = 0;
        private int currentTick = 0;

        private Camera activeCamera;

        public PlaybackSession(Timeline timeline, String sessionId) {
            this.timeline = timeline;
            this.sessionId = sessionId;

            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }

        @EventHandler
        private void onPlayerStopSpectatingEntityEvent(PlayerStopSpectatingEntityEvent playerStopSpectatingEntityEvent) {
            if (isActive(sessionId)) {
                playerStopSpectatingEntityEvent.setCancelled(true);
            }
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
            Scene currentScene = timeline.getScenes().get(currentSceneIndex);
            Shot activeShot = currentScene.getShots().get(currentShotIndex);



            List<KeyFrame> keyFrames = activeShot.getKeyFrames();

            // update camera
            if (activeCamera != activeShot.getCamera()) {

                if (activeCamera != null) {
                    activeCamera.destroy();
                }
                activeCamera = activeShot.getCamera();
                activeCamera.spawn();
                Location startFrame = keyFrames.getFirst().location();
                activeCamera.getEntity().setRotation(startFrame.getYaw(), startFrame.getPitch());
            }

            KeyFrame startFrame = null;
            KeyFrame endFrame = null;

            for (int i = 0; i < keyFrames.size(); i++) {
                KeyFrame current = keyFrames.get(i);
                KeyFrame next = (i + 1 < keyFrames.size()) ? keyFrames.get(i + 1) : null;

                // also run event if its on the last frame
                boolean isInCurrentFrame = (next == null)
                        ? (currentTick == current.tick())
                        : (currentTick >= current.tick() && currentTick < next.tick());

                if (isInCurrentFrame) {
                    if (next != null) {
                        startFrame = current;
                        endFrame = next;
                    }

                    if (currentTick == current.tick()) {
                        for (CutsceneEvent event : current.events()) {
                            event.trigger(activeCamera.getViewers());
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

            if (currentTick >= activeShot.getDuration()) {
                currentShotIndex++;
                currentTick = 0;
            }

            if (currentShotIndex >= currentScene.getShots().size()) {
                currentSceneIndex++;
                currentShotIndex = 0;
            }

            if (currentSceneIndex >= timeline.getScenes().size()) {

                // destroy camera here bc other destroy method won't be called if return false
                activeCamera.destroy();
                return false;
            }

            currentTick++;
            return true;
        }

        /**
         * Interpolates between two locations
         */
        private Location interpolate(Location from, Location to, double t) {
            double x = from.getX() + (to.getX() - from.getX()) * t;
            double y = from.getY() + (to.getY() - from.getY()) * t;
            double z = from.getZ() + (to.getZ() - from.getZ()) * t;
            float yaw = (float) (from.getYaw() + (to.getYaw() - from.getYaw()) * t);
            float pitch = (float) (from.getPitch() + (to.getPitch() - from.getPitch()) * t);
            return new Location(from.getWorld(), x, y, z, yaw, pitch);
        }

        /**
         * Stops the playback session and cleans up resources.
         */
        public void stop() {
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }

            activeSessions.remove(sessionId);
        }
    }
}