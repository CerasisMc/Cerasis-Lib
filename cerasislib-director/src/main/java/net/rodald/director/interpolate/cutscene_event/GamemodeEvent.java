package net.rodald.director.interpolate.cutscene_event;

import net.rodald.director.interpolate.CutsceneEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeEvent implements CutsceneEvent {
    private final GameMode gameMode;

    public GamemodeEvent(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void trigger(Player player) {
        player.setGameMode(gameMode);
    }
}
