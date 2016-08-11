package org.xtraktor.mining

import groovy.sql.Sql
import org.xtraktor.CrossTracker
import org.xtraktor.load.LoadDataJdbc
import org.xtraktor.location.LocationConfig
import org.xtraktor.storage.SimpleDataStorage
import org.xtraktor.storage.redis.RedisDataStorage
import org.xtraktor.storage.redis.StorageUtility
import redis.embedded.RedisServer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.nio.file.Paths

class LoadDataJdbcTest extends Specification {

    public static final String H2_TEST_CONNECTION_STRING = 'jdbc:h2:mem:test'
    public static final int HASH_PRECISION = 8
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
    Sql sql

    def setupSpec() {
        loadRedis()
        loadDatabase()

        LoadDataJdbc loader = new LoadDataJdbc(
                connectionString: H2_TEST_CONNECTION_STRING)

        loader.load(redisTracker, HASH_PRECISION)
        loader.load(simpleTracker, HASH_PRECISION)
    }

    private void loadDatabase() {
        sql = Sql.newInstance(H2_TEST_CONNECTION_STRING, 'org.h2.Driver')

        Paths.get('src/test-commit/resources/sql/gps_tracks.sql').text.split(';').each { s ->
            sql.execute s.replaceAll("\n", ' ')
        }
    }

    private void loadRedis() {
        int port = new StorageUtility(0).freePort
        redisServer = new RedisServer(port)
        redisServer.start()

        simpleTracker = CrossTracker.create(config, new SimpleDataStorage())
        redisTracker = CrossTracker.create(config, new RedisDataStorage('localhost', port))
    }

    def cleanupSpec() {
        redisServer.stop()
    }

    @Unroll
    def "for user #userId intersections number is #expectCount"() {
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
