package org.xtraktor;

import com.google.common.math.DoubleMath;
import com.javadocmd.simplelatlng.Geohasher;
import com.javadocmd.simplelatlng.LatLng;
import org.xtraktor.location.LocationConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class RawPointJava {
    private final double longitude;
    private final double latitude;
    private final long timestamp;
    private final long userId;

    private RawPointJava nextPoint;

    public RawPointJava(
            double longitude, double latitude, long timestamp,
            long userId, RawPointJava nextPoint) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
        this.userId = userId;
        this.nextPoint = nextPoint;
    }

    public boolean isValid(LocationConfig config) {
        return timestamp >= config.getTimeMin() &&
                nextPoint != null &&
                nextPoint.timestamp > timestamp &&
                DoubleMath.fuzzyEquals(longitude, nextPoint.longitude, config.getTolerance()) &&
                DoubleMath.fuzzyEquals(latitude, nextPoint.latitude, config.getTolerance());

    }

    public List<HashPoint> interpolate(LocationConfig config) {

        long minIndex = DoubleMath.roundToLong(
                (timestamp - config.getTimeMin()) / config.getTimeDelta(),
                RoundingMode.UP);

        long maxIndex = Math.max(
                DoubleMath.roundToLong(
                        (nextPoint.timestamp - config.getTimeMin()) / config.getTimeDelta(),
                        RoundingMode.DOWN),
                minIndex);


        return LongStream.rangeClosed(minIndex, maxIndex)
                .parallel()
                .mapToObj(it -> {
                    long pointTime = config.getTimeMin() + config.getTimeDelta() * it;
                    double pointRatio = (pointTime - timestamp) / (nextPoint.timestamp - timestamp);

                    double pointLon = new BigDecimal(
                            longitude + (nextPoint.longitude - longitude) * pointRatio)
                            .setScale(LocationConfig.getPRECISION(), BigDecimal.ROUND_HALF_EVEN)
                            .doubleValue();
                    double pointLat = new BigDecimal(
                            latitude + (nextPoint.latitude - latitude) * pointRatio)
                            .setScale(LocationConfig.getPRECISION(), BigDecimal.ROUND_HALF_EVEN)
                            .doubleValue();

                    return new HashPoint(
                            Geohasher.hash(new LatLng(pointLat, pointLon)),
                            pointLon,
                            pointLat,
                            pointTime,
                            userId);
                })
                .collect(Collectors.toList());
    }
}

