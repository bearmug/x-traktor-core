package org.xtraktor

import com.google.common.math.DoubleMath
import groovy.transform.Canonical
import org.xtraktor.location.LocationConfig

import java.util.stream.Collectors

@Canonical
class RawPoint {
    double longitute
    double latitude
    long timestamp
    long userId

    RawPoint nextPoint

    /**
     * Validates if point is:
     * <p>
     * <ul>
     *     <li>not beyond time horzon</li>
     *     <li>there is {@link #nextPoint} member variable</li>
     *     <li>{@link #nextPoint} is closer than predefined tolerance</li>
     * </ul>
     * @param config common configuration to match against
     * @return true if point could be used for data production
     */
    boolean isValid(LocationConfig config) {
        timestamp >= config.timeMin &&
                nextPoint != null &&
                DoubleMath.fuzzyEquals(longitute, nextPoint.longitute, config.tolerance) &&
                DoubleMath.fuzzyEquals(latitude, nextPoint.latitude, config.tolerance)

    }

    List<HashPoint> interpolate(LocationConfig config) {
        Long minIndex = (timestamp - config.timeMin) / config.timeDelta
        Long maxIndex = (nextPoint.timestamp - config.timeMin) / config.timeDelta

        return [minIndex..maxIndex]
                .parallelStream()
                .map { index ->
            def tim = config.timeMin + index * config.timeDelta
            def pointRatio = (tim - timestamp) / (nextPoint.timestamp - timestamp)

            def lon = longitute + (nextPoint.longitute - longitute) * pointRatio
            def lat = latitude + (nextPoint.latitude - latitude) * pointRatio

            def lonIndex = (lon - config.longitudeMin) / config.longitudeDelta
            def latIndex = (lat - config.latitudeMin) / config.latitudeDelta

            new HashPoint(
                    longitude: lon,
                    latitude: lat,
                    longitudeIndex: lonIndex,
                    latitudeIndex: latIndex,
                    timestamp: tim,
                    userId: userId
            )
        }
        .collect(Collectors.toList())
    }


}
