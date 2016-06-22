package org.xtraktor.preprocessing;

import org.xtraktor.DataPreprocessor;
import org.xtraktor.HashPoint;
import org.xtraktor.RawPoint;
import org.xtraktor.location.LocationConfig;
import org.xtraktor.DataStorage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class SimpleDataPreprocessor implements DataPreprocessor, PointsProcessor {

    private final LocationConfig config;
    private final DataStorage storage;

    public SimpleDataPreprocessor(LocationConfig config, DataStorage storage) {
        this.config = config;
        this.storage = storage;
    }

    public SimpleDataPreprocessor(LocationConfig config) {
        this(config, null);
    }

    @Override
    public Stream<HashPoint> normalize(List<RawPoint> input) {

        Stream<HashPoint> res = pair(sort(input))
                .parallelStream()
                .filter(point -> point.isValid(config))
                .flatMap(point -> point.interpolate(config));

        if (storage != null) {
            storage.save(res, config.getHashPrecision());
        }

        return res;
    }

    @Override
    public List<RawPoint> pair(List<RawPoint> input) {
        final AtomicReference<RawPoint> prev = new AtomicReference<>();
        input.forEach(p -> {
            if (prev.get() != null) {
                prev.get().setNextPoint(p);
            }
            prev.set(p);
        });

        return input;
    }

    @Override
    public List<RawPoint> sort(List<RawPoint> input) {
        Collections.sort(input, (p1, p2) -> Long.compare(p1.getTimestamp(), p2.getTimestamp()));
        return input;
    }
}
