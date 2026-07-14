package net.rodald.director;

import java.util.ArrayList;
import java.util.List;

public class Timeline {
    private final String id;
    private final List<Scene> scenes = new ArrayList<>();

    public Timeline(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public List<Scene> getScenes() {
        return scenes;
    }

    public Timeline addScene(Scene scene) {
        scenes.add(scene);
        return this;
    }

    public int getDuration() {
        return scenes.stream().mapToInt(Scene::getDuration).sum();
    }
}
