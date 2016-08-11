package org.xtraktor.storage.redis;

import org.xtraktor.HashPoint;

public class RedisJsonStorage extends RedisStorage<String> {

    public RedisJsonStorage(String host, int port) {
        super(host, port);
    }

    @Override
    public String deserialize(String json) {
        return json;
    }

    @Override
    public boolean filter(String point, HashPoint input) {
        return true;
    }

    @Override
    public int sort(String p1, String p2) {
        return 0;
    }

    @Override
    public HashPoint toPoint(String input) {
        return deserializePoint(input);
    }
}
