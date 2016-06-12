package org.xtraktor;

import java.util.stream.Stream;

public interface DataStorage {

    boolean save(Stream<HashPoint> points);

    Stream<HashPoint> findByHashAndTime(HashPoint input, int hashPrecision);
}
