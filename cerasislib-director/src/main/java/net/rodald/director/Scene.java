package net.rodald.director;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SerializableAs("Scene")
public record Scene(String id, List<Shot> shots) implements ConfigurationSerializable {
    public Scene(String id) {
        this(id, new ArrayList<>());
    }

    public Scene(String id, List<Shot> shots) {
        this.id = id;
        this.shots = new ArrayList<>();
        this.shots.addAll(shots);
    }

    @Override
    public List<Shot> shots() {
        return Collections.unmodifiableList(shots);
    }

    public void addShot(Shot shot) {
        shots.add(shot);
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
