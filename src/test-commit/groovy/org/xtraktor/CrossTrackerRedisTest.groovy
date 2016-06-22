package org.xtraktor

import org.xtraktor.location.LocationConfig
import org.xtraktor.storage.StorageUtility
import redis.embedded.RedisServer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class CrossTrackerRedisTest extends Specification {
    @Shared
    LocationConfig config = new LocationConfig(
            timeMin: 0,
            tolerance: 1.0,
            timeDelta: 1000,
            hashPrecision: 6)


    @Shared
    CrossTracker tracker

    @Shared
    RedisServer redisServer

    def setupSpec() {
        int port = new StorageUtility().freePort
        redisServer = new RedisServer(port)
        redisServer.start()

        tracker = CrossTracker.withRedis(config, 'localhost', port)
    }

    def cleanupSpec() {
        redisServer.stop()
    }

    @Unroll
    def "two points normalized and result queried: #mode"() {

        given: //1-second precision config with 1.0lon/lat tolerance
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
        tracker.preprocessor.normalize([point, nextPoint])
        Optional<HashPoint> res = tracker.miner.matchForPoint(
                new HashPoint(
                        geoHashFull: "v05cdhehktqw",
                        timestamp: 1000,
                        userId: point.userId + 1
                ), config.hashPrecision).findAny()

        then:
        res.isPresent()
        res.get().getHash(config.hashPrecision) == hash.take(config.hashPrecision)

        where:
        mode                     | lon     | lat     | time | nextLon | nextLat | nextTime | hash
        'inside range'           | 50.3656 | 45.2891 | 500  | 50.3658 | 45.2893 | 2500     | 'v05cdhe'
        'point from left limit'  | 50.3647 | 45.2892 | 1000 | 50.3652 | 45.2893 | 2500     | 'v05cdhd'
        'point from right limit' | 50.3656 | 45.2891 | 500  | 50.3654 | 45.2889 | 2000     | 'v05cdh'
        'point from both limits' | 50.3653 | 45.2892 | 1000 | 50.3655 | 45.2894 | 2000     | 'v05cdh'
    }
}
