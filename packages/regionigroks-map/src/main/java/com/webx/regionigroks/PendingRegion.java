package com.webx.regionigroks;

import org.bukkit.Color;
import org.bukkit.Location;

public class PendingRegion {
    public enum Stage { COLOR_SELECT, RADIUS_INPUT, NAME_INPUT }

    private Color color;
    private Location center;
    private int radius = 32; // default radius in blocks
    private Stage stage = Stage.COLOR_SELECT;

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    public Location getCenter() { return center; }
    public void setCenter(Location center) { this.center = center; }

    public int getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = radius; }

    public Stage getStage() { return stage; }
    public void setStage(Stage stage) { this.stage = stage; }
}
