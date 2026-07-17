package net.rodald.director.interpolate;

import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("Easing")
public enum EasingType {
    LINEAR(t -> t),
    EASE_IN_QUAD(t -> t * t),
    EASE_OUT_QUAD(t -> t * (2 - t)),
    EASE_IN_OUT_QUAD(t -> t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t),
    EASE_IN_CUBIC(t -> t * t * t),
    EASE_OUT_CUBIC(t -> {
        double v = t - 1;
        return v * v * v + 1;
    }),
    EASE_IN_OUT_CUBIC(t -> t < 0.5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1),
    STEP(t -> t < 1 ? 0 : 1),
    SMOOTH(t -> (1 - Math.cos(t * Math.PI)) / 2),
    NONE(t -> 0.0);

    private final Easing function;

    EasingType(Easing function) {
        this.function = function;
    }

    // Führt die mathematische Berechnung aus
    public double apply(double t) {
        return this.function.apply(t);
    }
}
