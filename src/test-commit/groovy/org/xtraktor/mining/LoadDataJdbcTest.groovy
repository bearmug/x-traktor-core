package org.xtraktor.mining

import org.xtraktor.CrossTracker
import org.xtraktor.load.LoadDataJdbc
import org.xtraktor.location.LocationConfig
import org.xtraktor.storage.RedisDataStorage
import org.xtraktor.storage.SimpleDataStorage
import org.xtraktor.storage.StorageUtility
import redis.embedded.RedisServer
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class LoadDataJdbcTest extends Specification {

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

//        tracker = CrossTracker.create(config, new RedisDataStorage('localhost', port))
        tracker = CrossTracker.create(config, new SimpleDataStorage())
    }

    def cleanupSpec() {
        redisServer.stop()
    }

    @Ignore
    def "test"() {
        given:
        LoadDataJdbc loader = new LoadDataJdbc(
                connection: 'localhost:3306/mirami',
                username: 'usr',
                pw: 'password'
        )

        loader.load(tracker, 6)

        expect:
        1 == 1
    }

}
