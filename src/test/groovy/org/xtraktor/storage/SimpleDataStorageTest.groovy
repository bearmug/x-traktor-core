package org.xtraktor.storage

import spock.lang.Specification

import static junit.framework.Assert.fail

class SimpleDataStorageTest extends Specification {

    def "point saved with full timestamp and limited precision"() {
        expect:
        fail()
    }

    def "points couple saved to the same buckets due to limited geohash precision"() {
        expect:
        fail()
    }

    def "same point stored once"() {
        expect:
        fail()
    }

    def "no points found for empty storage"() {
        expect:
        fail()
    }

    def "no points found for invalid input"() {
        expect:
        fail()
    }

    def "single point found for given input with reduced geohash precision"() {
        expect:
        fail()
    }

    def "10 points found for given input"() {
        expect:
        fail()
    }

    def "search result does not contains point itself"() {
        expect:
        fail()
    }

    def "search result does not contains points for the same user"() {
        expect:
        fail()
    }
}
