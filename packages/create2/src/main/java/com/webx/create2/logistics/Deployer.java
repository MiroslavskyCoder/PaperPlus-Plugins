package com.webx.create2.logistics;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.joml.Vector3i;

/**
 * Simplified deployer: pulls items from inventory behind and places/uses on block ahead.
 */
public class Deployer {

    private final Vector3i position;
    private final Vector3i direction;
    private final int cooldownTicks;
    private int cooldown;

    public Deployer(Vector3i position, BlockFace face, int cooldownTicks) {
        this.position = position;
        this.direction = faceToVector(face);
        this.cooldownTicks = cooldownTicks;
        this.cooldown = 0;
    }

    public Vector3i getPosition() {
        return position;
    }

    public void tick(World world) {
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        Block sourceBlock = world.getBlockAt(position.x - direction.x, position.y - direction.y, position.z - direction.z);
        Inventory sourceInv = getInventory(sourceBlock);
        if (sourceInv == null) return;

        Block targetBlock = world.getBlockAt(position.x + direction.x, position.y + direction.y, position.z + direction.z);

        // Find first non-empty item
        ItemStack stack = null;
        int slotFound = -1;
        for (int i = 0; i < sourceInv.getSize(); i++) {
            ItemStack s = sourceInv.getItem(i);
            if (s != null && s.getAmount() > 0) {
                stack = s.clone();
                slotFound = i;
                break;
            }
        }
        if (stack == null) return;

        // Try place block if target is air and item is placeable
        if (targetBlock.getType() == Material.AIR && stack.getType().isBlock()) {
            targetBlock.setType(stack.getType());
            sourceInv.getItem(slotFound).setAmount(sourceInv.getItem(slotFound).getAmount() - 1);
        } else if (getInventory(targetBlock) != null) {
            // If inventory in front, insert
            Inventory inv = getInventory(targetBlock);
            inv.addItem(new ItemStack(stack.getType(), 1));
            sourceInv.getItem(slotFound).setAmount(sourceInv.getItem(slotFound).getAmount() - 1);
        } else {
            // Otherwise, right-click like player use (simplified drop)
            Location drop = targetBlock.getLocation().add(0.5, 0.5, 0.5);
            world.dropItemNaturally(drop, new ItemStack(stack.getType(), 1));
            sourceInv.getItem(slotFound).setAmount(sourceInv.getItem(slotFound).getAmount() - 1);
        }

        cooldown = cooldownTicks;
    }

    private Inventory getInventory(Block block) {
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
