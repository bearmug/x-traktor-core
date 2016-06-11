package org.xtraktor.mining;

import org.xtraktor.DataMiner;
import org.xtraktor.HashPoint;
import org.xtraktor.location.LocationConfig;
import org.xtraktor.DataStorage;

import java.util.List;
import java.util.stream.Stream;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.collect;

public class SimpleDataMiner implements DataMiner {

    private final DataStorage storage;

    public SimpleDataMiner(DataStorage storage) {
        this.storage = storage;
    }

    @Override
    public Stream<HashPoint> matchForPoint(HashPoint input, int hashPrecision) {
        return storage.findByHashAndTime(input);
    }

    @Override
    public Stream<HashPoint> matchForRoute(List<HashPoint> input, int hashPrecision) {

        return input.parallelStream()
                .flatMap(it -> matchForPoint(it, hashPrecision));
    }
}
