package net.rodald.director.camera;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.NumberConversions;
import org.checkerframework.common.value.qual.IntRange;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("CameraProfile")
public record CameraProfile(
        @NotNull String id,
        double shakeIntensity,
        double shakeMoveMultiplier,
        double handheldShakeIntensity,
        int targetFov,
        @IntRange(from = 0, to = 59) int pathStabilization
) implements ConfigurationSerializable {

    public static final CameraProfile DEFAULT = new CameraProfile("default", 0.0, 1.0, 0.0, 100, 10);

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();

        data.put("id", id);
        data.put("shakeIntensity", shakeIntensity);
        data.put("shakeMoveMultiplier", shakeMoveMultiplier);
        data.put("handheldShakeIntensity", handheldShakeIntensity);
        data.put("pathStabilization", pathStabilization);
        data.put("targetFov", targetFov);

        return data;
    }

    @NotNull
    public static CameraProfile deserialize(@NotNull Map<String, Object> args) {
        return new CameraProfile(
                (String) args.get("id"),
                NumberConversions.toDouble(args.get("shakeIntensity")),
                NumberConversions.toDouble(args.get("shakeMoveMultiplier")),
                NumberConversions.toDouble(args.get("handheldShakeIntensity")),
                NumberConversions.toInt(args.get("targetFov")),
                NumberConversions.toInt(args.get("pathStabilization"))
        );
    }

    // TODO: add screen effects https://minecraft.wiki/w/Screen_effects
}
