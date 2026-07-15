package net.rodald.director.camera;

import org.checkerframework.common.value.qual.IntRange;

public record CameraProfile(String id, double shakeIntensity, double shakeMoveMultiplier, double handheldShakeIntensity,
                            int targetFov, @IntRange(from = 0, to = 59) int pathStabilization) {

    public static final CameraProfile DEFAULT = new CameraProfile("default", 0.0, 1.0, 0.0, 100, 10);

    // TODO: add screen effects https://minecraft.wiki/w/Screen_effects
}
