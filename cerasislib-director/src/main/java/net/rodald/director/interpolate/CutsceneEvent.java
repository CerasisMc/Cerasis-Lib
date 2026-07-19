package net.rodald.director.interpolate;

import org.bukkit.entity.Player;

import java.util.List;

public interface CutsceneEvent {
    void trigger(Player player);

    default void trigger(List<Player> players) {
        players.forEach(this::trigger);
    }
}

