package org.xtraktor.mining

import groovy.sql.Sql
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

//TODO: has to be replaced with runtime database when done
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

    @Shared
    sql = Sql.newInstance("jdbc:h2:mem:", "org.h2.Driver")

    private static final int HASH_PRECISION = 8

    def setupSpec() {
        int port = new StorageUtility().freePort
        redisServer = new RedisServer(port)
        redisServer.start()

        simpleTracker = CrossTracker.create(config, new SimpleDataStorage())
        redisTracker = CrossTracker.create(config, new RedisDataStorage('localhost', port))

        LoadDataJdbc loader = new LoadDataJdbc(
                connectionString: 'jdbc:mysql://localhost:3306/mirami?serverTimezone=UTC&user=usr&password=password')

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
        redisCount == expectCount
        simpleCount == expectCount

        where:
        userId | expectCount
        453    | 1912
        369    | 2
        99     | 3
        3      | 1
        452    | 1912
    }
}
