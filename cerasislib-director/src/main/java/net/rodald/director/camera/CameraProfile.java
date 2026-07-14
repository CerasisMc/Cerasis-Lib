package net.rodald.director.camera;

public record CameraProfile(String id, double shakeIntensity, double shakeMoveMultiplier, double handheldShakeIntensity,
                            int targetFov) {
    // TODO: add screen effects https://minecraft.wiki/w/Screen_effects

}
