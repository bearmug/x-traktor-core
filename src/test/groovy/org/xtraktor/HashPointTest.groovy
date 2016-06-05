package org.xtraktor

import com.javadocmd.simplelatlng.Geohasher
import com.javadocmd.simplelatlng.LatLng
import spock.lang.Specification

class HashPointTest extends Specification {

    def "check close points have similar geohash"() {
        given:
        HashPoint p1 = new HashPoint(geoHashFull: Geohasher.hash(
                new LatLng(lon1 as Double, lat1 as Double)))
        HashPoint p2 = new HashPoint(geoHashFull: Geohasher.hash(
                new LatLng(lon2 as Double, lat2 as Double)))

        when:
        String hash1 = p1.getGeoHashFull()
        String hash2 = p2.getGeoHashFull()

        then:
        hash1.take(hashPrecision as int) == hash2.take(hashPrecision as int)

        where:
        lon1      | lat1      | lon2      | lat2      | hashPrecision
        54.345673 | 43.239947 | 54.345674 | 43.239947 | 10
        54.345673 | 43.239947 | 54.345673 | 43.239948 | 10
        54.345673 | 43.239947 | 54.345674 | 43.239948 | 10

        54.345673 | 43.239947 | 54.345674 | 43.239947 | 10
        54.345673 | 43.239947 | 54.345673 | 43.239948 | 10
        54.345673 | 43.239947 | 54.345674 | 43.239948 | 10

        54.345673 | 43.239947 | 54.345674 | 43.239947 | 10
        54.345673 | 43.239947 | 54.345673 | 43.239948 | 10
        54.345673 | 43.239947 | 54.345674 | 43.239948 | 10

        54.345673 | 43.239947 | 54.345674 | 43.239947 | 10
        54.345673 | 43.239947 | 54.345673 | 43.239948 | 10
        54.345673 | 43.239947 | 54.345674 | 43.239948 | 10

        54.345673 | 43.239947 | 54.345674 | 43.239947 | 10
        54.345673 | 43.239947 | 54.345673 | 43.239948 | 10
        54.345673 | 43.239947 | 54.345674 | 43.239948 | 10

        54.345673 | 43.239947 | 54.345674 | 43.239947 | 10
        54.345673 | 43.239947 | 54.345673 | 43.239948 | 10
        54.345673 | 43.239947 | 54.345674 | 43.239948 | 10
    }

    def "lon/lat precision limited with 6 digits"() {

        fail()
    }
}
