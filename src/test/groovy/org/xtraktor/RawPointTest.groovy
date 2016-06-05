package org.xtraktor

import org.xtraktor.location.LocationConfig
import spock.lang.Specification

class RawPointTest extends Specification {

    def "validation failed for point below time horizon"() {
        given:
        LocationConfig config = new LocationConfig(timeMin: millis)

        when:
        RawPoint point = new RawPoint(timestamp: millis - 1)

        then:
        !point.isValid(config)

        where:
        millis = System.currentTimeMillis()
    }

    def "validation failed for point without nextPoint member"() {

        given:
        LocationConfig config = new LocationConfig(timeMin: millis)

        when:
        RawPoint point = new RawPoint(timestamp: millis + 1, nextPoint: null)

        then:
        !point.isValid(config)

        where:
        millis = System.currentTimeMillis()
    }

    def "validation failed nextPoint lon/lat out of tolerance limits"() {

        given:
        LocationConfig config = new LocationConfig(
                timeMin: millis,
                tolerance: tolerance)

        when:
        RawPoint point = new RawPoint(
                timestamp: millis + 1,
                longitute: lon,
                latitude: lat,
                nextPoint: new RawPoint(longitute: nextLon, latitude: nextLat))

        then:
        !point.isValid(config)

        where:
        lon  | lat    | nextLon | nextLat | millis                     | tolerance
        55.2 | 64.345 | 52.3    | 64.346  | System.currentTimeMillis() | 1.0
        55.2 | 64.345 | 55.243  | 65.346  | System.currentTimeMillis() | 1.0
    }

    def "validation passed for correct RawPoint"() {
        LocationConfig config = new LocationConfig(
                timeMin: millis,
                tolerance: tolerance)

        when:
        RawPoint point = new RawPoint(
                timestamp: millis + 1,
                longitute: lon,
                latitude: lat,
                nextPoint: new RawPoint(longitute: nextLon, latitude: nextLat))

        then:
        point.isValid(config)

        where:
        lon  | lat    | nextLon | nextLat | millis                     | tolerance
        55.2 | 64.345 | 52.3    | 64.346  | System.currentTimeMillis() | 3.0
        55.2 | 64.345 | 55.243  | 65.346  | System.currentTimeMillis() | 1.1
    }
}
