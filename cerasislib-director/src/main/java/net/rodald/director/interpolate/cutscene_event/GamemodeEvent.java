package net.rodald.director.interpolate.cutscene_event;

import net.rodald.director.interpolate.CutsceneEvent;
import org.bukkit.GameMode;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("GamemodeEvent")
public class GamemodeEvent implements CutsceneEvent, ConfigurationSerializable {
    private final GameMode gameMode;

    public GamemodeEvent(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void trigger(Player player) {
        player.setGameMode(gameMode);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("gameMode", gameMode);

        return data;
    }
}
