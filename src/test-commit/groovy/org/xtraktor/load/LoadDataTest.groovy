package org.xtraktor.load

import org.xtraktor.DataStorage
import org.xtraktor.HashPoint
import spock.lang.Specification

import java.util.stream.Stream

class LoadDataTest extends Specification {

    def "points stream to be stored by passed storage"() {
        given:
        DataStorage storage = Mock(DataStorage)
        Stream<HashPoint> stream = [point].stream()
        LoadData loader = new LoadData() {
            @Override
            Stream<HashPoint> openDataStream() {
                return stream
            }
        }

        when:
        loader.load(storage, precision)

        then:
        1 * storage.save(stream, precision)

        where:
        point                                                   | precision
        new HashPoint(geoHashFull: "12345678", timestamp: 1000) | 6

    }
}
