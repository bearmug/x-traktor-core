package org.xtraktor.storage.redis;

import org.xtraktor.HashPoint;

public class RedisDataStorage extends RedisStorage<HashPoint> {

    public RedisDataStorage(String host, int port) {
        super(host, port);
    }

    @Override
    public HashPoint deserialize(String json) {
        return utility.deserialize(json);
    }

    @Override
    public boolean filter(HashPoint point, HashPoint input) {
        return point != input && point.getUserId() != input.getUserId();
    }

    @Override
    public int sort(HashPoint p1, HashPoint p2) {
        return Long.compare(p1.getTimestamp(), p2.getTimestamp());
    }

    @Override
    public HashPoint toPoint(HashPoint input) {
        return input;
    }
}
