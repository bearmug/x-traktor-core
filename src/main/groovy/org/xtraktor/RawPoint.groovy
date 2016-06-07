package org.xtraktor

import com.google.common.math.DoubleMath
import com.javadocmd.simplelatlng.Geohasher
import com.javadocmd.simplelatlng.LatLng
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import org.xtraktor.location.LocationConfig

import java.math.RoundingMode
import java.util.stream.Collectors
import java.util.stream.LongStream
import java.util.stream.Stream

@Canonical
@CompileStatic
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
                nextPoint.timestamp > timestamp &&
                DoubleMath.fuzzyEquals(longitude, nextPoint.longitude, config.tolerance) &&
                DoubleMath.fuzzyEquals(latitude, nextPoint.latitude, config.tolerance)

    }

    Stream<HashPoint> interpolate(LocationConfig config) {

        long minIndex = DoubleMath.roundToLong(
                (timestamp - config.timeMin) / config.timeDelta,
                RoundingMode.UP)

        long maxIndex = Math.max(
                DoubleMath.roundToLong(
                        (nextPoint.timestamp - config.timeMin) / config.timeDelta,
                        RoundingMode.DOWN),
                minIndex)

        return LongStream.rangeClosed(minIndex, maxIndex)
                .parallel()
                .mapToObj(
                {
                    long pointTime = config.timeMin + config.timeDelta * it
                    def pointRatio = (pointTime - timestamp) / (nextPoint.timestamp - timestamp)

                    double pointLon = ((longitude + (nextPoint.longitude - longitude) * pointRatio) as Double)
                            .round(LocationConfig.PRECISION)
                    double pointLat = ((latitude + (nextPoint.latitude - latitude) * pointRatio) as Double)
                            .round(LocationConfig.PRECISION)

                    new HashPoint(
                            Geohasher.hash(new LatLng(pointLat, pointLon)),
                            pointLon,
                            pointLat,
                            pointTime,
                            userId
                    )
                })
    }
}
