package net.rodald.director.interpolate;

@FunctionalInterface
public interface Easing {
    /**
     * @param t Value between 0 and 1
     * @return transformed Value
     */
    double apply(double t);
}

