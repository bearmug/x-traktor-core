package org.xtraktor

import org.xtraktor.location.LocationConfig
import spock.lang.Specification
import spock.lang.Unroll

class RawPointTest extends Specification {

    @Unroll
    def "interpolation for single point executed: #mode"() {

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
            assert it.longitude == targetLon as Double
            assert it.latitude == targetLat as Double
            assert it.timestamp == targetTime
            assert it.geoHashFull == hash
            assert it.userId == userId
        }

        where:
        mode                     | lon     | lat     | time | nextLon | nextLat | nextTime | targetLon | targetLat | targetTime | hash           | userId
        'inside range'           | 50.3656 | 45.2891 | 500  | 50.3658 | 45.2893 | 1500     | 50.3657   | 45.2892   | 1000       | 'v05cdhehtygc' | 777
        'point from left limit'  | 50.3657 | 45.2892 | 1000 | 50.3658 | 45.2893 | 1500     | 50.3657   | 45.2892   | 1000       | 'v05cdhehtygc' | 777
        'point from right limit' | 50.3656 | 45.2891 | 500  | 50.3657 | 45.2892 | 1000     | 50.3657   | 45.2892   | 1000       | 'v05cdhehtygc' | 777
    }

    @Unroll
    def "interpolation for two points executed: #mode"() {

        given: //1-second precision config with 1.0lon/lat tolerance
        LocationConfig config = new LocationConfig(
                timeMin: 0,
                tolerance: 1.0,
                timeDelta: 1000)
        RawPoint nextPoint = new RawPoint(
                longitude: nextLon,
                latitude: nextLat,
                timestamp: nextTime)
        RawPoint point = new RawPoint(
                longitude: lon,
                latitude: lat,
                timestamp: time,
                nextPoint: nextPoint)

        when:
        List<HashPoint> res = point.interpolate config

        then:
        res.size() == 2
        res.each {
            assert it.geoHashFull.indexOf(hash) == 0
        }

        where:
        mode                     | lon     | lat     | time | nextLon | nextLat | nextTime | hash
        'inside range'           | 50.3656 | 45.2891 | 500  | 50.3658 | 45.2893 | 2500     | 'v05cdhe'
        'point from left limit'  | 50.3647 | 45.2892 | 1000 | 50.3652 | 45.2893 | 2500     | 'v05cdhd'
        'point from right limit' | 50.3656 | 45.2891 | 500  | 50.3654 | 45.2889 | 2000     | 'v05cdh'
        'point from both limits' | 50.3653 | 45.2892 | 1000 | 50.3655 | 45.2894 | 2000     | 'v05cdh'
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

    def "validation failed nextPoint with invalid timestamp"() {

        given:
        LocationConfig config = new LocationConfig(
                timeMin: 0,
                tolerance: tolerance)

        when:
        RawPoint point = new RawPoint(
                timestamp: pointTime,
                longitude: lon,
                latitude: lat,
                nextPoint: new RawPoint(
                        longitude: nextLon,
                        latitude: nextLat,
                        timestamp: nextPointTime))

        then:
        !point.isValid(config)

        where:
        lon  | lat    | pointTime | nextLon | nextLat | nextPointTime | tolerance
        55.2 | 64.345 | 1001      | 55.3    | 64.346  | 1001          | 1.0
        55.2 | 64.345 | 1001      | 55.243  | 64.346  | 999           | 1.0
    }

    def "validation failed nextPoint lon/lat out of tolerance limits"() {

        given:
        LocationConfig config = new LocationConfig(
                timeMin: 0,
                tolerance: tolerance)

        when:
        RawPoint point = new RawPoint(
                timestamp: pointTime,
                longitude: lon,
                latitude: lat,
                nextPoint: new RawPoint(
                        longitude: nextLon,
                        latitude: nextLat,
                        timestamp: pointTime + 1))

        then:
        !point.isValid(config)

        where:
        lon  | lat    | pointTime | nextLon | nextLat | tolerance
        55.2 | 64.345 | 1000      | 52.3    | 64.346  | 1.0
        55.2 | 64.345 | 1000      | 55.243  | 65.346  | 1.0
    }

    def "validation passed for correct RawPoint"() {
        LocationConfig config = new LocationConfig(
                timeMin: 0,
                tolerance: tolerance)

        when:
        RawPoint point = new RawPoint(
                timestamp: pointTime,
                longitude: lon,
                latitude: lat,
                nextPoint: new RawPoint(
                        longitude: nextLon,
                        latitude: nextLat,
                        timestamp: pointTime + 1))

        then:
        point.isValid(config)

        where:
        lon  | lat    | pointTime | nextLon | nextLat | tolerance
        55.2 | 64.345 | 1000      | 52.3    | 64.346  | 3.0
        55.2 | 64.345 | 1000      | 55.243  | 65.346  | 1.1
    }
}
