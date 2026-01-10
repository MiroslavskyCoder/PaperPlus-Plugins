package com.webx.abomination;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class AbominationManager {
    private final JavaPlugin plugin;
    private final NamespacedKey key;
    private final Set<UUID> active = new HashSet<>();
    private int taskId = -1;

    private final double health;
    private final double damage;
    private final double speed;
    private final double kbResist;
    private final boolean fireImmune;
    private final boolean explosionImmune;
    private final boolean useCustomHead;
    private final String skinTextureUrl;

    private final int blindRadius;
    private final int blindDuration;
    private final int blindCooldown;
    private final int summonInterval;
    private final int summonCount;
    private final int summonRadius;

    public AbominationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "abomination");

        this.health = plugin.getConfig().getDouble("stats.health", 120.0);
        this.damage = plugin.getConfig().getDouble("stats.damage", 10.0);
        this.speed = plugin.getConfig().getDouble("stats.speed", 0.32);
        this.kbResist = plugin.getConfig().getDouble("stats.knockback-resist", 0.5);
        this.fireImmune = plugin.getConfig().getBoolean("stats.fire-immune", true);
        this.explosionImmune = plugin.getConfig().getBoolean("stats.explosion-immune", true);
        this.useCustomHead = plugin.getConfig().getBoolean("skin.use-custom-head", true);
        this.skinTextureUrl = plugin.getConfig().getString("skin.texture-url", "");

        this.blindRadius = plugin.getConfig().getInt("abilities.blindness-radius", 12);
        this.blindDuration = plugin.getConfig().getInt("abilities.blindness-duration", 100);
        this.blindCooldown = plugin.getConfig().getInt("abilities.blindness-cooldown", 160);
        this.summonInterval = plugin.getConfig().getInt("abilities.summon-interval", 300);
        this.summonCount = plugin.getConfig().getInt("abilities.summon-count", 3);
        this.summonRadius = plugin.getConfig().getInt("abilities.summon-radius", 4);
    }

    public void startNaturalSpawns() {
        if (!plugin.getConfig().getBoolean("natural-spawn.enabled", true)) return;
        int interval = plugin.getConfig().getInt("natural-spawn.interval-ticks", 600);
        taskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (plugin.getServer().getOnlinePlayers().isEmpty()) return;
            int maxActive = plugin.getConfig().getInt("natural-spawn.max-active", 2);
            if (active.size() >= maxActive) return;
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (!isNight(p.getWorld())) continue;
                spawnAroundPlayer(p);
            }
        }, interval, interval);
    }

    private void spawnAroundPlayer(Player player) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int min = plugin.getConfig().getInt("natural-spawn.min-distance", 20);
        int max = plugin.getConfig().getInt("natural-spawn.max-distance", 35);
        double angle = rnd.nextDouble(0, Math.PI * 2);
        double dist = rnd.nextDouble(min, max);
        int x = player.getLocation().getBlockX() + (int) Math.round(Math.cos(angle) * dist);
        int z = player.getLocation().getBlockZ() + (int) Math.round(Math.sin(angle) * dist);
        World world = player.getWorld();
        int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);
        Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
        spawnAbomination(loc);
    }

    public void spawnAbominationNear(Location around) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        double angle = rnd.nextDouble(0, Math.PI * 2);
        double dist = rnd.nextDouble(8, 12);
        int x = around.getBlockX() + (int) Math.round(Math.cos(angle) * dist);
        int z = around.getBlockZ() + (int) Math.round(Math.sin(angle) * dist);
        World world = around.getWorld();
        int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);
        Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
        spawnAbomination(loc);
    }

    private void spawnAbomination(Location loc) {
        Zombie z = (Zombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
        z.setCustomName(ChatColor.DARK_PURPLE + "Абоминация");
        z.setCustomNameVisible(true);
        z.setAdult();
        z.getEquipment().clear();
        if (useCustomHead && skinTextureUrl != null && !skinTextureUrl.isEmpty()) {
            ItemStack head = SkullUtil.fromUrl(skinTextureUrl);
            if (head != null) {
                z.getEquipment().setHelmet(head);
            } else {
                z.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
            }
        } else {
            z.getEquipment().setHelmet(new ItemStack(Material.NETHERITE_HELMET));
        }
        z.getEquipment().setChestplate(new ItemStack(Material.NETHERITE_CHESTPLATE));
        z.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
        z.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));
        z.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_AXE));
        z.getEquipment().setHelmetDropChance(0f);
        z.getEquipment().setChestplateDropChance(0f);
        z.getEquipment().setLeggingsDropChance(0f);
        z.getEquipment().setBootsDropChance(0f);
        z.getEquipment().setItemInMainHandDropChance(0f);

        setAttribute(z, Attribute.GENERIC_MAX_HEALTH, health);
        z.setHealth(health);
        setAttribute(z, Attribute.GENERIC_ATTACK_DAMAGE, damage);
        setAttribute(z, Attribute.GENERIC_MOVEMENT_SPEED, speed);
        setAttribute(z, Attribute.GENERIC_KNOCKBACK_RESISTANCE, kbResist);

        PersistentDataContainer pdc = z.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.INTEGER, 1);

        active.add(z.getUniqueId());
        scheduleBehaviors(z);

        String msg = plugin.getConfig().getString("messages.spawn", "&5Абоминация появилась!");
        broadcast(loc.getWorld(), ChatColor.translateAlternateColorCodes('&', msg));
    }

    private void scheduleBehaviors(LivingEntity entity) {
        // Targeting and abilities loop
        new BukkitRunnable() {
            int blindTimer = 0;
            int summonTimer = 0;
            @Override
            public void run() {
                // Despawn at daytime
                if (!isNight(entity.getWorld())) {
                    String msg = plugin.getConfig().getString("messages.despawn", "&7Абоминация растворилась на рассвете.");
                    broadcast(entity.getWorld(), ChatColor.translateAlternateColorCodes('&', msg));
                    removeMinions(entity.getWorld());
                    active.remove(entity.getUniqueId());
                    entity.remove();
                    cancel();
                    return;
                }
                if (entity.isDead() || !entity.isValid()) {
                    cancel();
                    active.remove(entity.getUniqueId());
                    return;
                }
                tickTarget(entity);
                if (++blindTimer >= blindCooldown) {
                    blindTimer = 0;
                    applyBlindness(entity);
                }
                if (++summonTimer >= summonInterval) {
                    summonTimer = 0;
                    summonMinions(entity.getLocation());
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void tickTarget(LivingEntity entity) {
        Player nearest = null;
        double best = Double.MAX_VALUE;
        for (Player p : entity.getWorld().getPlayers()) {
            double d = p.getLocation().distanceSquared(entity.getLocation());
            if (d < best && d <= 48 * 48) {
                best = d;
                nearest = p;
            }
        }
        if (nearest != null) {
            if (entity instanceof org.bukkit.entity.Mob mob) {
                mob.setTarget(nearest);
            }
            // Occasional short teleports to keep pressure
            if (ThreadLocalRandom.current().nextDouble() < 0.15) {
                Location base = nearest.getLocation();
                double ang = ThreadLocalRandom.current().nextDouble(0, Math.PI * 2);
                double dist = 4 + ThreadLocalRandom.current().nextDouble(0, 3);
                Location tp = base.clone().add(Math.cos(ang) * dist, 0, Math.sin(ang) * dist);
                tp.setY(entity.getWorld().getHighestBlockYAt(tp, HeightMap.MOTION_BLOCKING) + 1);
                entity.teleport(tp);
            }
        }
    }

    private void applyBlindness(LivingEntity entity) {
        for (Player p : entity.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(entity.getLocation()) <= blindRadius * blindRadius) {
                p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS, blindDuration, 0, true, true, true));
                String msg = plugin.getConfig().getString("messages.blindness", "&7Тьма окутывает вас!");
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
        }
    }

    private void summonMinions(Location loc) {
        for (int i = 0; i < summonCount; i++) {
            double ang = ThreadLocalRandom.current().nextDouble(0, Math.PI * 2);
            double dist = 1 + ThreadLocalRandom.current().nextDouble(0, summonRadius);
            int x = loc.getBlockX() + (int) Math.round(Math.cos(ang) * dist);
            int z = loc.getBlockZ() + (int) Math.round(Math.sin(ang) * dist);
            World world = loc.getWorld();
            int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);
            Location spawn = new Location(world, x + 0.5, y + 1, z + 0.5);
            Zombie minion = (Zombie) world.spawnEntity(spawn, EntityType.ZOMBIE);
            minion.setAdult();
            minion.setCustomName(ChatColor.DARK_GRAY + "Миньон" );
            setAttribute(minion, Attribute.GENERIC_MAX_HEALTH, plugin.getConfig().getDouble("minion.health", 20.0));
            minion.setHealth(plugin.getConfig().getDouble("minion.health", 20.0));
            setAttribute(minion, Attribute.GENERIC_ATTACK_DAMAGE, plugin.getConfig().getDouble("minion.damage", 5.0));
            setAttribute(minion, Attribute.GENERIC_MOVEMENT_SPEED, plugin.getConfig().getDouble("minion.speed", 0.30));
            minion.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 2); // mark as minion
            minion.setTarget(findNearestPlayer(minion.getLocation(), 32));
        }
    }

    private Player findNearestPlayer(Location loc, double radius) {
        Player nearest = null;
        double best = Double.MAX_VALUE;
        for (Player p : loc.getWorld().getPlayers()) {
            double d = p.getLocation().distanceSquared(loc);
            if (d < best && d <= radius * radius) {
                best = d;
                nearest = p;
            }
        }
        return nearest;
    }

    private boolean isNight(World world) {
        long time = world.getTime() % 24000;
        return time >= 13000 && time <= 23000;
    }

    private void broadcast(World world, String msg) {
        for (Player p : world.getPlayers()) {
            p.sendMessage(msg);
        }
    }

    private void setAttribute(LivingEntity entity, Attribute attr, double value) {
        AttributeInstance inst = entity.getAttribute(attr);
        if (inst != null) {
            inst.setBaseValue(value);
        }
    }

    public boolean isAbomination(Entity e) {
        return e.getPersistentDataContainer().has(key, PersistentDataType.INTEGER) && e.getPersistentDataContainer().getOrDefault(key, PersistentDataType.INTEGER, 0) == 1;
    }

    public boolean isMarked(Entity e) {
        return e.getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }

    private void removeMinions(World world) {
        for (Entity e : world.getEntities()) {
            if (!(e instanceof LivingEntity)) continue;
            PersistentDataContainer pdc = e.getPersistentDataContainer();
            if (pdc.has(key, PersistentDataType.INTEGER) && pdc.getOrDefault(key, PersistentDataType.INTEGER, 0) == 2) {
                e.remove();
            }
        }
    }

    public boolean isFireImmune() {
        return fireImmune;
    }

    public boolean isExplosionImmune() {
        return explosionImmune;
    }

    public int removeAll() {
        int removed = 0;
        for (UUID id : new HashSet<>(active)) {
            Entity e = Bukkit.getEntity(id);
            if (e != null) {
                e.remove();
                removed++;
            }
        }
        active.clear();
        return removed;
    }

    public int getActiveCount() {
        return active.size();
    }

    public void cleanup() {
        removeAll();
        if (taskId != -1) {
            plugin.getServer().getScheduler().cancelTask(taskId);
        }
    }
}
