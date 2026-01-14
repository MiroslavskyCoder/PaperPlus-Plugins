package com.webx.create2.listeners;

import com.webx.create2.Create2Plugin;
import com.webx.create2.kinematic.KinematicNetworkManager;
import com.webx.create2.kinematic.KinematicNodeType;
import com.webx.create2.logistics.LogisticsManager;
import com.webx.create2.fluid.FluidManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.joml.Vector3i;

/**
 * Listens for block events to manage kinematic networks
 * Inspired by Create's block placement system
 */
public class BlockListener implements Listener {
    
    private final Create2Plugin plugin;
    
    public BlockListener(Create2Plugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Material material = block.getType();
        
        // Check if this is a kinematic component
        KinematicNodeType type = getNodeType(material);
        if (type == null) return;
        
        // Create kinematic node
        Vector3i position = new Vector3i(
            block.getX(),
            block.getY(),
            block.getZ()
        );
        
        KinematicNetworkManager manager = plugin.getNetworkManager();
        manager.createNode(position, type);

        // Logistics / fluid extras
        LogisticsManager logistics = plugin.getLogisticsManager();
        FluidManager fluids = plugin.getFluidManager();
        BlockFace facing = event.getPlayer() != null ? event.getPlayer().getFacing() : BlockFace.NORTH;

        switch (type) {
            case BELT -> logistics.getBeltManager().addBelt(position, facing);
            case FUNNEL -> logistics.addFunnel(position, facing);
            case DEPLOYER -> logistics.addDeployer(position, facing);
            case PIPE -> fluids.addNode(position, com.webx.create2.fluid.FluidNode.Type.PIPE);
            case PUMP -> fluids.addNode(position, com.webx.create2.fluid.FluidNode.Type.PUMP);
            case TANK -> fluids.addNode(position, com.webx.create2.fluid.FluidNode.Type.TANK);
            default -> {}
        }
        
        plugin.getLogger().info("Created " + type.name() + " at " + position);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        
        Vector3i position = new Vector3i(
            block.getX(),
            block.getY(),
            block.getZ()
        );
        
        KinematicNetworkManager manager = plugin.getNetworkManager();
        LogisticsManager logistics = plugin.getLogisticsManager();
        FluidManager fluids = plugin.getFluidManager();
        
        // Check if this is a kinematic component
        if (manager.getNode(position) != null) {
            manager.removeNode(position);
            plugin.getLogger().info("Removed kinematic component at " + position);
        }

        // Cleanup logistics/fluid data
        logistics.getBeltManager().removeBelt(position);
        logistics.removeFunnel(position);
        logistics.removeDeployer(position);
        fluids.removeNode(position);
    }
    
    /**
     * Map Minecraft materials to kinematic node types
     * TODO: Use custom items or NBT tags
     */
    private KinematicNodeType getNodeType(Material material) {
        switch (material) {
            // Transmission
            case STRIPPED_OAK_LOG:
            case STRIPPED_BIRCH_LOG:
                return KinematicNodeType.SHAFT;
                
            case IRON_BLOCK:
                return KinematicNodeType.COGWHEEL;
                
            case GOLD_BLOCK:
                return KinematicNodeType.LARGE_COGWHEEL;
                
            case DIAMOND_BLOCK:
                return KinematicNodeType.GEARBOX;

            // Belts
            case WHITE_CARPET:
                return KinematicNodeType.BELT;

            // Funnels
            case HOPPER:
                return KinematicNodeType.FUNNEL;

            // Deployer
            case DISPENSER:
                return KinematicNodeType.DEPLOYER;
                
            // Power sources
            case LEVER:
                return KinematicNodeType.HAND_CRANK;
                
            case DARK_OAK_PLANKS:
                return KinematicNodeType.WATER_WHEEL;
                
            case WHITE_WOOL:
                return KinematicNodeType.WINDMILL;
                
            case BEACON:
                return KinematicNodeType.MOTOR;

            // Fluids
            case PISTON:
                return KinematicNodeType.PUMP;
            case GLASS:
                return KinematicNodeType.PIPE;
            case BARREL:
                return KinematicNodeType.TANK;
                
            // Consumers
            case ANVIL:
                return KinematicNodeType.MECHANICAL_PRESS;
                
            case CAULDRON:
                return KinematicNodeType.MECHANICAL_MIXER;
                
            case GRINDSTONE:
                return KinematicNodeType.MILLSTONE;
                
            default:
                return null;
        }
    }
}
