package org.xtraktor

import org.xtraktor.location.LocationConfig
import spock.lang.Specification

class LongPointTest extends Specification {

    def "validation against minimal time threshold executed"() {

        given:
        LocationConfig config=new LocationConfig(timeMin: 1000)

        when:
        LongPoint point = new LongPoint(timestamp: 10)

        then:
        !point.isValid(config)
    }

//    def "validation against latitude/longitude limits executed"() {
//        given:
//        LocationConfig config = new LocationConfig(
//                timeMin: 1000,
//                lon
//        )
//    }
}
