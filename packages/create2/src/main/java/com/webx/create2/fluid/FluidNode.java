package com.webx.create2.fluid;

import org.joml.Vector3i;

public class FluidNode {
    public enum Type { PIPE, TANK, PUMP }

    private final Vector3i position;
    private final Type type;
    private final int capacity;
    private int amount;

    public FluidNode(Vector3i position, Type type, int capacity) {
        this.position = position;
        this.type = type;
        this.capacity = capacity;
        this.amount = 0;
    }

    public Vector3i getPosition() {
        return position;
    }

    public Type getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, Math.min(capacity, amount));
    }

    public int insert(int toAdd) {
        int space = capacity - amount;
        int accepted = Math.min(space, toAdd);
        amount += accepted;
        return toAdd - accepted;
    }

    public int extract(int toTake) {
        int taken = Math.min(amount, toTake);
        amount -= taken;
        return taken;
    }
}
