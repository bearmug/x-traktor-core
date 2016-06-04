package org.xtraktor

import groovy.transform.Canonical
import org.xtraktor.location.LocationConfig

import java.util.stream.Collectors

@Canonical
class LongPoint {
    long longitute
    long latitude
    long timestamp
    long userId

    LongPoint nextPoint

    boolean isValid(LocationConfig config) {
        timestamp >= config.timeMin &&
                longitute >= config.longitudeMin &&
                latitude >= config.latitudeMin &&
                longitute <= config.longitudeMin + config.longitudeDelta * config.longitudeSteps &&
                latitude <= config.latitudeMin + config.latitudeDelta * config.latitudeSteps
    }

    List<ShortPoint> interpolate(LocationConfig config) {
        //TODO: round up
        Long minIndex = (timestamp - config.timeMin) / config.timeDelta

        //TODO: round down
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

            new ShortPoint(
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
