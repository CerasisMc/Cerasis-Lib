package net.rodald.director.interpolate.cutscene_event;

import net.rodald.director.interpolate.CutsceneEvent;
import org.bukkit.entity.Player;

public class ChatEvent implements CutsceneEvent {
    private final String text;

    public ChatEvent(String text) {
        this.text = text;
    }

    @Override
    public void trigger(Player player) {
        player.sendMessage(text);
    }
}
