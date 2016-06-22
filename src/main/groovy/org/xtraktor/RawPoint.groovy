package org.xtraktor

import com.google.common.math.DoubleMath
import com.javadocmd.simplelatlng.Geohasher
import com.javadocmd.simplelatlng.LatLng
import groovy.transform.Canonical
import groovy.transform.CompileStatic
import org.xtraktor.location.LocationConfig

import java.math.RoundingMode
import java.util.stream.LongStream
import java.util.stream.Stream

/**
 * Basic input for calculations. Contains real route data with
 * natural locations and timings.
 */
@Canonical
class RawPoint {

    //Location longitude
    double longitude

    //Location latitude
    double latitude

    //Milliseconds timestamp with fully available precision
    long timestamp

    //Identifier for relevant user
    long userId

    //next point into the route to build interpolations
    RawPoint nextPoint

    @CompileStatic
    boolean isValid(LocationConfig config) {
        timestamp >= config.timeMin &&
                nextPoint?.timestamp > timestamp &&
                DoubleMath.fuzzyEquals(longitude, nextPoint.longitude, config.tolerance) &&
                DoubleMath.fuzzyEquals(latitude, nextPoint.latitude, config.tolerance)

    }

    Stream<HashPoint> interpolate(LocationConfig config) {

        long minIndex = DoubleMath.roundToLong(
                (double) (timestamp - config.timeMin) / config.timeDelta,
                RoundingMode.UP)

        double delta = (double) (nextPoint.timestamp - config.timeMin) / config.timeDelta
        if (DoubleMath.roundToLong(delta, RoundingMode.UP) == minIndex &&
                (nextPoint.timestamp - config.timeMin) % config.timeDelta != 0) {
            return Stream.empty()
        }

        long maxIndex = Math.max(
                DoubleMath.roundToLong(delta, RoundingMode.DOWN),
                minIndex)

        if (minIndex < 0 || maxIndex < 0) {
            return Stream.empty()
        }

        return LongStream.rangeClosed(minIndex, maxIndex)
                .parallel()
                .mapToObj { long it ->
            long pointTime = config.getTimeMin() + config.getTimeDelta() * it;
            double pointRatio = (double) (pointTime - timestamp) / (nextPoint.timestamp - timestamp);

            double pointLon = new BigDecimal(
                    (double) (longitude + (nextPoint.longitude - longitude) * pointRatio))
                    .setScale(LocationConfig.PRECISION, BigDecimal.ROUND_HALF_EVEN)
                    .doubleValue();
            double pointLat = new BigDecimal(
                    (double) (latitude + (nextPoint.latitude - latitude) * pointRatio))
                    .setScale(LocationConfig.PRECISION, BigDecimal.ROUND_HALF_EVEN)
                    .doubleValue();

            new HashPoint(
                    Geohasher.hash(new LatLng(pointLat, pointLon)),
                    pointLon,
                    pointLat,
                    pointTime,
                    userId
            )
        }
    }
}
