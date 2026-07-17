package net.rodald.director;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SerializableAs("Scene")
public class Scene implements ConfigurationSerializable {
    private final String id;

    private final List<Shot> shots = new ArrayList<>();
    public Scene(String id) {
        this.id = id;
    }

    public Scene(String id, List<Shot> shots) {
        this.id = id;
        this.shots.addAll(shots);
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
    public long getDuration() {
        return shots.stream().mapToLong(Shot::getDuration).sum();
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("shots", shots);

        return data;
    }

    public static Scene deserialize(@NotNull Map<String, Object> args) {
        List<Shot> shots = new ArrayList<>();

        if (args.get("shots") instanceof List<?> rawList) {
            for (Object obj : rawList) {
                shots.add((Shot) obj);
            }
        }
        return new Scene(
                (String) args.get("id"),
                shots
        );
    }
}
