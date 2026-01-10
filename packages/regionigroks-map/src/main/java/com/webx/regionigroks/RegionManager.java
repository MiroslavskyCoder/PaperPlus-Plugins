package com.webx.regionigroks;

import org.bukkit.Color;
import org.bukkit.Location;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RegionManager {
    private final List<Region> regions = new ArrayList<>();

    public Region createRegion(String name, Color color, UUID owner, Location center, int radius) {
        Region r = new Region(
                name,
                color,
                owner,
                center.getWorld() != null ? center.getWorld().getUID().toString() : "",
                center.getBlockX(),
                center.getBlockZ(),
                radius
        );
        regions.add(r);
        return r;
    }

    public List<Region> getRegions() { return regions; }

    public Optional<Region> getRegionByName(String name) {
        return regions.stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<Region> findRegionAt(Location loc) {
        return regions.stream().filter(r -> r.contains(loc)).findFirst();
    }

    public boolean canJoin(Region r, UUID playerId) {
        if (r.getPrivacy() == Region.Privacy.OPEN) return true;
        return r.isInvited(playerId) || r.getOwner().equals(playerId);
    }

    public boolean joinRegion(Region r, UUID playerId) {
        if (canJoin(r, playerId)) {
            r.addMember(playerId);
            r.revokeInvite(playerId);
            return true;
        }
        return false;
    }

    public boolean leaveRegion(Region r, UUID playerId) {
        if (!r.getOwner().equals(playerId)) {
            r.removeMember(playerId);
            return true;
        }
        return false; // owner cannot leave their own region
    }

    public void invite(Region r, UUID playerId) { r.invite(playerId); }

    public void setPrivacy(Region r, Region.Privacy privacy) { r.setPrivacy(privacy); }

    // Persistence
    public void save(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        List<RegionData> data = regions.stream().map(r -> {
            RegionData d = new RegionData();
            d.name = r.getName();
            d.colorRGB = r.getColor().asRGB();
            d.owner = r.getOwner().toString();
            d.worldId = r.getWorldId();
            d.centerX = r.getCenterX();
            d.centerZ = r.getCenterZ();
            d.radius = r.getRadius();
            d.privacy = r.getPrivacy().name();
            d.members = r.getMembers().stream().map(UUID::toString).collect(Collectors.toList());
            d.invites = r.getInvites().stream().map(UUID::toString).collect(Collectors.toList());
            return d;
        }).collect(Collectors.toList());
        file.getParentFile().mkdirs();
        mapper.writeValue(file, data);
    }

    public void load(File file) throws IOException {
        regions.clear();
        if (!file.exists()) return;
        ObjectMapper mapper = new ObjectMapper();
        RegionData[] arr = mapper.readValue(file, RegionData[].class);
        for (RegionData d : arr) {
            Region r = new Region(
                    d.name,
                    Color.fromRGB(d.colorRGB),
                    UUID.fromString(d.owner),
                    d.worldId,
                    d.centerX,
                    d.centerZ,
                    d.radius
            );
            r.setPrivacy("CLOSED".equalsIgnoreCase(d.privacy) ? Region.Privacy.CLOSED : Region.Privacy.OPEN);
            if (d.members != null) {
                for (String m : d.members) r.addMember(UUID.fromString(m));
            }
            if (d.invites != null) {
                for (String m : d.invites) r.invite(UUID.fromString(m));
            }
            regions.add(r);
        }
    }
}
