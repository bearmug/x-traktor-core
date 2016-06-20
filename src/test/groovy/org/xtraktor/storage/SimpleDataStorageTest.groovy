package org.xtraktor.storage

import org.xtraktor.DataStorage
import org.xtraktor.HashPoint
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

import static junit.framework.TestCase.fail

class SimpleDataStorageTest extends Specification {

    static final long USER_ID = 777
    static final long TIME = Long.MAX_VALUE

    @Unroll
    def "point saved with full timestamp and precision == #precision"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.save([point].stream())
        HashPoint stored = storage.findByHashAndTime(input, precision).findAny().get()

        then:
        stored?.userId == USER_ID
        stored?.timestamp == input.timestamp
        stored?.geoHashFull == point.geoHashFull

        where:
        point                                            | input                                            | precision
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID) | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('123456789X', 0, 0, TIME, USER_ID) | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID) | 8
    }

    @Unroll
    def "point lookup failed for changed precision == #precision"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.save([point].stream())
        Optional<HashPoint> stored = storage.findByHashAndTime(input, precision).findAny()

        then:
        !stored.present

        where:
        point                                            | input                                            | precision
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID) | 7
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID) | 6
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID) | 5
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID) | 4
    }

    @Unroll
    def "point lookup for changing precision == #precision"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.precision = precision
        storage.save([point].stream())
        HashPoint stored = storage.findByHashAndTime(input, precision).findAny().get()

        then:
        stored?.userId == USER_ID
        stored?.timestamp == input.timestamp
        stored?.geoHashFull == point.geoHashFull

        where:
        point                                            | input                                            | precision
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID) | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('123456789X', 0, 0, TIME, USER_ID) | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID) | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID) | 7
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID) | 6
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID) | 5
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID) | 4
    }

    def "points couple saved to the same buckets due to limited geohash precision"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.save([
                new HashPoint(geoHashFull: hash1, timestamp: TIME, userId: USER_ID),
                new HashPoint(geoHashFull: hash2, timestamp: TIME, userId: USER_ID)]
                .stream())
        List<HashPoint> stored = storage.findByHashAndTime(input, precision).collect(Collectors.toList())

        then:
        stored.size() == 2
        stored.each {
            it.timestamp == TIME
            it.userId == USER_ID
            it.getHash(precision) == input.getHash(precision)
        }

        where:
        hash1        | hash2        | input                                            | precision
        '123456oooo' | '1234456XXX' | new HashPoint('1234567890', 0, 0, TIME, USER_ID) | 6
    }

    def "same point stored once"() {
        expect:
        fail()
    }

    def "no points found for empty storage"() {
        expect:
        fail()
    }

    def "no points found for invalid input"() {
        expect:
        fail()
    }

    def "single point found for given input with reduced geohash precision"() {
        expect:
        fail()
    }

    def "10 points found for given input"() {
        expect:
        fail()
    }

    def "search result does not contains point itself"() {
        expect:
        fail()
    }

    def "search result does not contains points for the same user"() {
        expect:
        fail()
    }
}
