package org.xtraktor.preprocessing

import org.xtraktor.RawPoint
import org.xtraktor.location.LocationConfig
import spock.lang.Specification

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
}
