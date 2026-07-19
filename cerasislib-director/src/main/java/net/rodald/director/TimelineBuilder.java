package net.rodald.director;

import net.rodald.director.camera.Camera;
import net.rodald.director.interpolate.CutsceneEvent;
import net.rodald.director.interpolate.EasingType;
import net.rodald.director.interpolate.KeyFrame;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class TimelineBuilder {
    private final String timelineId;
    private final Timeline timeline;
    private Scene currentScene;
    private Shot currentShot;
    private final List<CutsceneEvent> pendingEvents = new ArrayList<>();

    public TimelineBuilder(String timelineId) {
        this.timelineId = timelineId;
        this.timeline = new Timeline(timelineId);
    }

    /**
     * Starts a new scene.
     */
    public TimelineBuilder startScene(String sceneId) {
        if (currentScene != null) {
            throw new IllegalStateException("Cannot start a new scene without ending the current one. Call endScene() first.");
        }
        currentScene = new Scene(sceneId);
        return this;
    }

    /**
     * Ends the current scene and adds it to the timeline.
     */
    public TimelineBuilder endScene() {
        if (currentScene == null) {
            throw new IllegalStateException("No scene to end. Call startScene() first.");
        }
        if (currentShot != null) {
            throw new IllegalStateException("Cannot end scene while shot is still open. Call endShot() first.");
        }
        timeline.addScene(currentScene);
        currentScene = null;
        return this;
    }

    /**
     * Starts a new shot within the current scene.
     */
    public TimelineBuilder startShot(String shotId, Camera camera) {
        if (currentScene == null) {
            throw new IllegalStateException("Cannot start a shot without an active scene. Call startScene() first.");
        }
        if (currentShot != null) {
            throw new IllegalStateException("Cannot start a new shot without ending the current one. Call endShot() first.");
        }
        currentShot = new Shot(shotId, camera);
        return this;
    }

    /**
     * Ends the current shot and adds it to the current scene.
     */
    public TimelineBuilder endShot() {
        if (currentShot == null) {
            throw new IllegalStateException("No shot to end. Call startShot() first.");
        }
        currentScene.addShot(currentShot);
        currentShot = null;
        pendingEvents.clear();
        return this;
    }

    /**
     * Adds a keyframe to the current shot.
     */
    public TimelineBuilder addKeyFrame(int tick, Location location, EasingType easing) {
        if (currentShot == null) {
            throw new IllegalStateException("Cannot add keyframe without an active shot. Call startShot() first.");
        }
        KeyFrame keyFrame = new KeyFrame(location, tick, easing, new ArrayList<>(pendingEvents));
        currentShot.addKeyFrame(keyFrame);
        pendingEvents.clear();
        return this;
    }

    /**
     * Adds a keyframe with linear easing.
     */
    public TimelineBuilder addKeyFrame(int tick, Location location) {
        return addKeyFrame(tick, location, EasingType.LINEAR);
    }

    /**
     * Adds an event to be attached to the next keyframe.
     */
    public TimelineBuilder addEvent(CutsceneEvent event) {
        pendingEvents.add(event);
        return this;
    }

    /**
     * Builds and returns the final Timeline.
     */
    public Timeline build() {
        if (currentScene != null) {
            throw new IllegalStateException("Cannot build timeline with an open scene. Call endScene() first.");
        }
        if (currentShot != null) {
            throw new IllegalStateException("Cannot build timeline with an open shot. Call endShot() first.");
        }
        return timeline;
    }
}