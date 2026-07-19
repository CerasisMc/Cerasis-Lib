package net.rodald.director.interpolate.cutscene_event;

import net.rodald.director.interpolate.CutsceneEvent;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("ChatEvent")
public class ChatEvent implements CutsceneEvent, ConfigurationSerializable {
    private final String text;

    public ChatEvent(String text) {
        this.text = text;
    }

    @Override
    public void trigger(Player player) {
        player.sendMessage(text);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("text", text);

        return data;
    }
}
