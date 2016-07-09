package org.xtraktor.storage.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.xtraktor.HashPoint;

import java.io.IOException;
import java.net.ServerSocket;

public class StorageUtility {

    private final ObjectMapper mapper = new ObjectMapper();

    String serialize(HashPoint p) {
        try {
            return mapper.writeValueAsString(p);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Can not serialize: " + p, e);
        }
    }

    HashPoint deserialize(String s) {
        try {
            return mapper.readValue(s, HashPoint.class);
        } catch (IOException e) {
            throw new IllegalStateException("Can not parse: " + s, e);
        }
    }

    String getLocationKey(HashPoint point, int precision) {
        return point.getHash(precision) + "-" + point.getTimestamp();
    }

    String getUserKey(long userId) {
        return "user-" + userId;
    }

    public int getFreePort() {
        try (ServerSocket tempServer = new ServerSocket(0)) {
            return tempServer.getLocalPort();
        } catch (IOException e) {
            throw new IllegalStateException("Can not allocate free port", e);
        }
    }
}
