package org.xtraktor.storage;

import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;
import redis.clients.jedis.Jedis;

import java.util.stream.Stream;

public class BigLocalDataStorage implements DataStorage {

    private final StorageUtility utility = new StorageUtility();
    private final Jedis jedis;

    private static final int DEFAULT_GEO_HASH_PRECISION = 8;
    private int precision = DEFAULT_GEO_HASH_PRECISION;

    public BigLocalDataStorage(String host, int port) {
        this.jedis = new Jedis(host, port);
    }

    @Override
    public boolean save(Stream<HashPoint> points) {

        points.parallel()
                .forEach(p -> {
                    jedis.rpush(utility.generateKey(p, precision), utility.serialize(p));
                });

        return true;
    }

    @Override
    public Stream<HashPoint> findByHashAndTime(HashPoint input, int hashPrecision) {
        return jedis.blpop(1000, utility.generateKey(input,precision))
                .parallelStream()
                .map(utility::deserialize);
    }
}
