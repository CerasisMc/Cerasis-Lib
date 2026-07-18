package net.rodald.director;

import net.rodald.director.camera.Camera;
import net.rodald.director.camera.CameraContext;
import net.rodald.director.interpolate.KeyFrame;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("Shot")
public record Shot(String id, Camera camera, List<KeyFrame> keyFrames) implements ConfigurationSerializable {

    public Shot(String id, Camera camera) {
        this(id, camera, new ArrayList<>());
    }

    public void addKeyFrame(KeyFrame keyFrame) {
        keyFrames.add(keyFrame);
    }

    /**
     * Returns the total duration of this shot in ticks.
     * Returns 0 if there are no keyframes.
     */
    public long getDuration() {
        if (keyFrames.isEmpty()) return 0;
        return keyFrames.getLast().tick();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("id", id);
        data.put("camera", camera.cameraContext());
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

        CameraContext cameraContext = (CameraContext) args.get("camera");

        return new Shot(
                (String) args.get("id"),
                new Camera(cameraContext),
                keyFrames
        );
    }
}