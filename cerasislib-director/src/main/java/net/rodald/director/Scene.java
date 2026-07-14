package net.rodald.director;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scene {
    private final String id;
    private final List<Shot> shots = new ArrayList<>();

    public Scene(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Shot> getShots() {
        return Collections.unmodifiableList(shots);
    }

    public Scene addShot(Shot shot) {
        shots.add(shot);
        return this;
    }

    /**
     * Returns the total duration of this scene in ticks.
     */
    public int getDuration() {
        return shots.stream().mapToInt(Shot::getDuration).sum();
    }
}
