package org.xtraktor.storage.redis;

import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.stream.Stream;

public abstract class RedisStorage<T> implements DataStorage<T> {

    final StorageUtility utility = new StorageUtility();
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
                String json = utility.serialize(p);
                jedis.sadd(utility.getLocationKey(p, hashPrecision), json);
                jedis.sadd(utility.getUserKey(p.getUserId()), json);
            });
        }
        return true;
    }

    @Override
    public Stream<T> findByHashAndTime(HashPoint input, int hashPrecision) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.smembers(utility.getLocationKey(input, hashPrecision))
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
            return jedis.smembers(utility.getUserKey(userId))
                    .stream()
                    .map(this::deserialize)
                    .sorted(this::sort);
        }
    }
}

