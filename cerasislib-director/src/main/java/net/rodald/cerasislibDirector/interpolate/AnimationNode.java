package net.rodald.cerasislibDirector.interpolate;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public record AnimationNode(Location location, int durationTicks, Easing easing, List<CutsceneEvent> events) {

    public AnimationNode(Location location, int durationTicks, Easing easing) {
        this(location, durationTicks, easing, new ArrayList<>());
    }

    @Override
    public Location location() {
        return location.clone();
    }

    public AnimationNode addEvent(CutsceneEvent event) {
        events.add(event);

        return this;
    }
}

