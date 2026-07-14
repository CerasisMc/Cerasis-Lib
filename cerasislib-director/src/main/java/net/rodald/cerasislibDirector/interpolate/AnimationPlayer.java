package net.rodald.cerasislibDirector.interpolate;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AnimationPlayer extends BukkitRunnable {

    private final Player player;
    private final AnimationPath path;

    private int currentNodeIndex = 0;
    private int tickInNode = 0;

    public AnimationPlayer(Player player, AnimationPath path) {
        this.player = player;
        this.path = path;
    }

    @Override
    public void run() {
        AnimationNode start = path.getNodes().get(currentNodeIndex);


        // trigger events
        if (tickInNode == 0) {
            for (CutsceneEvent cutsceneEvent : start.events()) {
                cutsceneEvent.trigger(player);
            }
        }

        // cancel animation if the end node is reached
        if (currentNodeIndex >= path.getNodes().size() - 1) {
            this.cancel();
            return;
        }

        AnimationNode end = path.getNodes().get(currentNodeIndex + 1);

        int duration = end.durationTicks();
        double t = (double) tickInNode / duration;
        double eased = end.easing().apply(t);

        Location from = start.location();
        Location to = end.location();

        Location interpolated = interpolate(from, to, eased);
        player.teleport(interpolated);

        tickInNode++;
        if (tickInNode > duration) {
            currentNodeIndex++;
            tickInNode = 0;
        }
    }


    private Location interpolate(Location from, Location to, double t) {
        double x = from.getX() + (to.getX() - from.getX()) * t;
        double y = from.getY() + (to.getY() - from.getY()) * t;
        double z = from.getZ() + (to.getZ() - from.getZ()) * t;
        float yaw = (float) (from.getYaw() + (to.getYaw() - from.getYaw()) * t);
        float pitch = (float) (from.getPitch() + (to.getPitch() - from.getPitch()) * t);
        return new Location(from.getWorld(), x, y, z, yaw, pitch);
    }
}


