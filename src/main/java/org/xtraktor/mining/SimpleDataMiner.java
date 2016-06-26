package org.xtraktor.mining;

import org.xtraktor.DataMiner;
import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;

import java.util.List;
import java.util.stream.Stream;

public class SimpleDataMiner implements DataMiner {

    private final DataStorage storage;

    public SimpleDataMiner(DataStorage storage) {
        this.storage = storage;
    }

    @Override
    public Stream<HashPoint> matchForPoint(HashPoint input, int hashPrecision) {
        return storage.findByHashAndTime(input,
                Math.min(DataStorage.MAX_HASH_PRECISION, hashPrecision));
    }

    @Override
    public Stream<HashPoint> matchForRoute(List<HashPoint> input, int hashPrecision) {

        return input.parallelStream()
                .flatMap(it -> matchForPoint(it, hashPrecision));
    }

    @Override
    public Stream<HashPoint> matchForUser(long userId, int hashPrecision) {

        return storage.routeForUser(userId)
                .flatMap(p ->
                        matchForPoint(p, hashPrecision))
                .sorted((p1, p2) ->
                        Long.compare(p1.getTimestamp(), p2.getTimestamp()));
    }

    @Override
    public DataStorage getStorage() {
        return storage;
    }
}
