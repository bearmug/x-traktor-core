package org.xtraktor;

import org.xtraktor.HashPoint;

import java.util.stream.Stream;

public interface DataStorage {

    boolean save(Stream<HashPoint> points);

    Stream<HashPoint> findByHashAndTime(HashPoint input);
}
