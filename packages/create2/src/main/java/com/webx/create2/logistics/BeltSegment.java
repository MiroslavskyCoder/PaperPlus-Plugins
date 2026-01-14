package com.webx.create2.logistics;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A single belt block with direction and moving items.
 */
public class BeltSegment {

    private final Vector3i position;
    private final Vector3i direction; // unit vector
    private final List<BeltItem> items = new ArrayList<>();

    public BeltSegment(Vector3i position, Vector3i direction) {
        this.position = position;
        this.direction = direction;
    }

    public Vector3i getPosition() {
        return position;
    }

    public Vector3i getDirection() {
        return direction;
    }

    public void insert(ItemStack stack) {
        if (stack == null || stack.getAmount() <= 0) return;
        items.add(new BeltItem(stack.clone()));
    }

    /**
     * Tick movement, attempt to move items to next belt or inventory.
     * @param world world
     * @param speed blocks per tick
     * @param nextSegment the neighbor belt or null
     */
    public void tick(World world, double speed, BeltSegment nextSegment) {
        if (speed <= 0) return;
        Iterator<BeltItem> iter = items.iterator();
        while (iter.hasNext()) {
            BeltItem item = iter.next();
            item.advance(speed);
            if (item.getProgress() >= 1.0) {
                // try handoff
                ItemStack stack = item.getStack();
                if (nextSegment != null) {
                    nextSegment.insert(stack);
                } else {
                    // try inventory
                    if (!tryInsertInventory(world, stack)) {
                        // drop item in world
                        Location dropLoc = new Location(world, position.x + 0.5 + direction.x * 0.5,
                            position.y + 0.5 + direction.y * 0.5, position.z + 0.5 + direction.z * 0.5);
                        world.dropItemNaturally(dropLoc, stack);
                    }
                }
                iter.remove();
            }
        }
    }

    private boolean tryInsertInventory(World world, ItemStack stack) {
        Block target = world.getBlockAt(position.x + direction.x, position.y + direction.y, position.z + direction.z);
        if (!(target.getState() instanceof Container container)) {
            return false;
        }
        Inventory inv = container.getInventory();
        HashMap<Integer, ItemStack> left = inv.addItem(stack);
        if (left.isEmpty()) return true;
        // If not fully inserted, keep remaining in place
        stack.setAmount(left.values().iterator().next().getAmount());
        insert(stack);
        return true;
    }
}
