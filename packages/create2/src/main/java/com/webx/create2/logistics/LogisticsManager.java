package com.webx.create2.logistics;

import com.webx.create2.Create2Plugin;
import com.webx.create2.kinematic.KinematicNetworkManager;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.joml.Vector3i;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * High-level manager for belts, funnels, deployers.
 */
public class LogisticsManager {

    private final Create2Plugin plugin;
    private final BeltManager beltManager;
    private final Map<Vector3i, Funnel> funnels = new ConcurrentHashMap<>();
    private final Map<Vector3i, Deployer> deployers = new ConcurrentHashMap<>();

    public LogisticsManager(Create2Plugin plugin, KinematicNetworkManager networkManager) {
        this.plugin = plugin;
        this.beltManager = new BeltManager(plugin, networkManager);
    }

    public BeltManager getBeltManager() {
        return beltManager;
    }

    public void addFunnel(Vector3i pos, BlockFace face) {
        int rate = plugin.getConfig().getInt("components.funnel.transfer-rate", 4);
        funnels.put(pos, new Funnel(pos, face, rate));
    }

    public void removeFunnel(Vector3i pos) {
        funnels.remove(pos);
    }

    public void addDeployer(Vector3i pos, BlockFace face) {
        int cooldown = plugin.getConfig().getInt("components.deployer.cooldown", 20);
        deployers.put(pos, new Deployer(pos, face, cooldown));
    }

    public void removeDeployer(Vector3i pos) {
        deployers.remove(pos);
    }

    public void tick(World world) {
        beltManager.tick(world);
        funnels.values().forEach(f -> f.tick(world, beltManager));
        deployers.values().forEach(d -> d.tick(world));
    }

    public boolean isFunnel(Vector3i pos) {
        return funnels.containsKey(pos);
    }

    public boolean isDeployer(Vector3i pos) {
        return deployers.containsKey(pos);
    }
}
