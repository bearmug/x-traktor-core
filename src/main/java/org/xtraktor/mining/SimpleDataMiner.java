package org.xtraktor.mining;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtraktor.DataMiner;
import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;

import java.util.List;
import java.util.stream.Stream;

public class SimpleDataMiner<T> implements DataMiner<T> {

    private final Logger log = LoggerFactory.getLogger(SimpleDataMiner.class);
    private final DataStorage<T> storage;

    public SimpleDataMiner(DataStorage<T> storage) {
        this.storage = storage;
    }

    @Override
    public Stream<T> matchForPoint(HashPoint input, int hashPrecision) {
        log.debug("Lookup for matching around point: {} with precision: {}",
                input, hashPrecision);
        return storage.findByHashAndTime(input,
                Math.min(DataStorage.MAX_HASH_PRECISION, hashPrecision));
    }

    @Override
    public Stream<T> matchForRoute(List<HashPoint> input, int hashPrecision) {

        return input.parallelStream()
                .flatMap(it -> matchForPoint(it, hashPrecision));
    }

    @Override
    public Stream<T> matchForUser(long userId, int hashPrecision) {

        return storage.routeForUser(userId)
                .flatMap(p ->
                        matchForPoint(storage.toPoint(p), hashPrecision));
    }
}
