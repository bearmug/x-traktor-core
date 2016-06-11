package org.xtraktor.preprocessing;

import com.google.common.math.DoubleMath;
import com.javadocmd.simplelatlng.Geohasher;
import com.javadocmd.simplelatlng.LatLng;
import org.xtraktor.DataPreprocessor;
import org.xtraktor.HashPoint;
import org.xtraktor.RawPoint;
import org.xtraktor.location.LocationConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class SimpleDataPreprocessor implements DataPreprocessor, PointsProcessor {

    private final LocationConfig config;

    public SimpleDataPreprocessor(LocationConfig config) {
        this.config = config;
    }

    @Override
    public Stream<HashPoint> normalize(List<RawPoint> input) {

        return pair(sort(input))
                .parallelStream()
                .filter(point -> point.isValid(config))
                .flatMap(point -> point.interpolate(config));
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

    @Override
    public Stream<HashPoint> interpolate(RawPoint p1, RawPoint  p2) {

        long minIndex = DoubleMath.roundToLong(
                (p1.getTimestamp() - config.getTimeMin()) / config.getTimeDelta(),
                RoundingMode.UP);

        double delta = (p2.getTimestamp() - config.getTimeMin()) / config.getTimeDelta();
        if (DoubleMath.roundToLong(delta, RoundingMode.UP) == minIndex &&
                (p2.getTimestamp() - config.getTimeMin()) % config.getTimeDelta() != 0) {
            return Stream.empty();
        }

        long maxIndex = Math.max(
                DoubleMath.roundToLong(delta, RoundingMode.DOWN),
                minIndex);

        if (minIndex < 0 || maxIndex < 0) {
            return Stream.empty();
        }

        return LongStream.rangeClosed(minIndex, maxIndex)
                .parallel()
                .mapToObj(it -> {
                    long pointTime = config.getTimeMin() + config.getTimeDelta() * it;
                    double pointRatio = (pointTime - p1.getTimestamp()) / (p2.getTimestamp() - p1.getTimestamp());

                    double pointLon = new BigDecimal(
                            p1.getLongitude() + (p2.getLongitude() - p1.getLongitude()) * pointRatio)
                            .setScale(LocationConfig.getPRECISION(), BigDecimal.ROUND_HALF_EVEN)
                            .doubleValue();
                    double pointLat = new BigDecimal(
                            p1.getLatitude() + (p2.getLatitude() - p1.getLatitude()) * pointRatio)
                            .setScale(LocationConfig.getPRECISION(), BigDecimal.ROUND_HALF_EVEN)
                            .doubleValue();

                    return new HashPoint();
//                            Geohasher.hash(new LatLng(pointLat, pointLon)),
//                            pointLon,
//                            pointLat,
//                            pointTime,
//                            p1.getUserId());
                });
    }
}
