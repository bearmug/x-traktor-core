package org.xtraktor.storage;

import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;
import org.xtraktor.location.LocationConfig;
import redis.clients.jedis.Jedis;

import java.util.stream.Stream;

public class BigLocalDataStorage implements DataStorage {

    private final StorageUtility utility = new StorageUtility();
    private final Jedis jedis;

    int precision = LocationConfig.PRECISION;

    public BigLocalDataStorage(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    @Override
    public boolean save(Stream<HashPoint> points) {

        points.parallel()
                .forEach(p -> {
                    jedis.rpush(utility.getKey(p, precision), utility.serialize(p));
                });

        return true;
    }

    @Override
    public Stream<HashPoint> findByHashAndTime(HashPoint input, int hashPrecision) {
        return jedis.lrange(utility.getKey(input, precision), 0, -1)
                .parallelStream()
                .map(utility::deserialize);
    }
}
