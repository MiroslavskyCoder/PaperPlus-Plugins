package com.webx.create2.logistics;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.joml.Vector3i;

import java.util.HashMap;

/**
 * Simple funnel that moves items between inventory and belts in facing direction.
 */
public class Funnel {

    private final Vector3i position;
    private final Vector3i direction;
    private final int transferRate;

    public Funnel(Vector3i position, BlockFace face, int transferRate) {
        this.position = position;
        this.direction = faceToVector(face);
        this.transferRate = transferRate;
    }

    public Vector3i getPosition() {
        return position;
    }

    public Vector3i getDirection() {
        return direction;
    }

    public void tick(World world, BeltManager beltManager) {
        // Pull from inventory behind funnel and push into belt ahead
        Block behind = world.getBlockAt(position.x - direction.x, position.y - direction.y, position.z - direction.z);
        Block ahead = world.getBlockAt(position.x + direction.x, position.y + direction.y, position.z + direction.z);

        Inventory fromInv = getInventory(behind);
        if (fromInv == null) return;

        int moved = 0;
        for (int slot = 0; slot < fromInv.getSize() && moved < transferRate; slot++) {
            ItemStack stack = fromInv.getItem(slot);
            if (stack == null || stack.getAmount() == 0) continue;
            ItemStack toMove = stack.clone();
            toMove.setAmount(1);
            stack.setAmount(stack.getAmount() - 1);
            moved++;

            Vector3i targetPos = new Vector3i(ahead.getX(), ahead.getY(), ahead.getZ());
            if (beltManager.isBelt(targetPos)) {
                beltManager.insertIntoBelt(targetPos, toMove);
            } else {
                Inventory toInv = getInventory(ahead);
                if (toInv != null) {
                    toInv.addItem(toMove);
                } else {
                    world.dropItemNaturally(new Location(world, targetPos.x + 0.5, targetPos.y + 0.5, targetPos.z + 0.5), toMove);
                }
            }
        }
    }

    private Inventory getInventory(Block block) {
        if (block == null) return null;
        if (block.getState() instanceof Container container) {
            return container.getInventory();
        }
        return null;
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
}
