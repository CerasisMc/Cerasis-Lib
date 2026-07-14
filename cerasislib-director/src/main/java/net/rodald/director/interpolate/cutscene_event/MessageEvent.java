package net.rodald.director.interpolate.cutscene_event;

import net.kyori.adventure.title.Title;
import net.rodald.director.interpolate.CutsceneEvent;
import org.bukkit.entity.Player;

public class MessageEvent implements CutsceneEvent {
    private final Title title;

    public MessageEvent(Title title) {
        this.title = title;
    }

    @Override
    public void trigger(Player player) {
        player.showTitle(title);
    }
}
