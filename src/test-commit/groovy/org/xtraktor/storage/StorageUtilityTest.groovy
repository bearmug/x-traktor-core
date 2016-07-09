package org.xtraktor.storage

import groovy.json.JsonSlurper
import org.xtraktor.HashPoint
import org.xtraktor.storage.redis.StorageUtility
import spock.lang.Specification

class StorageUtilityTest extends Specification {

    def "key generated noticing precision"() {
        given:
        StorageUtility utility = new StorageUtility()

        when:
        String key = utility.getLocationKey(point, precision)

        then:
        key == expectedKey

        where:
        point                                        | precision | expectedKey
        new HashPoint("1234567890", 0, 0, 1000, 777) | 8         | "12345678-1000"
        new HashPoint("1234567890", 0, 0, 1000, 777) | 7         | "1234567-1000"
        new HashPoint("1234567890", 0, 0, 1000, 777) | 6         | "123456-1000"
        new HashPoint("1234567890", 0, 0, 1000, 777) | 5         | "12345-1000"
    }

    def "serialization producing json"() {
        given:
        StorageUtility utility = new StorageUtility()

        when:
        def json = new JsonSlurper().parseText(utility.serialize(point))

        then:
        json.geoHashFull == point.geoHashFull
        json.longitude == point.longitude
        json.latitude == point.latitude
        json.timestamp == point.timestamp
        json.userId == point.userId

        where:
        point = new HashPoint("1234567890", 1, 2, 1000, 777)
    }

    def "deserialization producing same content HashPoint"() {
        given:
        StorageUtility utility = new StorageUtility()

        when:
        HashPoint res = utility.deserialize(utility.serialize(point))

        then:
        res == point

        where:
        point                                                     | _
        new HashPoint("1234567890", 1, 2, 1000, 777)              | _
        new HashPoint("", 1, 2, 1000, 777)                        | _
        new HashPoint("1234567890", Long.MIN_VALUE, 2, 1000, 777) | _
        new HashPoint("1234567890", 1, 0, 1000, 777)              | _
        new HashPoint("1234567890", 1, 2, Long.MAX_VALUE, 777)    | _
        new HashPoint("1234567890", 1, 2, 1000, -1)               | _
    }
}
