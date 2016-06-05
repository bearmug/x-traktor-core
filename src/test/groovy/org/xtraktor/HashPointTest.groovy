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
                new LatLng(lon, lat)))
        HashPoint p2 = new HashPoint(geoHashFull: Geohasher.hash(
                new LatLng(lon + lonDelta, lat + latDelta)))

        when:
        String hash1 = p1.getGeoHashFull()
        String hash2 = p2.getGeoHashFull()

        then:
        hash1.take(hashPrecision) == hash2.take(hashPrecision)

        where:
        lonDelta | latDelta | hashPrecision
        0.000001 | 0.0      | 10    // approx. 0.1 meter precision
        0.0      | 0.000001 | 10
        0.000001 | 0.000001 | 10

        0.00001  | 0.0      | 7     // approx. 1.1 meter precision
        0.0      | 0.00001  | 7
        0.00001  | 0.00001  | 7

        0.0001   | 0.0      | 7     // approx. 11 meters precision
        0.0      | 0.0001   | 7
        0.0001   | 0.0001   | 7

        0.001    | 0.0      | 7     // approx. 110 meters precision
        0.0      | 0.001    | 7
        0.001    | 0.001    | 7

        0.01     | 0.0      | 5     // approx 1.1 kilometers precision
        0.0      | 0.01     | 5
        0.01     | 0.01     | 5

        0.1      | 0.0      | 3     // approx 11 kilometers precision
        0.0      | 0.1      | 3
        0.1      | 0.1      | 3

        1.0      | 0.0      | 2
        0.0      | 1.0      | 2
        1.0      | 1.0      | 2
    }

    @Unroll
    def "current geohash precision limited with 6 digits, delta: #lonDelta / #latDelta"() {

        def lon = 54.254775
        def lat = 41.995358

        given:
        HashPoint p1 = new HashPoint(geoHashFull: Geohasher.hash(
                new LatLng(lon, lat)))
        HashPoint p2 = new HashPoint(geoHashFull: Geohasher.hash(
                new LatLng(lon + lonDelta, lat + latDelta)))

        when:
        String hash1 = p1.getGeoHashFull()
        String hash2 = p2.getGeoHashFull()

        then:
        hash1.take(hashPrecision) == hash2.take(hashPrecision)

        where:
        lonDelta   | latDelta   | hashPrecision
        0.0000001  | 0.0        | 14
        0.0        | 0.0000001  | 14
        0.0000001  | 0.0000001  | 14

        0.00000001 | 0.0        | 14
        0.0        | 0.00000001 | 14
        0.00000001 | 0.00000001 | 14
    }
}
