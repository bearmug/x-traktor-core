package org.xtraktor

import com.javadocmd.simplelatlng.Geohasher
import com.javadocmd.simplelatlng.LatLng
import spock.lang.Specification
import spock.lang.Unroll

class HashPointTest extends Specification {

    @Unroll
    def "check close points have similar geohash, delta: #lonDelta / #latDelta"() {

        def lon = 54.254775
        def lat = 41.995358

        given:
        HashPoint p1 = new HashPoint(geoHashFull: Geohasher.hash(
                new LatLng(lon as Double, lat as Double)))
        HashPoint p2 = new HashPoint(geoHashFull: Geohasher.hash(
                new LatLng(lon + lonDelta, lat + latDelta)))

        when:
        String hash1 = p1.getGeoHashFull()
        String hash2 = p2.getGeoHashFull()

        then:
        hash1.take(hashPrecision as int) == hash2.take(hashPrecision as int)

        where:
        lonDelta | latDelta | hashPrecision
        0.000001 | 0.0      | 10
        0.0      | 0.000001 | 10
        0.000001 | 0.000001 | 10

        0.00001  | 0.0      | 7
        0.0      | 0.00001  | 7
        0.00001  | 0.00001  | 7

        0.0001   | 0.0      | 7
        0.0      | 0.0001   | 7
        0.0001   | 0.0001   | 7

        0.001    | 0.0      | 7
        0.0      | 0.001    | 7
        0.001    | 0.001    | 7

        0.01     | 0.0      | 5
        0.0      | 0.01     | 5
        0.01     | 0.01     | 5

        0.1      | 0.0      | 3
        0.0      | 0.1      | 3
        0.1      | 0.1      | 3

        1.0      | 0.0      | 2
        0.0      | 1.0      | 2
        1.0      | 1.0      | 2
    }

    def "lon/lat precision limited with 6 digits"() {

        fail()
    }
}
