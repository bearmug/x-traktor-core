package org.xtraktor.storage

import org.xtraktor.storage.redis.StorageUtility
import spock.lang.Specification
import spock.lang.Unroll

class StorageUtilityTest extends Specification {

    @Unroll
    def "invalid port #invalidPort caused exception"() {
        given:
        StorageUtility u = new StorageUtility(invalidPort)

        when:
        u.getFreePort()

        then:
        thrown IllegalStateException

        where:
        invalidPort << [1, 10]
    }
}
