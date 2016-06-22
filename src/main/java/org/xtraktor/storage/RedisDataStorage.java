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
            points.forEach(p -> {
                jedis.rpush(utility.getKey(p, hashPrecision), utility.serialize(p));
            });
        }
        return true;
    }

    @Override
    public Stream<HashPoint> findByHashAndTime(HashPoint input, int hashPrecision) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.lrange(utility.getKey(input, hashPrecision), 0, -1)
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
}
