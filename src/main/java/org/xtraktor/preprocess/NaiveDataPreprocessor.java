package org.xtraktor.preprocess;

import org.xtraktor.DataPreprocessor;
import org.xtraktor.LongPoint;
import org.xtraktor.ShortPoint;
import org.xtraktor.location.LocationConfig;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Running logic same to {@link SimpleDataPreprocessor}, but much more
 * straightforward w.o streams usage. Performance comparedunder JMH
 * harness into benchmarks sourceset.
 */
public class NaiveDataPreprocessor implements DataPreprocessor {
    final LocationConfig config;
    final PointValidator validator;
    final PointsInterpolation interpolation;

    public NaiveDataPreprocessor(LocationConfig config,
                                  PointValidator validator,
                                  PointsInterpolation interpolation) {
        this.config = config;
        this.validator = validator;
        this.interpolation = interpolation;
    }

    @Override
    public List<ShortPoint> normalize(List<LongPoint> input) {

        Collections.sort(
                input, (p1, p2) -> Long.compare(p1.getTimestamp(), p2.getTimestamp()));

        Iterator<LongPoint> iterator = input.iterator();
        while (iterator.hasNext()) {

        }

        return input.parallelStream()
                .sorted((p1, p2) -> Long.compare(p1.getTimestamp(), p2.getTimestamp()))
                .filter(validator::isValid)
                .flatMap(point -> interpolation.runInterpolation(point).stream())
                .collect(Collectors.toList());
    }
}
