package org.xtraktor.preprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtraktor.DataPreprocessor;
import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;
import org.xtraktor.RawPoint;
import org.xtraktor.location.LocationConfig;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class SimpleDataPreprocessor implements DataPreprocessor, PointsProcessor {

    private final Logger log = LoggerFactory.getLogger(SimpleDataPreprocessor.class);

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

        log.trace("Filtering and sorting input size: {}", input.size());
        Stream<HashPoint> res = pair(sort(input))
                .parallelStream()
                .filter(point -> point.isValid(config))
                .flatMap(point -> point.interpolate(config));
        log.debug("Filter and sort done for input size: {}", input.size());

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
