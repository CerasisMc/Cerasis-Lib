package net.rodald.cerasislibDirector.interpolate;

@FunctionalInterface
public interface Easing {
    /**
     * @param t Value between 0 and 1
     * @return transformed Value
     */
    double apply(double t);

    Easing LINEAR = t -> t;
    Easing STEP = t -> (t < 1 ? 0 : 1);
    Easing SMOOTH = t -> (1 - Math.cos(t * Math.PI)) / 2;
    Easing NONE = t -> 0;
}

