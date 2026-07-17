package net.rodald.director;

import net.rodald.director.camera.Camera;
import net.rodald.director.interpolate.KeyFrame;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("Shot")
public class Shot implements ConfigurationSerializable {

    private final String id;
    private final Camera camera;
    private final List<KeyFrame> keyFrames = new ArrayList<>();
    private BukkitTask task;

    public Shot(String id, Camera camera) {
        this.id = id;
        this.camera = camera;
    }

    public Shot(String id, Camera camera, List<KeyFrame> keyFrames) {
        this.id = id;
        this.camera = camera;
        this.keyFrames.addAll(keyFrames);
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
    public long getDuration() {
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

    public String getId() {
        return id;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("id", id);
        data.put("camera", camera);
        data.put("keyFrames", keyFrames);

        return data;
    }

    public static Shot deserialize(@NotNull Map<String, Object> args) {
        List<KeyFrame> keyFrames = new ArrayList<>();

        if (args.get("keyFrames") instanceof List<?> rawList) {
            for (Object obj : rawList) {
                keyFrames.add((KeyFrame) obj);
            }
        }

        return new Shot(
                (String) args.get("id"),
                (Camera) args.get("camera"),
                keyFrames
        );
    }
}
