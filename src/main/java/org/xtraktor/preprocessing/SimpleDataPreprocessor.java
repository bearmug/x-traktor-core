package org.xtraktor.preprocessing;

import org.xtraktor.DataPreprocessor;
import org.xtraktor.LongPoint;
import org.xtraktor.ShortPoint;
import org.xtraktor.location.LocationConfig;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SimpleDataPreprocessor implements DataPreprocessor, PointsProcessor {

    final LocationConfig config;

    public SimpleDataPreprocessor(LocationConfig config) {
        this.config = config;
    }

    @Override
    public List<ShortPoint> normalize(List<LongPoint> input) {

        return pair(sort(input))
                .parallelStream()
                .flatMap(point -> point.interpolate(config).stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<LongPoint> pair(List<LongPoint> input) {
        final AtomicReference<LongPoint> prev = new AtomicReference<>();
        input.forEach(p -> {
            if (prev.get() != null) {
                prev.get().setNextPoint(p);
            }
            prev.set(p);
        });

        return input;
    }

    @Override
    public List<LongPoint> sort(List<LongPoint> input) {
        Collections.sort(input, (p1, p2) -> Long.compare(p1.getTimestamp(), p2.getTimestamp()));
        return input;
    }
}
