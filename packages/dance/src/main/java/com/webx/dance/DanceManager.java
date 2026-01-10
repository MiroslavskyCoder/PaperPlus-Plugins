package com.webx.dance;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DanceManager {
    private final JavaPlugin plugin;
    private final Map<String, DanceInfo> dances = new HashMap<>();
    private final Set<UUID> cooldown = new HashSet<>();
    private final Map<UUID, Rig> rigs = new HashMap<>();

    public DanceManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadDances();
    }

    private void loadDances() {
        plugin.saveDefaultConfig();
        var cfg = plugin.getConfig();
        var section = cfg.getConfigurationSection("dances");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                var danceSection = section.getConfigurationSection(key);
                if (danceSection != null) {
                    String display = danceSection.getString("display-name", key);
                    int duration = danceSection.getInt("duration", 30);
                    String desc = danceSection.getString("description", "");
                    dances.put(key.toLowerCase(), new DanceInfo(key, display, duration, desc));
                }
            }
        }
    }

    public void performDance(Player player, String danceNameInput) {
        String danceName = danceNameInput.toLowerCase();
        if (!dances.containsKey(danceName)) {
            player.sendMessage("§cDance not found. Use /dance list");
            return;
        }
        
        UUID pId = player.getUniqueId();
        if (cooldown.contains(pId)) {
            player.sendMessage("§cWait before dancing again");
            return;
        }
        
        DanceInfo dance = dances.get(danceName);
        cooldown.add(pId);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> cooldown.remove(pId), 
            (long) plugin.getConfig().getInt("settings.cooldown-seconds", 2) * 20L);
        
        if (plugin.getConfig().getBoolean("settings.announce-dances", true)) {
            player.sendMessage("§a✨ " + dance.getDisplayName() + " §a✨");
        }
        
        playDanceAnimation(player, dance);
    }

    private void playDanceAnimation(Player player, DanceInfo dance) {
        switch (dance.getName().toLowerCase()) {
            case "floss":
                playFloss(player, dance);
                break;
            case "moonwalk":
                playMoonwalk(player, dance);
                break;
            case "wiggle":
                playWiggle(player, dance);
                break;
            case "jump":
                playJump(player, dance);
                break;
            case "spin":
                playSpin(player, dance);
                break;
            case "nod":
                playNod(player, dance);
                break;
            case "headbang":
                playHeadbang(player, dance);
                break;
            case "standup":
                playStandUp(player, dance);
                break;
            case "crouch":
                playCrouch(player, dance);
                break;
            case "wave":
                playWave(player, dance);
                break;
            case "dab":
                playDab(player, dance);
                break;
            case "breakdance":
                playBreakdance(player, dance);
                break;
            case "robotics":
                playRobotics(player, dance);
                break;
            case "hiphop":
                playHipHop(player, dance);
                break;
            case "ballet":
                playBallet(player, dance);
                break;
            case "rave":
                playRave(player, dance);
                break;
            case "confused":
                playConfused(player, dance);
                break;
            case "celebrate":
                playCelebrate(player, dance);
                break;
            case "twerk":
                playTwerk(player, dance);
                break;
            case "handstand":
                playHandstand(player, dance);
                break;
            case "starjump":
                playStarJump(player, dance);
                break;
            case "zombie":
                playZombie(player, dance);
                break;
            case "disco":
                playDisco(player, dance);
                break;
            default:
                playDefault(player, dance);
        }
    }

    // ===== COMPLEX FULL BODY DANCES =====

    // FLOSS: Side-to-side arm swaying with head rotation and body movement
    private void playFloss(Player p, DanceInfo d) {
        Rig rig = ensureRig(p);
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Head rotates side to side
            rotateYaw(p, (tick % 2 == 0 ? 8 : -8));
            // Body wiggles with pose changes
            if (tick % 4 < 2) pose(p, org.bukkit.entity.Pose.SWIMMING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            // Arm swinging through velocity
            if (tick % 3 == 0) applyVelocity(p, new Vector(0.15, 0, 0));
            else if (tick % 3 == 1) applyVelocity(p, new Vector(-0.15, 0, 0));
            // Rig limb animation
            double swing = Math.sin(tick * 0.30) * 0.35;
            rig.teleportRelative(p,
                    new Vector(0, 1.55, 0),
                    new Vector(0.35 + swing, 1.20, 0),
                    new Vector(-0.35 - swing, 1.20, 0),
                    new Vector(0.18 + swing * 0.4, 0.60, 0),
                    new Vector(-0.18 - swing * 0.4, 0.60, 0));
            rig.poseArms( new EulerAngle(Math.abs(swing) * 0.8, 0, 0), new EulerAngle(Math.abs(swing) * 0.8, 0, 0));
            spawnHair(p, tick);
            spawnParticles(p, tick);
        });
        removeRigLater(p, d.getDuration() + 2);
    }

    // MOONWALK: Backward gliding with arm swings and body sway
    private void playMoonwalk(Player p, DanceInfo d) {
        Rig rig = ensureRig(p);
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Smooth backward movement
            if (!stableCamera()) p.setVelocity(p.getLocation().getDirection().multiply(-0.15));
            // Head and shoulders sway
            rotateYaw(p, (tick % 2 == 0 ? 3 : -3));
            setPitch(p, (float)(5 + (tick % 8 < 4 ? -5 : 5)));
            // Body pose for rhythm
            if (tick % 6 < 3) pose(p, org.bukkit.entity.Pose.SWIMMING);
            // Rig: gentle arm swings backward
            double phase = Math.sin(tick * 0.25);
            rig.teleportRelative(p,
                    new Vector(0, 1.55, 0),
                    new Vector(0.35 + phase * 0.2, 1.20, -0.10),
                    new Vector(-0.35 - phase * 0.2, 1.20, -0.10),
                    new Vector(0.18, 0.60, -0.05),
                    new Vector(-0.18, 0.60, -0.05));
            rig.poseArms( new EulerAngle(0.2 + phase * 0.3, 0, 0), new EulerAngle(0.2 - phase * 0.3, 0, 0));
            spawnHair(p, tick);
            if (tick % 5 == 0) spawnParticles(p, tick);
        });
        removeRigLater(p, d.getDuration() + 2);
    }

    // WIGGLE: Hip shaking with torso twisting and rapid movements
    private void playWiggle(Player p, DanceInfo d) {
        Rig rig = ensureRig(p);
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Hip wiggle - rapid side to side
            if (tick % 3 == 0) rotateYaw(p, 15);
            else if (tick % 3 == 1) rotateYaw(p, -15);
            // Body crouches and stands rhythmically
            if (tick % 2 == 0) pose(p, org.bukkit.entity.Pose.SNEAKING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            // Velocity for hip movement
            if (tick % 4 < 2) applyVelocity(p, new Vector(0.1, 0, 0));
            else applyVelocity(p, new Vector(-0.1, 0, 0));
            // Rig limbs wiggle
            double s = Math.sin(tick * 0.35);
            rig.teleportRelative(p,
                    new Vector(0, 1.55, 0),
                    new Vector(0.35 + s * 0.25, 1.20, 0),
                    new Vector(-0.35 - s * 0.25, 1.20, 0),
                    new Vector(0.18 + s * 0.15, 0.60, 0),
                    new Vector(-0.18 - s * 0.15, 0.60, 0));
            rig.poseArms( new EulerAngle(0.4 + s * 0.4, 0, 0), new EulerAngle(0.4 - s * 0.4, 0, 0));
            spawnHair(p, tick);
        });
        removeRigLater(p, d.getDuration() + 2);
    }

    // JUMP: Continuous jumping with arm movements and head motion
    private void playJump(Player p, DanceInfo d) {
        Rig rig = ensureRig(p);
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Jump every 6 ticks
            if (tick % 6 == 0 && p.isOnGround()) {
                applyVelocity(p, new Vector(0, 0.8, 0));
                playSound(p, Sound.ENTITY_PLAYER_HURT);
            }
            // Head nod with jump
            setPitch(p, (float)((tick % 6) * 5 - 15));
            // Arms swing
            if (tick % 3 == 0) rotateYaw(p, 5);
            // Body pose for intensity
            if (tick % 4 < 2) pose(p, org.bukkit.entity.Pose.SWIMMING);
            // Rig: arms up-down with jumps
            double up = (tick % 6 == 0) ? 0.25 : 0.0;
            double ap = Math.sin(tick * 0.30) * 0.25;
            rig.teleportRelative(p,
                    new Vector(0, 1.55 + up, 0),
                    new Vector(0.35, 1.20 + ap, 0),
                    new Vector(-0.35, 1.20 - ap, 0),
                    new Vector(0.18, 0.60, 0),
                    new Vector(-0.18, 0.60, 0));
            rig.poseArms( new EulerAngle(0.8 + ap, 0, 0), new EulerAngle(0.8 - ap, 0, 0));
            spawnHair(p, tick);
            spawnParticles(p, tick);
        });
        removeRigLater(p, d.getDuration() + 2);
    }

    // SPIN: Fast 360 spin with head tilting and arm extension
    private void playSpin(Player p, DanceInfo d) {
        Rig rig = ensureRig(p);
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Full fast rotation
            rotateYaw(p, 18);
            // Head tilts forward and back
            setPitch(p, (float)(tick % 10 < 5 ? -25 : 25));
            // Arm extension through pose
            if (tick % 4 < 2) pose(p, org.bukkit.entity.Pose.SWIMMING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            // Rig arms circling around body
            double radius = 0.38;
            double ang = tick * 0.25;
            rig.teleportRelative(p,
                    new Vector(0, 1.55, 0),
                    new Vector(radius * Math.cos(ang), 1.20, radius * Math.sin(ang)),
                    new Vector(radius * Math.cos(ang + Math.PI), 1.20, radius * Math.sin(ang + Math.PI)),
                    new Vector(0.18, 0.60, 0),
                    new Vector(-0.18, 0.60, 0));
            rig.poseArms( new EulerAngle(0.6, 0, 0), new EulerAngle(0.6, 0, 0));
            spawnHair(p, tick);
            spawnParticles(p, tick);
        });
        removeRigLater(p, d.getDuration() + 2);
    }

    // NOD: Head nodding with upper body movement and shoulder shrug
    private void playNod(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Nod up and down
            setPitch(p, (float)(tick % 10 < 5 ? -30 : 30));
            // Shoulder shrug with pose change
            if (tick % 10 < 5) pose(p, org.bukkit.entity.Pose.SWIMMING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            // Small yaw for expression
            if (tick % 8 == 0) rotateYaw(p, 3);
        });
    }

    // HEADBANG: Aggressive head banging with body slam and sounds
    private void playHeadbang(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Aggressive pitch change
            setPitch(p, (float)(tick % 4 < 2 ? -50 : 50));
            // Body movement
            if (tick % 4 == 0) {
                playSound(p, Sound.BLOCK_ANVIL_LAND);
                p.setVelocity(new Vector(0, tick % 8 == 0 ? 0.15 : 0, 0));
            }
            // Pose changes for intensity
            if (tick % 2 == 0) pose(p, org.bukkit.entity.Pose.SWIMMING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            spawnParticles(p, tick);
        });
    }

    // STANDUP: Rising from crouch to standing with arms up
    private void playStandUp(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            pose(p, org.bukkit.entity.Pose.STANDING);
            // Head rises
            float pitch = Math.max(-30, -30 + (tick * 2));
            setPitch(p, pitch);
            // Arms stretch via pose
            if (tick % 3 == 0) rotateYaw(p, 2);
            if (tick % 5 == 0) spawnParticles(p, tick);
        });
    }

    // CROUCH: Crouching with hugging pose and swaying
    private void playCrouch(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            pose(p, org.bukkit.entity.Pose.SNEAKING);
            // Head down
            setPitch(p, 40);
            // Swaying while crouched
            if (tick % 4 < 2) rotateYaw(p, 8);
            else rotateYaw(p, -8);
            spawnParticles(p, tick);
        });
    }

    // WAVE: Waving hand with body sway and expressions
    private void playWave(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Arm wave motion with upper body
            float yaw = p.getLocation().getYaw();
            if (tick % 5 < 2) {
                rotateYaw(p, 12);
                setPitch(p, -10);
            } else if (tick % 5 >= 3) {
                rotateYaw(p, -12);
                setPitch(p, 10);
            }
            // Friendly pose
            if (tick % 2 == 0) pose(p, org.bukkit.entity.Pose.STANDING);
            if (tick % 3 == 0) spawnParticles(p, tick);
        });
    }

    // DAB: Quick pose down to elbow with hip turn and finality
    private void playDab(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Dab pose - head down to elbow
            if (tick < 15) {
                float pitch = -20 - (tick * 2);
                setPitch(p, (float)Math.min(-60, pitch));
                rotateYaw(p, 15);
                pose(p, org.bukkit.entity.Pose.SWIMMING);
                spawnParticles(p, tick);
            } else {
                pose(p, org.bukkit.entity.Pose.STANDING);
                setPitch(p, 0);
            }
        });
    }

    // BREAKDANCE: Explosive spins with high jumps and freezes
    private void playBreakdance(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Jump sequence
            if (p.isOnGround() && tick % 10 == 0) {
                applyVelocity(p, new Vector(0, 1.3, 0));
                playSound(p, Sound.ENTITY_PLAYER_HURT);
            }
            // Fast spinning
            rotateYaw(p, 15);
            // Body pose flips
            if (tick % 6 < 3) {
                pose(p, org.bukkit.entity.Pose.SWIMMING);
                setPitch(p, 20);
            } else {
                pose(p, org.bukkit.entity.Pose.STANDING);
                setPitch(p, 0);
            }
            spawnParticles(p, tick);
        });
    }

    // ROBOTICS: Mechanical rigid movements and jerks
    private void playRobotics(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Mechanical 90 degree turns
            if (tick % 5 == 0) {
                rotateYaw(p, 45);
            }
            // Rigid pose switching
            if (tick % 4 == 0) {
                pose(p, org.bukkit.entity.Pose.SWIMMING);
                playSound(p, Sound.BLOCK_METAL_PLACE);
            } else if (tick % 4 == 2) {
                pose(p, org.bukkit.entity.Pose.STANDING);
            }
            // Jerky movement
            if (tick % 6 == 0) applyVelocity(p, new Vector(0.2, 0, 0));
            spawnParticles(p, tick);
        });
    }

    // HIPHOP: Hip shaking with arm movements and swagger
    private void playHipHop(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Hip shaking left and right
            if (tick % 6 < 3) {
                rotateYaw(p, 8);
                pose(p, org.bukkit.entity.Pose.SNEAKING);
                setPitch(p, -5);
            } else {
                rotateYaw(p, -8);
                pose(p, org.bukkit.entity.Pose.STANDING);
                setPitch(p, 5);
            }
            // Arm swagger
            if (tick % 4 < 2) applyVelocity(p, new Vector(0.1, 0, 0));
            else applyVelocity(p, new Vector(-0.1, 0, 0));
            spawnParticles(p, tick);
        });
    }

    // BALLET: Graceful spinning and jumping with elegant poses
    private void playBallet(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Graceful jumping
            if (tick % 8 < 4 && p.isOnGround()) {
                p.setVelocity(new Vector(0, 0.6, 0));
            }
            // Spinning gracefully
            rotateYaw(p, 6);
            // Elegant pose
            if (tick % 10 < 5) pose(p, org.bukkit.entity.Pose.SWIMMING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            // Head position
            setPitch(p, (float)(-10 + (tick % 8 < 4 ? -5 : 5)));
            spawnParticles(p, tick);
        });
    }

    // RAVE: Wild jumping and spinning with light effects
    private void playRave(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Wild spinning
            rotateYaw(p, 18);
            // Jumping
            if (tick % 4 == 0 && p.isOnGround()) {
                applyVelocity(p, new Vector(0, 0.8, 0));
            }
            // Body movement
            if (tick % 3 == 0) pose(p, org.bukkit.entity.Pose.SWIMMING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            // Effects
            if (tick % 2 == 0) {
                p.getWorld().spawnParticle(Particle.FLAME, p.getEyeLocation(), 4, 0.4, 0.4, 0.4, 0.15);
                playSound(p, Sound.BLOCK_BEACON_POWER_SELECT);
            }
        });
    }

    // CONFUSED: Confused head scratching and swaying
    private void playConfused(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Confused scratching motion
            float pitch = tick % 8 < 4 ? 35 : -35;
            setPitch(p, pitch);
            float yaw = tick % 6 < 3 ? 25 : -25;
            rotateYaw(p, yaw);
            // Uncertain body pose
            if (tick % 4 < 2) pose(p, org.bukkit.entity.Pose.SNEAKING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            // Swaying
            if (tick % 3 == 0) applyVelocity(p, new Vector(0.08, 0, 0));
        });
    }

    // CELEBRATE: Celebration jumps with spins and cheers
    private void playCelebrate(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Big celebration jumps
            if (tick % 6 == 0 && p.isOnGround()) {
                applyVelocity(p, new Vector(0, 1.0, 0));
                playSound(p, Sound.ENTITY_PLAYER_LEVELUP);
                spawnParticles(p, tick);
            }
            // Spinning while celebrating
            rotateYaw(p, 10);
            // Cheering pose
            if (tick % 3 < 2) pose(p, org.bukkit.entity.Pose.SWIMMING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            // Head back in celebration
            setPitch(p, (float)(-20 + (tick % 4 * 5)));
        });
    }

    // TWERK: Rhythmic hip shaking with body bounce
    private void playTwerk(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Rapid hip movement
            if (tick % 3 == 0) {
                pose(p, org.bukkit.entity.Pose.SNEAKING);
                applyVelocity(p, new Vector(0, 0.1, 0));
            } else if (tick % 3 == 1) {
                pose(p, org.bukkit.entity.Pose.STANDING);
                applyVelocity(p, new Vector(0, -0.05, 0));
            }
            // Twisting
            rotateYaw(p, (tick % 2 == 0 ? 6 : -6));
            // Leaning back
            setPitch(p, (float)(-10 + (tick % 6 * 3)));
            spawnParticles(p, tick);
        });
    }

    // HANDSTAND: Upside down pose with rotation
    private void playHandstand(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Handstand pose - head down
            setPitch(p, 180);
            // Slow rotation while upside down
            if (tick % 4 == 0) rotateYaw(p, 8);
            // Body stays rigid
            pose(p, org.bukkit.entity.Pose.SWIMMING);
            spawnParticles(p, tick);
        });
    }

    // STARJUMP: High jumping with arms and legs spread
    private void playStarJump(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Star jump - big jumps
            if (tick % 8 == 0 && p.isOnGround()) {
                applyVelocity(p, new Vector(0, 1.5, 0));
                playSound(p, Sound.ENTITY_PLAYER_HURT);
                spawnParticles(p, tick);
            }
            // Spinning during jump
            rotateYaw(p, 10);
            // Arms spread pose
            if (tick % 4 < 2) pose(p, org.bukkit.entity.Pose.SWIMMING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
        });
    }

    // ZOMBIE: Zombie shuffle - slow movement with jerks and sounds
    private void playZombie(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Slow zombie shuffle backward
            if (!stableCamera()) p.setVelocity(p.getLocation().getDirection().multiply(-0.1));
            // Zombie head jerks
            if (tick % 4 == 0) setPitch(p, -25);
            else if (tick % 4 == 2) setPitch(p, 25);
            // Jerky arms
            if (tick % 6 < 3) pose(p, org.bukkit.entity.Pose.SWIMMING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            // Moaning sound
            if (tick % 10 == 0) playSound(p, Sound.ENTITY_ZOMBIE_AMBIENT);
            spawnParticles(p, tick);
        });
    }

    // DISCO: Disco fever - spinning with light show and rhythms
    private void playDisco(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            // Disco spinning
            rotateYaw(p, 12);
            // Body rhythm
            if (tick % 6 < 3) {
                pose(p, org.bukkit.entity.Pose.SNEAKING);
                setPitch(p, -5);
            } else {
                pose(p, org.bukkit.entity.Pose.STANDING);
                setPitch(p, 5);
            }
            // Disco light effects
            if (tick % 3 == 0) {
                p.getWorld().spawnParticle(Particle.FLAME, p.getEyeLocation(), 3, 0.3, 0.3, 0.3, 0.1);
                playSound(p, Sound.BLOCK_NOTE_BLOCK_BASEDRUM);
            }
            // Hip movement
            if (tick % 4 < 2) applyVelocity(p, new Vector(0.08, 0, 0));
            else applyVelocity(p, new Vector(-0.08, 0, 0));
        });
    }

    // DEFAULT: Simple bobbing dance with basic movements
    private void playDefault(Player p, DanceInfo d) {
        scheduleComplexAnimation(p, d.getDuration(), tick -> {
            rotateYaw(p, 5);
            if (tick % 4 < 2) pose(p, org.bukkit.entity.Pose.SWIMMING);
            else pose(p, org.bukkit.entity.Pose.STANDING);
            if (tick % 5 == 0) spawnParticles(p, tick);
        });
    }

    private void scheduleComplexAnimation(Player p, int durationTicks, java.util.function.Consumer<Integer> anim) {
        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                if (tick >= durationTicks || p.isDead() || !p.isValid()) {
                    cancel();
                    return;
                }
                anim.accept(tick++);
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    private void spawnParticles(Player p, int tick) {
        if (!plugin.getConfig().getBoolean("settings.particles", true)) return;
        p.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, p.getLocation().add(0, 1, 0), 2, 0.3, 0.3, 0.3, 0.05);
    }

    private void playSound(Player p, Sound sound) {
        if (!plugin.getConfig().getBoolean("settings.sounds", true)) return;
        p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
    }

    // ===== Helpers to prevent camera shake =====
    private boolean stableCamera() {
        return plugin.getConfig().getBoolean("settings.stable-camera", true);
    }

    private void pose(Player p, org.bukkit.entity.Pose pose) {
        if (stableCamera()) {
            // Keep camera stable by avoiding frequent pose toggles; default to standing
            p.setPose(org.bukkit.entity.Pose.STANDING);
            return;
        }
        p.setPose(pose);
    }

    private void applyVelocity(Player p, Vector delta) {
        if (stableCamera()) return; // skip movement to keep camera steady
        p.setVelocity(p.getVelocity().add(delta));
    }

    private void rotateYaw(Player p, float delta) {
        if (stableCamera()) return; // avoid rotating camera
        p.getLocation().setYaw(p.getLocation().getYaw() + delta);
    }

    private void setPitch(Player p, float pitch) {
        if (stableCamera()) return; // avoid changing camera pitch
        p.getLocation().setPitch(pitch);
    }

    // ===== ArmorStand limb rig =====
    private Rig ensureRig(Player p) {
        if (!plugin.getConfig().getBoolean("settings.use-rig", true)) return Rig.EMPTY;
        return rigs.computeIfAbsent(p.getUniqueId(), id -> Rig.spawn(plugin, p.getLocation()));
    }

    private void removeRigLater(Player p, int delayTicks) {
        Rig rig = rigs.get(p.getUniqueId());
        if (rig == null || rig == Rig.EMPTY) return;
        new BukkitRunnable() {
            @Override public void run() {
                Rig r = rigs.remove(p.getUniqueId());
                if (r != null) r.remove();
            }
        }.runTaskLater(plugin, delayTicks);
    }

    private void spawnHair(Player p, int tick) {
        if (!plugin.getConfig().getBoolean("settings.hair-particles", true)) return;
        double r = 0.18;
        double a = (tick % 20) * (Math.PI / 10);
        Location base = p.getEyeLocation().clone().add(0, 0.15, 0);
        Location l1 = base.clone().add(r * Math.cos(a), 0, r * Math.sin(a));
        Location l2 = base.clone().add(r * Math.cos(a + Math.PI), 0, r * Math.sin(a + Math.PI));
        Particle.DustOptions opts = new Particle.DustOptions(Color.fromRGB(180, 60, 60), 0.8f);
        p.getWorld().spawnParticle(Particle.REDSTONE, l1, 2, 0.01, 0.01, 0.01, 0, opts);
        p.getWorld().spawnParticle(Particle.REDSTONE, l2, 2, 0.01, 0.01, 0.01, 0, opts);
        // Eye sparkle
        if (tick % 12 == 0) {
            p.getWorld().spawnParticle(Particle.END_ROD, base, 1, 0.02, 0.02, 0.02, 0.0);
        }
    }

    private static Vector rotateY(Vector v, float yawDeg) {
        double yaw = Math.toRadians(yawDeg);
        double cos = Math.cos(yaw);
        double sin = Math.sin(yaw);
        return new Vector(v.getX() * cos - v.getZ() * sin, v.getY(), v.getX() * sin + v.getZ() * cos);
    }

    private static class Rig {
        static final Rig EMPTY = new Rig(null, null, null, null, null, null);
        private final JavaPlugin plugin;
        private final ArmorStand head;
        private final ArmorStand leftArm;
        private final ArmorStand rightArm;
        private final ArmorStand leftLeg;
        private final ArmorStand rightLeg;

        private Rig(JavaPlugin plugin, ArmorStand head, ArmorStand leftArm, ArmorStand rightArm, ArmorStand leftLeg, ArmorStand rightLeg) {
            this.plugin = plugin;
            this.head = head;
            this.leftArm = leftArm;
            this.rightArm = rightArm;
            this.leftLeg = leftLeg;
            this.rightLeg = rightLeg;
        }

        static Rig spawn(JavaPlugin plugin, Location base) {
            Location loc = base.clone();
            ArmorStand head = (ArmorStand) base.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            ArmorStand la = (ArmorStand) base.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            ArmorStand ra = (ArmorStand) base.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            ArmorStand ll = (ArmorStand) base.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            ArmorStand rl = (ArmorStand) base.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
            for (ArmorStand as : new ArmorStand[]{head, la, ra, ll, rl}) {
                as.setInvisible(true);
                as.setMarker(true);
                as.setSmall(true);
                as.setBasePlate(false);
                as.setGravity(false);
                as.setArms(true);
            }
            // Equip arms with rods to visualize movement
            ra.getEquipment().setItemInMainHand(new org.bukkit.inventory.ItemStack(org.bukkit.Material.BLAZE_ROD));
            la.getEquipment().setItemInMainHand(new org.bukkit.inventory.ItemStack(org.bukkit.Material.BLAZE_ROD));
            return new Rig(plugin, head, la, ra, ll, rl);
        }

        void teleportRelative(Player p, Vector headOff, Vector lArmOff, Vector rArmOff, Vector lLegOff, Vector rLegOff) {
            float yaw = p.getLocation().getYaw();
            Location base = p.getLocation();
            if (head != null) head.teleport(base.clone().add(rotateY(headOff, yaw)));
            if (leftArm != null) leftArm.teleport(base.clone().add(rotateY(lArmOff, yaw)));
            if (rightArm != null) rightArm.teleport(base.clone().add(rotateY(rArmOff, yaw)));
            if (leftLeg != null) leftLeg.teleport(base.clone().add(rotateY(lLegOff, yaw)));
            if (rightLeg != null) rightLeg.teleport(base.clone().add(rotateY(rLegOff, yaw)));
        }

        void poseArms(EulerAngle left, EulerAngle right) {
            if (leftArm != null) leftArm.setLeftArmPose(left);
            if (rightArm != null) rightArm.setRightArmPose(right);
        }

        void remove() {
            for (ArmorStand as : new ArmorStand[]{head, leftArm, rightArm, leftLeg, rightLeg}) {
                if (as != null && as.isValid()) as.remove();
            }
        }
    }

    public Collection<DanceInfo> getAllDances() {
        return dances.values();
    }

    public Optional<DanceInfo> getDance(String name) {
        return Optional.ofNullable(dances.get(name.toLowerCase()));
    }
}

