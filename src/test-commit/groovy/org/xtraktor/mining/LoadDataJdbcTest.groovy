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

import java.util.stream.Collectors

//TODO: has to be replaced with runtime database when done
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

        println "Expect redis: ${redisTracker.matchForUser(userId, HASH_PRECISION).collect(Collectors.toList()).join(', ')}"
        println "Expect simple: ${simpleTracker.matchForUser(userId, HASH_PRECISION).collect(Collectors.toList()).join(", ")}"

        where:
        userId | redisExpect | simpleExpect
        453    | 1912        | 1912
        201    | 2           | 2
        369    | 10          | 10
        99     | 9           | 9
        3      | 6           | 6
        452    | 1912        | 1912
    }

}
