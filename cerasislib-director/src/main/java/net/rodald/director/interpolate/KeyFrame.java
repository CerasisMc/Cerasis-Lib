package net.rodald.director.interpolate;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record KeyFrame(Location location, int tick, Easing easing, @NotNull List<CutsceneEvent> events) {

    /**
     * Creates a KeyFrame without any events.
     */
    public static KeyFrame of(int tick, Location location, Easing easing) {
        return new KeyFrame(location, tick, easing, new ArrayList<>());
    }

    /**
     * Creates a KeyFrame with linear easing and no events.
     */
    public static KeyFrame of(int tick, Location location) {
        return new KeyFrame(location, tick, Easing.LINEAR, new ArrayList<>());
    }

    @Override
    public Location location() {
        return location.clone();
    }

    @Override
    public List<CutsceneEvent> events() {
        return Collections.unmodifiableList(events);
    }

    /**
     * Adds an event to this keyframe.
     * Note: This modifies the internal list.
     */
    public KeyFrame addEvent(CutsceneEvent event) {
        this.events.add(event);
        return this;
    }
}
