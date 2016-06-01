package org.xtraktor

import spock.lang.Specification

class LongPointTest extends Specification {

    def "obsolete point is no longer valid"() {
        when:
        LongPoint point = new LongPoint()
    }
}
