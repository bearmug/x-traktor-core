package org.xtraktor

import groovy.transform.Canonical
import org.xtraktor.location.LocationConfig

@Canonical
class LongPoint {
    long longitute
    long latitude
    long timestamp
    long userId

    LongPoint nextPoint

    boolean isValid(LocationConfig config) {
        timestamp >= config.minTimestamp &&
                longitute >= config.minLongitude && longitute <= config.maxLongitude &&
                latitude >= config.minLatitude && latitude <= config.maxLatitude
    }

    List<ShortPoint> toShort(LocationConfig config, LongPoint next) {

    }
}
