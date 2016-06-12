package org.xtraktor.storage;

import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;

import java.util.stream.Stream;

public class BigLocalDataStorage implements DataStorage {
    @Override
    public boolean save(Stream<HashPoint> points) {
        return false;
    }

    @Override
    public Stream<HashPoint> findByHashAndTime(HashPoint input, int hashPrecision) {
        return null;
    }
}
