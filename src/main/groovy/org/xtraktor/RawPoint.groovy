package org.xtraktor

import com.google.common.math.DoubleMath
import com.javadocmd.simplelatlng.Geohasher
import com.javadocmd.simplelatlng.LatLng
import groovy.transform.Canonical
import org.xtraktor.location.LocationConfig

import java.math.RoundingMode
import java.util.stream.Collectors

@Canonical
class RawPoint {
    double longitude
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
                DoubleMath.fuzzyEquals(longitude, nextPoint.longitude, config.tolerance) &&
                DoubleMath.fuzzyEquals(latitude, nextPoint.latitude, config.tolerance)

    }

    List<HashPoint> interpolate(LocationConfig config) {

        long minIndex = DoubleMath.roundToLong(
                (timestamp - config.timeMin) / config.timeDelta,
                RoundingMode.UP)

        long maxIndex = DoubleMath.roundToLong(
                (nextPoint.timestamp - config.timeMin) / config.timeDelta,
                RoundingMode.DOWN)

        return [minIndex..maxIndex]
                .parallelStream()
                .map { index ->
            long pointTime = config.timeMin + index * config.timeDelta
            double pointRatio = (pointTime - timestamp) / (nextPoint.timestamp - timestamp)

            def pointLon = longitude + (nextPoint.longitude - longitude) * pointRatio
            def pointLat = latitude + (nextPoint.latitude - latitude) * pointRatio

            new HashPoint(
                    longitude: pointLon,
                    latitude: pointLat,
                    timestamp: pointTime,
                    userId: userId,
                    geoHashFull: Geohasher.hash(new LatLng(pointLat, pointLon))
            )
        }
        .collect(Collectors.toList())
    }


}
