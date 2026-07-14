package net.rodald.director.interpolate;

@FunctionalInterface
public interface Easing {
    /**
     * @param t Value between 0 and 1
     * @return transformed Value
     */
    double apply(double t);

    Easing LINEAR = t -> t;
    Easing EASE_IN_QUAD = t -> t * t;
    Easing EASE_OUT_QUAD = t -> t * (2 - t);
    Easing EASE_IN_OUT_QUAD = t -> t < 0.5 ? 2 * t * t : -1 + (4 - 2 * t) * t;
    Easing EASE_IN_CUBIC = t -> t * t * t;
    Easing EASE_OUT_CUBIC = t -> (--t) * t * t + 1;
    Easing EASE_IN_OUT_CUBIC = t -> t < 0.5 ? 4 * t * t * t : (t - 1) * (2 * t - 2) * (2 * t - 2) + 1;
    Easing STEP = t -> (t < 1 ? 0 : 1);
    Easing SMOOTH = t -> (1 - Math.cos(t * Math.PI)) / 2;
    Easing NONE = t -> 0;
}

