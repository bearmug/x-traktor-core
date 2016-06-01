package org.xtraktor.preprocess;

import org.xtraktor.DataPreprocessor;
import org.xtraktor.LongPoint;
import org.xtraktor.ShortPoint;
import org.xtraktor.location.LocationConfig;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SimpleDataPreprocessor implements DataPreprocessor {

    final LocationConfig config;
    final PointsInterpolation interpolation;

    public SimpleDataPreprocessor(LocationConfig config,
                                  PointsInterpolation interpolation) {
        this.config = config;
        this.interpolation = interpolation;
    }

    @Override
    public List<ShortPoint> normalize(List<LongPoint> input) {

        pair(sort(input))
                .parallelStream()
                .flatMap(point -> interpolation.runInterpolation(point).stream())
                .collect(Collectors.toList());
    }

    private List<LongPoint> pair(List<LongPoint> input) {
        AtomicReference<LongPoint> prev = new AtomicReference<>();
        input.forEach(p -> {
            if (prev.get() != null) {
                prev.get().setNextPoint(p);
            }
            prev.set(p);
        });

        return input;
    }

    private List<LongPoint> sort(List<LongPoint> input) {
        Collections.sort(input, (p1, p2) -> Long.compare(p1.getTimestamp(), p2.getTimestamp()));
        return input;
    }
}
