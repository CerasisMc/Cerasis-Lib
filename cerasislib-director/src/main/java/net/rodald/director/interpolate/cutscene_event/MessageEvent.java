package net.rodald.director.interpolate.cutscene_event;

import net.kyori.adventure.title.Title;
import net.rodald.director.interpolate.CutsceneEvent;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("MessageEvent")
public class MessageEvent implements CutsceneEvent, ConfigurationSerializable {
    private final Title title;

    public MessageEvent(Title title) {
        this.title = title;
    }

    @Override
    public void trigger(Player player) {
        player.showTitle(title);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("title", title);

        return data;
    }
}
