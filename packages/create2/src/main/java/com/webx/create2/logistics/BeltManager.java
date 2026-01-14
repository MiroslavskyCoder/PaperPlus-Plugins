package com.webx.create2.logistics;

import com.webx.create2.Create2Plugin;
import com.webx.create2.kinematic.KinematicNetwork;
import com.webx.create2.kinematic.KinematicNetworkManager;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.joml.Vector3i;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages belt segments and item movement.
 */
public class BeltManager {

    private final Create2Plugin plugin;
    private final KinematicNetworkManager networkManager;
    private final Map<Vector3i, BeltSegment> belts = new ConcurrentHashMap<>();

    public BeltManager(Create2Plugin plugin, KinematicNetworkManager networkManager) {
        this.plugin = plugin;
        this.networkManager = networkManager;
    }

    public void addBelt(Vector3i pos, BlockFace face) {
        Vector3i dir = faceToVector(face);
        belts.put(pos, new BeltSegment(pos, dir));
    }

    public void removeBelt(Vector3i pos) {
        belts.remove(pos);
    }

    public void insertIntoBelt(Vector3i pos, ItemStack stack) {
        BeltSegment belt = belts.get(pos);
        if (belt != null) {
            belt.insert(stack);
        }
    }

    public void tick(World world) {
        double speedPerRpm = plugin.getConfig().getDouble("components.belt.speed-per-rpm", 0.02);
        belts.values().forEach(belt -> {
            KinematicNetwork network = networkManager.getNetwork(belt.getPosition());
            double rpm = network != null ? network.getRpm() : 0.0;
            double speed = Math.max(0, rpm * speedPerRpm);
            Vector3i nextPos = new Vector3i(
                belt.getPosition().x + belt.getDirection().x,
                belt.getPosition().y + belt.getDirection().y,
                belt.getPosition().z + belt.getDirection().z
            );
            BeltSegment next = belts.get(nextPos);
            belt.tick(world, speed, next);
        });
    }

    private Vector3i faceToVector(BlockFace face) {
        return switch (face) {
            case NORTH -> new Vector3i(0, 0, -1);
            case SOUTH -> new Vector3i(0, 0, 1);
            case WEST -> new Vector3i(-1, 0, 0);
            case EAST -> new Vector3i(1, 0, 0);
            case UP -> new Vector3i(0, 1, 0);
            case DOWN -> new Vector3i(0, -1, 0);
            default -> new Vector3i(0, 0, 1);
        };
    }

    public boolean isBelt(Vector3i pos) {
        return belts.containsKey(pos);
    }
}
