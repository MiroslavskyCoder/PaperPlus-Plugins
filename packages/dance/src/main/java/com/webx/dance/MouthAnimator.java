package com.webx.dance;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MouthAnimator {
    private final JavaPlugin plugin;
    private final Map<UUID, MouthRig> rigs = new HashMap<>();

    public MouthAnimator(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void startTalking(Player p, int durationTicks) {
        if (!plugin.getConfig().getBoolean("settings.mouth.enabled", true)) return;
        MouthRig rig = rigs.computeIfAbsent(p.getUniqueId(), id -> MouthRig.spawn(plugin, p.getEyeLocation()));
        rig.setTalking(true);
        new BukkitRunnable(){
            int tick = 0;
            @Override public void run(){
                if (p.isDead() || !p.isValid()) { stopTalking(p); cancel(); return; }
                if (tick++ >= durationTicks) { stopTalking(p); cancel(); return; }
                rig.animate(p, tick);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public void stopTalking(Player p) {
        MouthRig rig = rigs.remove(p.getUniqueId());
        if (rig != null) rig.remove();
    }

    static class MouthRig {
        private final JavaPlugin plugin;
        private final ArmorStand upperLip;
        private final ArmorStand lowerLip;
        private boolean talking;
        MouthRig(JavaPlugin plugin, ArmorStand up, ArmorStand low){
            this.plugin = plugin; this.upperLip = up; this.lowerLip = low;
        }
        static MouthRig spawn(JavaPlugin plugin, Location eye){
            Location base = eye.clone().add(0, -0.15, 0.18);
            ArmorStand up = (ArmorStand) eye.getWorld().spawnEntity(base, EntityType.ARMOR_STAND);
            ArmorStand low = (ArmorStand) eye.getWorld().spawnEntity(base, EntityType.ARMOR_STAND);
            for (ArmorStand as : new ArmorStand[]{up, low}){
                as.setInvisible(true);
                as.setMarker(true);
                as.setSmall(true);
                as.setGravity(false);
                as.setArms(false);
                as.setBasePlate(false);
            }
            // Use particles as lips if no resource pack
            return new MouthRig(plugin, up, low);
        }
        void setTalking(boolean t){ this.talking = t; }
        void animate(Player p, int tick){
            float yaw = p.getLocation().getYaw();
            Location mouthBase = p.getEyeLocation().clone().add(0, -0.15, 0.18);
            double open = (Math.sin(tick * 0.45) * 0.06) + 0.06; // 0.0..0.12
            Location upLoc = mouthBase.clone().add(0, open * 0.5, 0);
            Location lowLoc = mouthBase.clone().add(0, -open * 0.5, 0);
            if (upperLip != null) upperLip.teleport(upLoc);
            if (lowerLip != null) lowerLip.teleport(lowLoc);
            // Visualize lips with particles
            if (plugin.getConfig().getBoolean("settings.mouth.particles", true)){
                Particle.DustOptions red = new Particle.DustOptions(Color.fromRGB(220,40,40), 0.8f);
                p.getWorld().spawnParticle(Particle.REDSTONE, upLoc, 2, 0.005,0.005,0.005, 0, red);
                p.getWorld().spawnParticle(Particle.REDSTONE, lowLoc, 2, 0.005,0.005,0.005, 0, red);
            }
        }
        void remove(){
            for (ArmorStand as : new ArmorStand[]{upperLip, lowerLip}){
                if (as != null && as.isValid()) as.remove();
            }
        }
    }
}
