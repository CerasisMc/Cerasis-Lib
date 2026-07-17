package net.rodald.director.interpolate;

import org.bukkit.Location;
import org.bukkit.Utility;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SerializableAs("KeyFrame")
public record KeyFrame(Location location, long tick, EasingType easing,
                       @NotNull List<CutsceneEvent> events) implements ConfigurationSerializable {

    /**
     * Creates a KeyFrame without any events.
     */
    public static KeyFrame of(long tick, Location location, EasingType easing) {
        return new KeyFrame(location, tick, easing, new ArrayList<>());
    }

    /**
     * Creates a KeyFrame with linear easing and no events.
     */
    public static KeyFrame of(long tick, Location location) {
        return new KeyFrame(location, tick, EasingType.LINEAR, new ArrayList<>());
    }

    @Override
    public Location location() {
        return location.clone();
    }

    @Override
    public @NotNull List<CutsceneEvent> events() {
        return Collections.unmodifiableList(events);
    }

    /**
     * Adds an event to this keyframe.
     * Note: This modifies the internal list.
     */
    public void addEvent(CutsceneEvent event) {
        this.events.add(event);
    }

    @Utility
    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("location", location);
        data.put("tick", tick);
        data.put("easing", easing.name());
        data.put("events", events);

        return data;
    }

    @NotNull
    public static KeyFrame deserialize(@NotNull Map<String, Object> args) {
        List<CutsceneEvent> events = new ArrayList<>();

        if (args.get("events") instanceof List<?> rawList) {
            for (Object obj : rawList) {
                if (obj instanceof CutsceneEvent cutsceneEvent) {
                    events.add(cutsceneEvent);
                }
            }
        }

        return new KeyFrame(
                (Location) args.get("location"),
                NumberConversions.toLong(args.get("tick")),
                EasingType.valueOf((String) args.get("easing")),
                events
        );
    }
}
