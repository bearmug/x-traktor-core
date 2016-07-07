package org.xtraktor.storage;

import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.stream.Stream;

public class RedisDataStorage implements DataStorage {

    private final StorageUtility utility = new StorageUtility();
    private final JedisPool pool;

    public RedisDataStorage(String host, int port) {
        this.pool = new JedisPool(host, port);
    }

    @Override
    public boolean save(Stream<HashPoint> points, int hashPrecision) {

        try (Jedis jedis = pool.getResource()) {
            points.sequential().forEach(p -> {
                String json = utility.serialize(p);
                jedis.sadd(utility.getLocationKey(p, hashPrecision), json);
                jedis.sadd(utility.getUserKey(p.getUserId()), json);
            });
        }
        return true;
    }

    @Override
    public Stream<HashPoint> findByHashAndTime(HashPoint input, int hashPrecision) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.smembers(utility.getLocationKey(input, hashPrecision))
                    .parallelStream()
                    .map(utility::deserialize)
                    .filter(p ->
                            p != input && p.getUserId() != input.getUserId());
        }

    }

    @Override
    public void clear() {
        try (Jedis jedis = pool.getResource()) {
            jedis.flushAll();
        }
    }

    @Override
    public Stream<HashPoint> routeForUser(long userId) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.smembers(utility.getUserKey(userId))
                    .stream()
                    .map(utility::deserialize)
                    .sorted((p1, p2) -> Long.compare(p1.getTimestamp(), p2.getTimestamp()));
        }
    }
}

