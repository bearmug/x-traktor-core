package org.xtraktor.preprocessing

import org.xtraktor.HashPoint
import org.xtraktor.RawPoint
import org.xtraktor.location.LocationConfig
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors
import java.util.stream.Stream

class SimpleDataPreprocessorTest extends Specification {

    def "pair() call should populate nextPoint field"() {
        given:
        List<RawPoint> list = [
                new RawPoint(timestamp: 0),
                new RawPoint(timestamp: 1),
                new RawPoint(timestamp: 2),
                new RawPoint(timestamp: 3)
        ]

        when:
        List<RawPoint> res = new SimpleDataPreprocessor(
                new LocationConfig(timeMin: 0)).pair(list)

        then:
        res == list
        res[-1].nextPoint == null
        res.dropRight(1).each {
            assert it.nextPoint != null
        }
    }

    def "sort() call sorting items according to contained timestamp"() {
        given:
        List<RawPoint> list = [
                new RawPoint(timestamp: 10),
                new RawPoint(timestamp: 1000000),
                new RawPoint(timestamp: 20),
                new RawPoint(timestamp: 3),
                new RawPoint(timestamp: 40000)
        ]

        when:
        List<RawPoint> res = new SimpleDataPreprocessor(
                new LocationConfig(timeMin: 0)).sort(list)

        then:
        res.size() == list.size()
        long latestTimestamp = 0
        res.each {
            assert it.timestamp > latestTimestamp
            latestTimestamp = it.timestamp
        }
    }

    def "normalization ignored for invalid point"() {
        given:
        def point = Mock(RawPoint)
        point.isValid(_) >> false

        when:
        Stream<HashPoint> out = new SimpleDataPreprocessor(
                new LocationConfig(timeMin: 0)).normalize([point] * 10, 8)

        then:
        out.collect().empty
    }

    @Unroll
    def "two points normalized: #mode"() {

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
        List<HashPoint> res = new SimpleDataPreprocessor(config)
                .normalize([point, nextPoint], 8)
                .collect(Collectors.toList())

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
}
