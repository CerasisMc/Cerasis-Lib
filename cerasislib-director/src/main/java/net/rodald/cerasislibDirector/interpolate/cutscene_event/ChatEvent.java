package net.rodald.cerasislibDirector.interpolate.cutscene_event;

import net.rodald.cerasislibDirector.interpolate.CutsceneEvent;
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
