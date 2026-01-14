package com.webx.hdphysicssound.engine;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class OcclusionCalculator {

    private final World world;

    public OcclusionCalculator(World world) {
        this.world = world;
    }

    /**
     * Returns occlusion factor in [0,1], where 1 = clear line of sight, 0 = fully blocked.
     */
    public double calculate(Location source, Player listener, int samples, double penalty) {
        if (!source.getWorld().equals(listener.getWorld())) {
            return 0.0;
        }

        Vector direction = listener.getEyeLocation().toVector().subtract(source.toVector());
        double distance = direction.length();
        if (distance < 0.1) {
            return 1.0;
        }
        direction.normalize();

        // Sample along the path to approximate blockage
        int blocked = 0;
        for (int i = 1; i <= samples; i++) {
            double sampleDistance = (distance * i) / (double) (samples + 1);
            Vector samplePoint = source.toVector().add(direction.clone().multiply(sampleDistance));
            RayTraceResult ray = world.rayTraceBlocks(samplePoint.toLocation(world), direction, 0.5, FluidCollisionMode.NEVER, true);
            if (ray != null && ray.getHitBlock() != null) {
                blocked++;
            }
        }

        double occlusion = 1.0 - (blocked * penalty);
        return Math.max(0.0, Math.min(1.0, occlusion));
    }
}
