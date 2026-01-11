package com.webx.hdphysicssound.engine;

import com.webx.hdphysicssound.config.PhysicsConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class SoundPhysicsEngine {

    private final org.bukkit.plugin.Plugin plugin;
    private PhysicsConfig config;

    public SoundPhysicsEngine(org.bukkit.plugin.Plugin plugin, PhysicsConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void setConfig(PhysicsConfig config) {
        this.config = config;
    }

    public void broadcastPhysicalSound(Location source, Sound sound, SoundCategory category, float baseVolume, float basePitch) {
        World world = source.getWorld();
        double maxDistance = config.getMaxDistance();

        List<Player> listeners = world.getPlayers().stream()
            .filter(p -> p.getLocation().distanceSquared(source) <= maxDistance * maxDistance)
            .limit(config.getMaxPlayersPerTick())
            .collect(Collectors.toList());

        OcclusionCalculator occlusionCalculator = new OcclusionCalculator(world);
        ReverbProfile reverbProfile = new ReverbProfile(
            config.isReverbEnabled(),
            config.getReverbRoomScale(),
            config.getReverbDecay(),
            config.getReverbWetGain()
        );

        for (Player listener : listeners) {
            double attenuation = computeAttenuation(source, listener, occlusionCalculator);
            if (attenuation <= 0) {
                continue;
            }

            float volume = (float) (baseVolume * attenuation);
            if (volume < config.getMinAudibleVolume()) {
                continue;
            }

            listener.playSound(source, sound, category, volume, basePitch);

            // Simple reverb tail
            if (reverbProfile.isEnabled()) {
                int delay = reverbProfile.computeDelayTicks(source, listener.getLocation());
                float wet = reverbProfile.computeWetVolume(volume);
                if (wet > config.getMinAudibleVolume()) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        listener.playSound(source, sound, category, wet, basePitch * 0.95f);
                    }, delay);
                }
            }
        }
    }

    private double computeAttenuation(Location source, Player listener, OcclusionCalculator occlusionCalculator) {
        double distance = source.distance(listener.getEyeLocation());
        if (distance > config.getMaxDistance()) {
            return 0.0;
        }

        double airLoss = Math.max(0.0, 1.0 - distance * config.getAirAbsorptionPerMeter());
        double occlusion = occlusionCalculator.calculate(source, listener, config.getOcclusionSteps(), config.getOcclusionPenalty());
        double occludedVolume = Math.max(config.getMinOcclusionVolume(), occlusion);

        return airLoss * occludedVolume;
    }
}
