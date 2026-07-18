package net.rodald.director;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("Timeline")
public record Timeline(String id, List<Scene> scenes) implements ConfigurationSerializable {

    public Timeline(String id) {
        this(id, new ArrayList<>());
    }


    public void addScene(Scene scene) {
        scenes.add(scene);
    }

    public long getDuration() {
        return scenes.stream().mapToLong(Scene::getDuration).sum();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("scenes", scenes);

        return data;
    }

    public static Timeline deserialize(@NotNull Map<String, Object> args) {
        List<Scene> scenes = new ArrayList<>();

        if (args.get("scenes") instanceof List<?> rawList) {
            for (Object obj : rawList) {
                scenes.add((Scene) obj);
            }
        }

        return new Timeline(
                (String) args.get("id"),
                scenes
        );
    }
}
