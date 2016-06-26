package org.xtraktor.mining

import org.xtraktor.DataStorage
import org.xtraktor.HashPoint
import org.xtraktor.location.LocationConfig
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

class SimpleDataMinerTest extends Specification {

    def "points list matching calculated"() {
        given:
        def storage = Mock(DataStorage)
        storage.findByHashAndTime(_,_) >> ([Mock(HashPoint)] * 3).stream()
        SimpleDataMiner miner = new SimpleDataMiner(storage)

        when:
        Stream<HashPoint> res = miner.matchForRoute([Mock(HashPoint)], DataStorage.MAX_HASH_PRECISION)

        then:
        res.collect().size() == 3
    }

    def "single point matching calculated"() {
        given:
        def storage = Mock(DataStorage)
        storage.findByHashAndTime(_,_) >> ([Mock(HashPoint)] * 3).stream()
        SimpleDataMiner miner = new SimpleDataMiner(storage)

        when:
        Stream<HashPoint> res = miner.matchForPoint(Mock(HashPoint), DataStorage.MAX_HASH_PRECISION)

        then:
        res.collect().size() == 3
    }

    def "matching done by user id"() {
        given:
        def storage = Mock(DataStorage)
        storage.routeForUser(_) >> ([Mock(HashPoint)] * 1).stream()
        storage.findByHashAndTime(_,_) >> ([Mock(HashPoint)] * 1).asList().stream()
        SimpleDataMiner miner = new SimpleDataMiner(storage)

        when:
        List<HashPoint> res = miner.matchForUser(0, 6).collect(Collectors.toList())

        then:
        res.size() == 1
    }

    def "simple data storage access provided"() {
        given:
        def storage = Mock(DataStorage)
        SimpleDataMiner miner = new SimpleDataMiner(storage)

        when:
        DataStorage res = miner.storage

        then:
        res.is storage
    }
}
