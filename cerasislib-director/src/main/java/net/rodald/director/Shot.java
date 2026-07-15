package net.rodald.director;

import net.rodald.director.camera.Camera;
import net.rodald.director.interpolate.KeyFrame;
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
}
