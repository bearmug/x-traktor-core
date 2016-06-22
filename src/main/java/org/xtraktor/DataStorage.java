package org.xtraktor;

import java.util.stream.Stream;

public interface DataStorage {

    int MAX_HASH_PRECISION = 8;

    boolean save(Stream<HashPoint> points);

    Stream<HashPoint> findByHashAndTime(HashPoint input, int hashPrecision);

    void clear();
}
