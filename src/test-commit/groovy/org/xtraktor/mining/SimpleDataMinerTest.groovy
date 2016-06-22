package org.xtraktor.mining

import org.xtraktor.DataStorage
import org.xtraktor.HashPoint
import spock.lang.Specification

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
}
