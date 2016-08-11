package org.xtraktor.storage.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.stream.Stream;

public abstract class RedisStorage<T> implements DataStorage<T> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JedisPool pool;

    public RedisStorage(String host, int port) {
        this.pool = new JedisPool(host, port);
    }

    public abstract T deserialize(String json);

    public abstract boolean filter(T point, HashPoint input);

    public abstract int sort(T p1, T p2);

    @Override
    public boolean save(Stream<HashPoint> points, int hashPrecision) {

        try (Jedis jedis = pool.getResource()) {
            points.sequential().forEach(p -> {
                String json = serializePoint(p);
                jedis.sadd(getLocationKey(p, hashPrecision), json);
                jedis.sadd(getUserKey(p.getUserId()), json);
            });
        }
        return true;
    }

    @Override
    public Stream<T> findByHashAndTime(HashPoint input, int hashPrecision) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.smembers(getLocationKey(input, hashPrecision))
                    .parallelStream()
                    .map(this::deserialize)
                    .filter(p -> this.filter(p, input));
        }

    }

    @Override
    public void clear() {
        try (Jedis jedis = pool.getResource()) {
            jedis.flushAll();
        }
    }

    @Override
    public Stream<T> routeForUser(long userId) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.smembers(getUserKey(userId))
                    .stream()
                    .map(this::deserialize)
                    .sorted(this::sort);
        }
    }

    public String serializePoint(HashPoint p) {
        try {
            return objectMapper.writeValueAsString(p);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Can not serialize: " + p, e);
        }
    }

    public HashPoint deserializePoint(String s) {
        try {
            return objectMapper.readValue(s, HashPoint.class);
        } catch (IOException e) {
            throw new IllegalStateException("Can not parse: " + s, e);
        }
    }

    public static String getLocationKey(HashPoint point, int precision) {
        return point.getHash(precision) + "-" + point.getTimestamp();
    }

    private static String getUserKey(long userId) {
        return "user-" + userId;
    }
}

