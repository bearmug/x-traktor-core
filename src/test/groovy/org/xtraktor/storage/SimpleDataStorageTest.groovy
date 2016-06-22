package org.xtraktor.storage

import org.xtraktor.DataStorage
import org.xtraktor.HashPoint
import spock.lang.Specification
import spock.lang.Unroll

import java.util.stream.Collectors

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
        point                                            | input                                                | precision
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID + 1) | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('123456789X', 0, 0, TIME, USER_ID + 1) | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID + 1) | 8
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
        point                                            | input                                                | precision
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID + 1) | 7
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID + 1) | 6
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID + 1) | 5
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID + 1) | 4
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
        point                                            | input                                                | precision
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID + 1) | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('123456789X', 0, 0, TIME, USER_ID + 1) | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID + 1) | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID + 1) | 7
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID + 1) | 6
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID + 1) | 5
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('12345678XX', 0, 0, TIME, USER_ID + 1) | 4
    }

    def "points couple saved to the same buckets due to limited geohash precision"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.precision = precision
        storage.save([
                new HashPoint(geoHashFull: hash1, timestamp: TIME, userId: USER_ID),
                new HashPoint(geoHashFull: hash2, timestamp: TIME, userId: USER_ID)]
                .stream())
        List<HashPoint> stored = storage.findByHashAndTime(input, precision).collect(Collectors.toList())

        then:
        stored.size() == 2
        stored.each {
            assert it.timestamp == TIME
            assert it.userId == USER_ID
            assert it.getHash(precision) == input.getHash(precision)
        }

        where:
        hash1           | hash2        | input                                                | precision
        '123456oooo'    | '123456XXX'  | new HashPoint('1234567890', 0, 0, TIME, USER_ID + 1) | 6
        '1234oooo'      | '1234'       | new HashPoint('1234567890', 0, 0, TIME, USER_ID + 1) | 4
        '12345678ooooo' | '12345678XX' | new HashPoint('1234567890', 0, 0, TIME, USER_ID + 1) | 8
    }

    def "same point stored once"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.save([point].stream())
        List<HashPoint> stored = storage.findByHashAndTime(input, 8).collect(Collectors.toList())

        then:
        stored.size() == 1

        when:
        stored = storage.findByHashAndTime(input, 8).collect(Collectors.toList())

        then:
        stored.size() == 1

        where:
        point = new HashPoint('1234567890', 0, 0, TIME, USER_ID)
        input = new HashPoint('1234567890', 0, 0, TIME, USER_ID + 1)
    }

    def "no points found for empty storage"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        List<HashPoint> stored = storage.findByHashAndTime(point, 8).collect(Collectors.toList())

        then:
        stored.isEmpty()

        where:
        point = new HashPoint('1234567890', 0, 0, TIME, USER_ID)
    }

    def "no points found for invalid input"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.save([point].stream())
        List<HashPoint> stored = storage.findByHashAndTime(input, precision).collect(Collectors.toList())

        then:
        stored.empty

        where:
        point                                            | input                                                      | precision
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('', 0, 0, TIME, USER_ID)                     | 8
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, Long.MIN_VALUE, USER_ID) | 8
    }

    def "point with null geohash can not be stored"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.save([point].stream())

        then:
        thrown IllegalStateException

        where:
        point                                    | precision
        new HashPoint(null, 0, 0, TIME, USER_ID) | 8
    }

    def "search result does not contains point itself"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.save([point, input].stream())
        List<HashPoint> stored = storage.findByHashAndTime(input, precision).collect(Collectors.toList())

        then:
        stored.size() == 1
        stored[0] == point

        where:
        point                                            | input                                                | precision
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID - 1) | 8
    }

    def "search result does not contains points for the same user"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.save([point, input].stream())
        List<HashPoint> stored = storage.findByHashAndTime(input, precision).collect(Collectors.toList())

        then:
        stored.isEmpty()

        where:
        point                                            | input                                            | precision
        new HashPoint('1234567891', 1, 1, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID) | 8
    }

    def "clear() removes everything from storage"() {
        given:
        DataStorage storage = new SimpleDataStorage();

        when:
        storage.save([point].stream())
        List<HashPoint> stored = storage.findByHashAndTime(input, precision).collect(Collectors.toList())

        then:
        stored.size() == 1
        stored[0] == point

        when:
        storage.clear()
        stored = storage.findByHashAndTime(input, precision).collect(Collectors.toList())

        then:
        stored.isEmpty()

        where:
        point                                            | input                                                | precision
        new HashPoint('1234567890', 0, 0, TIME, USER_ID) | new HashPoint('1234567890', 0, 0, TIME, USER_ID - 1) | 8
    }
}
