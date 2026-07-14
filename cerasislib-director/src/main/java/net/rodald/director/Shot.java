package net.rodald.director;

import net.rodald.director.camera.Camera;
import net.rodald.director.interpolate.KeyFrame;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class Shot {

    private final String id;
    private final Camera camera;
    private final List<KeyFrame> keyFrames = new ArrayList<>();
    private BukkitTask task;

    public Shot(String id, Camera camera) {
        this.id = id;
        this.camera = camera;
    }

    public Shot addKeyFrame(KeyFrame keyFrame) {
        keyFrames.add(keyFrame);
        return this;
    }

    public List<KeyFrame> getKeyFrames() {
        return keyFrames;
    }

    public Camera getCamera() {
        return camera;
    }

    /**
     * Returns the total duration of this shot in ticks.
     * Returns 0 if there are no keyframes.
     */
    public int getDuration() {
        if (keyFrames.isEmpty()) return 0;
        return keyFrames.getLast().tick();
    }

//    /**
//     * Plays this shot. Resets state and starts the animation from the beginning.
//     */
//    public void play() {
//        if (isPlaying()) {
//            task.cancel();
//        }
//
//        final int[] currentNodeIndex = {0};
//        final int[] tickInNode = {0};
//
//        task = new BukkitRunnable() {
//            @Override
//            public void run() {
//                if (keyFrames.isEmpty()) {
//                    this.cancel();
//                    return;
//                }
//
//                KeyFrame start = keyFrames.get(currentNodeIndex[0]);
//
//                // Trigger events and start camera on the first tick of each keyframe
//                if (tickInNode[0] == 0) {
//                    if (currentNodeIndex[0] == 0) {
//                        camera.play();
//                        Bukkit.getServer().broadcast(Component.text("Shot " + id + " started!"));
//                    }
//                    for (CutsceneEvent cutsceneEvent : start.events()) {
//                        camera.getViewers().forEach(cutsceneEvent::trigger);
//                    }
//                }
//
//                // Cancel animation once the last keyframe is reached
//                if (currentNodeIndex[0] >= keyFrames.size() - 1) {
//                    this.cancel();
//                    return;
//                }
//
//                KeyFrame end = keyFrames.get(currentNodeIndex[0] + 1);
//
//                int duration = end.tick() - start.tick();
//                double t = duration > 0 ? (double) tickInNode[0] / duration : 1.0;
//                double eased = end.easing().apply(t);
//
//                Location from = start.location();
//                Location to = end.location();
//
//                camera.teleport(interpolate(from, to, eased));
//
//                tickInNode[0]++;
//                if (tickInNode[0] >= duration) {
//                    currentNodeIndex[0]++;
//                    tickInNode[0] = 0;
//                }
//            }
//        }.runTaskTimer(DirectorLibService.getInstance(), 0L, 1L);
//    }

    /**
     * Stops this shot if it is currently playing.
     */
    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
            task = null;
        }
    }

    /**
     * Returns whether this shot is currently playing.
     */
    public boolean isPlaying() {
        return task != null && !task.isCancelled();
    }

    private Location interpolate(Location from, Location to, double t) {
        double x = from.getX() + (to.getX() - from.getX()) * t;
        double y = from.getY() + (to.getY() - from.getY()) * t;
        double z = from.getZ() + (to.getZ() - from.getZ()) * t;
        float yaw = (float) (from.getYaw() + (to.getYaw() - from.getYaw()) * t);
        float pitch = (float) (from.getPitch() + (to.getPitch() - from.getPitch()) * t);
        return new Location(from.getWorld(), x, y, z, yaw, pitch);
    }
}
