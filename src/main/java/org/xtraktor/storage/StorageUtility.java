package org.xtraktor.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.xtraktor.HashPoint;

import java.io.IOException;

public class StorageUtility {

    private final ObjectMapper mapper = new ObjectMapper();

    String serialize(HashPoint p) {
        try {
            return mapper.writeValueAsString(p);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Can not serialize: " + p);
        }
    }

    HashPoint deserialize(String s) {
        try {
            return mapper.readValue(s, HashPoint.class);
        } catch (IOException e) {
            throw new IllegalStateException("Can not parse: " + s);
        }
    }

    String generateKey(HashPoint point, int precision) {
        return point.getHash(precision) + "-" + point.getTimestamp();
    }
}
