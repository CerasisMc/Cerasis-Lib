package net.rodald.cerasislibDirector.interpolate;

import java.util.ArrayList;
import java.util.List;

public class AnimationPath {
    private final List<AnimationNode> nodes = new ArrayList<>();

    public void addNode(AnimationNode node) {
        nodes.add(node);
    }

    public List<AnimationNode> getNodes() {
        return nodes;
    }

    public int getTotalDuration() {
        return nodes.stream().mapToInt(AnimationNode::durationTicks).sum();
    }
}
