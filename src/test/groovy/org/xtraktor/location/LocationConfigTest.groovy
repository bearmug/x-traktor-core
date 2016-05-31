package org.xtraktor.location

import spock.lang.Specification

class LocationConfigTest extends Specification {

    def "there are no transformations inside LocationConfig"() {
        when:
        LocationConfig config = new LocationConfig(
                rowNumber: rowNum, colNumber: colNum,
                minLongitude: minLong, maxLongitude: maxLong,
                minLatitude: minLat, maxLatitude: maxLat)

        then:
        config.rowNumber == rowNum
        config.colNumber == colNum

        config.minLatitude == minLat
        config.maxLatitude == maxLat

        config.minLongitude == minLong
        config.maxLongitude == maxLong

        where:
        rowNum | colNum | minLong | maxLong | minLat | maxLat
        10     | 10     | 123     | 124     | 10     | 20
        1      | 1      | -123    | 14      | -10    | -3
    }
}
