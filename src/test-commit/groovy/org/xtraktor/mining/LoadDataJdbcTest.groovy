package org.xtraktor.mining

import org.xtraktor.CrossTracker
import org.xtraktor.load.LoadDataJdbc
import org.xtraktor.location.LocationConfig
import org.xtraktor.storage.RedisDataStorage
import org.xtraktor.storage.SimpleDataStorage
import org.xtraktor.storage.StorageUtility
import redis.embedded.RedisServer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

//@Ignore
class LoadDataJdbcTest extends Specification {

    @Shared
    LocationConfig config = new LocationConfig(
            timeMin: 0,
            tolerance: 1.0,
            timeDelta: 1000)


    @Shared
    CrossTracker redisTracker

    @Shared
    CrossTracker simpleTracker

    @Shared
    RedisServer redisServer

    private static final int HASH_PRECISION = 8

    def setupSpec() {
        int port = new StorageUtility().freePort
        redisServer = new RedisServer(port)
        redisServer.start()

        simpleTracker = CrossTracker.create(config, new SimpleDataStorage())
        redisTracker = CrossTracker.create(config, new RedisDataStorage('localhost', port))

        LoadDataJdbc loader = new LoadDataJdbc(
                connection: 'localhost:3306/mirami',
                username: 'usr',
                pw: 'password'
        )

        loader.load(redisTracker, HASH_PRECISION)
        loader.load(simpleTracker, HASH_PRECISION)
    }

    def cleanupSpec() {
        redisServer.stop()
    }

    @Unroll
    def "for user #userId intersections number is #redisExpect/#simpleExpect"() {
        when:
        int redisCount = redisTracker.matchForUser(userId, HASH_PRECISION).count()
        int simpleCount = simpleTracker.matchForUser(userId, HASH_PRECISION).count()

        then:
        redisCount == redisExpect
        simpleCount == simpleExpect

        where:
        userId | redisExpect | simpleExpect
        386    | 0           | 0
        849    | 0           | 0
        1001   | 0           | 0
        558    | 0           | 0
        681    | 0           | 0
        96     | 0           | 0
        606    | 0           | 0
        421    | 0           | 0
        453    | 1916        | 1912
        1094   | 0           | 0
        1096   | 0           | 0
        201    | 2           | 2
        320    | 0           | 0
        433    | 0           | 0
        493    | 0           | 0
        402    | 0           | 0
        369    | 14          | 10
        628    | 0           | 0
        658    | 0           | 0
        574    | 0           | 0
        626    | 0           | 0
        1066   | 0           | 0
        87     | 0           | 0
        544    | 0           | 0
        461    | 0           | 0
        99     | 10          | 9
        3      | 8           | 6
        452    | 1916        | 1912
        680    | 0           | 0
        5      | 0           | 0
    }

}
