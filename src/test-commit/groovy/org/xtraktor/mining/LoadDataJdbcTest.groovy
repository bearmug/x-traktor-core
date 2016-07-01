package org.xtraktor.mining

import org.xtraktor.CrossTracker
import org.xtraktor.load.LoadDataJdbc
import org.xtraktor.location.LocationConfig
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
            timeDelta: 1000)


    @Shared
    CrossTracker tracker

    @Shared
    RedisServer redisServer

    @Shared
    LoadDataJdbc loader

    private static final int HASH_PRECISION = 4

    def setupSpec() {
        int port = new StorageUtility().freePort
        redisServer = new RedisServer(port)
        redisServer.start()

//        tracker = CrossTracker.create(config, new RedisDataStorage('localhost', port))
        tracker = CrossTracker.create(config, new SimpleDataStorage())

        loader = new LoadDataJdbc(
                connection: 'localhost:3306/mirami',
                username: 'usr',
                pw: 'password'
        )

        loader.load(tracker, HASH_PRECISION)
    }

    def cleanupSpec() {
        redisServer.stop()
    }

    @Ignore
    def "test"() {
        when:
        int count = tracker.matchForUser(userId, HASH_PRECISION).count()
        then:
        count == 0

        where:
        userId | _
        386    | _
        849    | _
        1001   | _
        558    | _
        681    | _
        96     | _
        606    | _
        421    | _
        453    | _
        1094   | _
        1096   | _
        201    | _
        320    | _
        433    | _
        493    | _
        402    | _
        369    | _
        628    | _
        658    | _
        574    | _
        626    | _
        1066   | _
        87     | _
        544    | _
        461    | _
        99     | _
        3      | _
        452    | _
        680    | _
        5      | _
    }

}
