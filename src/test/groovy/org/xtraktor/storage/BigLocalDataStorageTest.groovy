package org.xtraktor.storage

import groovy.util.logging.Slf4j
import org.xtraktor.DataStorage
import org.xtraktor.HashPoint
import redis.embedded.RedisServer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Slf4j
class BigLocalDataStorageTest extends Specification {

    @Shared
    StorageUtility utility = new StorageUtility()
    RedisServer redisServer
    BigLocalDataStorage storage

    def setup() {
        log.debug("Starting embedded redis server...")
        redisServer = new RedisServer(utility.freePort)
        redisServer.start()
        log.info("Embedded redis server started, port: ${redisServer.port}")

        storage = new BigLocalDataStorage("localhost", redisServer.port)
    }

    static final long USER_ID = 777
    static final long TIME = Long.MAX_VALUE

    @Unroll
    def "point saved with full timestamp and precision == #precision"() {
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

    def cleanup() {
        log.debug("Stopping embedded redis server...")
        redisServer.stop()
        log.info("Embedded redis server stopped, port: ${redisServer.port}")
    }
}
