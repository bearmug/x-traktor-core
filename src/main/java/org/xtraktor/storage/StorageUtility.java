package org.xtraktor.storage;

import org.xtraktor.HashPoint;

public class StorageUtility {

    String serialize(HashPoint p) {
        return null;
    }

    HashPoint deserialize(String s) {
        return null;
    }

    String generateKey(HashPoint point, int precision) {
        return point.getHash(precision) + "-" + point.getTimestamp();
    }
}
