package net.rodald.director.camera;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@SerializableAs("Camera")
public record CameraContext(String id, @NotNull CameraProfile cameraProfile, Location startLocation,
                            AtomicReference<ItemDisplay> entity,
                            List<Player> viewers) implements ConfigurationSerializable {

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("id", this.id);
        data.put("startLocation", this.startLocation);
        data.put("cameraProfile", this.cameraProfile);

        return data;
    }

    public static Camera deserialize(@NotNull Map<String, Object> args) {
        return new Camera(
                (String) args.get("id"),
                (Location) args.get("startLocation"),
                (CameraProfile) args.get("cameraProfile")
        );
    }
}