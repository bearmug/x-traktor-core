package org.xtraktor

import org.xtraktor.location.LocationConfig
import spock.lang.Specification

class RawPointTest extends Specification {

    def "interpolation for single point executed"() {

        given: //1-second precision config with 1.0lon/lat tolerance
        LocationConfig config = new LocationConfig(
                timeMin: 0,
                tolerance: 1.0,
                timeDelta: 1000)
        RawPoint nextPoint = new RawPoint(
                longitude: nextLon,
                latitude: nextLat,
                timestamp: nextTime,
                userId: userId)
        RawPoint point = new RawPoint(
                longitude: lon,
                latitude: lat,
                timestamp: time,
                nextPoint: nextPoint,
                userId: userId)

        when:
        List<HashPoint> res = point.interpolate config

        then:
        res.size() == 1
        res.each {
            it.longitude == targetLon
            it.latitude == targetLat
            it.timestamp == targetTime
            it.geoHashFull == hash
            it.userId == userId
        }

        where:
        lon     | lat     | time | nextLon | nextLat | nextTime | targetLon | targetLat | targetTime | hash   | userId
        50.3656 | 45.2891 | 500  | 50.3658 | 45.2893 | 500      | 50.3657   | 45.2892   | 1000       | 'asfr' | 777
    }

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
                longitude: lon,
                latitude: lat,
                nextPoint: new RawPoint(longitude: nextLon, latitude: nextLat))

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
                longitude: lon,
                latitude: lat,
                nextPoint: new RawPoint(longitude: nextLon, latitude: nextLat))

        then:
        point.isValid(config)

        where:
        lon  | lat    | nextLon | nextLat | millis                     | tolerance
        55.2 | 64.345 | 52.3    | 64.346  | System.currentTimeMillis() | 3.0
        55.2 | 64.345 | 55.243  | 65.346  | System.currentTimeMillis() | 1.1
    }
}
