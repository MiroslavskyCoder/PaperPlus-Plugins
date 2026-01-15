package com.webx.redis;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Lightweight Redis helper for simple String/JSON IO backed by Jedis pool.
 */
public class RedisIO implements AutoCloseable {
    private final Logger logger;
    private final Gson gson;
    private final JedisPool pool;

    public RedisIO(RedisConfig cfg, Logger logger) {
        this.logger = logger != null ? logger : Logger.getLogger(RedisIO.class.getName());
        this.gson = new Gson();

        JedisPoolConfig poolCfg = new JedisPoolConfig();
        poolCfg.setMaxTotal(16);
        poolCfg.setMaxIdle(8);
        poolCfg.setMinIdle(1);
        poolCfg.setMaxWait(Duration.ofSeconds(5));

        String password = cfg.password == null || cfg.password.isEmpty() ? null : cfg.password;

        this.pool = new JedisPool(
            poolCfg,
            cfg.host,
            cfg.port,
            cfg.timeoutMillis,
            cfg.socketTimeoutMillis,
            null, // user
            password,
            cfg.database,
            null, // client name
            cfg.ssl
        );
    }

    public boolean ping() {
        return execute(jedis -> "PONG".equalsIgnoreCase(jedis.ping())).orElse(false);
    }

    public Optional<String> getString(String key) {
        return execute(jedis -> jedis.get(key));
    }

    public boolean setString(String key, String value) {
        return execute(jedis -> {
            String res = jedis.set(key, value);
            return "OK".equalsIgnoreCase(res);
        }).orElse(false);
    }

    public boolean setString(String key, String value, Duration ttl) {
        return execute(jedis -> {
            String res = jedis.setex(key, (int) ttl.getSeconds(), value);
            return "OK".equalsIgnoreCase(res);
        }).orElse(false);
    }

    public Optional<JsonElement> getJson(String key) {
        return getString(key).map(json -> gson.fromJson(json, JsonElement.class));
    }

    public boolean setJson(String key, JsonElement json) {
        return setString(key, gson.toJson(json));
    }

    public boolean setJson(String key, JsonElement json, Duration ttl) {
        return setString(key, gson.toJson(json), ttl);
    }

    public long increment(String key) {
        return execute(jedis -> jedis.incr(key)).orElse(0L);
    }

    public boolean delete(String key) {
        return execute(jedis -> jedis.del(key) > 0).orElse(false);
    }

    private <T> Optional<T> execute(Function<Jedis, T> fn) {
        try (Jedis jedis = pool.getResource()) {
            return Optional.ofNullable(fn.apply(jedis));
        } catch (Exception e) {
            logger.warning("RedisIO operation failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void close() {
        if (pool != null) {
            pool.close();
        }
    }
}
