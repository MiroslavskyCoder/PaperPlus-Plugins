package com.webx.create2.fluid;

import com.webx.create2.Create2Plugin;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.joml.Vector3i;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simplified fluid network: pipes equalize, pumps move from source to front, tanks store.
 */
public class FluidManager {

    private final Create2Plugin plugin;
    private final Map<Vector3i, FluidNode> nodes = new ConcurrentHashMap<>();

    public FluidManager(Create2Plugin plugin) {
        this.plugin = plugin;
    }

    public void addNode(Vector3i pos, FluidNode.Type type) {
        int capacity;
        switch (type) {
            case PIPE -> capacity = plugin.getConfig().getInt("fluids.pipe-capacity", 1000);
            case TANK -> capacity = plugin.getConfig().getInt("fluids.tank-capacity", 16000);
            case PUMP -> capacity = plugin.getConfig().getInt("fluids.pipe-capacity", 1000);
            default -> capacity = 1000;
        }
        nodes.put(pos, new FluidNode(pos, type, capacity));
    }

    public void removeNode(Vector3i pos) {
        nodes.remove(pos);
    }

    public boolean isNode(Vector3i pos) {
        return nodes.containsKey(pos);
    }

    public void tick(World world) {
        int pumpRate = plugin.getConfig().getInt("fluids.pump-rate", 200);
        double loss = plugin.getConfig().getDouble("fluids.viscosity-loss", 0.02);

        // Pumps: pull from source below, push forward
        nodes.values().stream()
            .filter(n -> n.getType() == FluidNode.Type.PUMP)
            .forEach(pump -> {
                Block below = world.getBlockAt(pump.getPosition().x, pump.getPosition().y - 1, pump.getPosition().z);
                boolean water = below.getType() == Material.WATER;
                if (!water) return;

                // take from infinite source
                int pulled = pumpRate;
                // push to front node if exists
                Vector3i frontPos = new Vector3i(pump.getPosition().x + 1, pump.getPosition().y, pump.getPosition().z);
                FluidNode front = nodes.get(frontPos);
                if (front != null) {
                    int leftover = front.insert(pulled);
                    // drop overflow
                    if (leftover > 0) {
                        pump.setAmount(pump.getAmount() + leftover);
                    }
                }
            });

        // Equalize pipes with neighbors (simple averaging)
        for (FluidNode node : nodes.values()) {
            for (Vector3i neighborPos : neighbors(node.getPosition())) {
                FluidNode neighbor = nodes.get(neighborPos);
                if (neighbor == null) continue;
                if (node.getAmount() == neighbor.getAmount()) continue;
                int avg = (node.getAmount() + neighbor.getAmount()) / 2;
                node.setAmount(avg);
                neighbor.setAmount(avg);
            }
        }

        // Apply loss for distance (simple decay)
        nodes.values().forEach(node -> {
            int lose = (int) (node.getAmount() * loss);
            node.setAmount(node.getAmount() - lose);
        });
    }

    private Set<Vector3i> neighbors(Vector3i pos) {
        return Set.of(
            new Vector3i(pos.x + 1, pos.y, pos.z),
            new Vector3i(pos.x - 1, pos.y, pos.z),
            new Vector3i(pos.x, pos.y + 1, pos.z),
            new Vector3i(pos.x, pos.y - 1, pos.z),
            new Vector3i(pos.x, pos.y, pos.z + 1),
            new Vector3i(pos.x, pos.y, pos.z - 1)
        );
    }
}
