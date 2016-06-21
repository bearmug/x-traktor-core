package org.xtraktor.storage

import groovy.util.logging.Slf4j
import redis.embedded.RedisServer
import spock.lang.Shared
import spock.lang.Specification

@Slf4j
class BigLocalDataStorageTest extends Specification {

    @Shared
    StorageUtility utility = new StorageUtility()
    RedisServer redisServer

    def setup() {
        log.debug("Starting embedded redis server...")
        redisServer = new RedisServer(utility.freePort)
        redisServer.start()
        log.info("Embedded redis server started, port: ${redisServer.port}")
    }

    def "do nothing test"() {
        expect:
        1 == 1
    }

    def cleanup() {
        log.debug("Stopping embedded redis server...")
        redisServer.stop()
        log.info("Embedded redis server stopped, port: ${redisServer.port}")
    }
}
