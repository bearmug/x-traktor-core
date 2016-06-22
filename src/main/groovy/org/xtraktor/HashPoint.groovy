package org.xtraktor

import groovy.transform.Canonical

/**
 * Processing-ready data. Contains interpolated longitude/latitude
 * for time-boxed timestamps
 */
@Canonical
class HashPoint {

    //hash with fully available precision
    String geoHashFull

    //interpolated longitude
    double longitude

    //interpolated latitude
    double latitude

    //time-boxed timestamp
    long timestamp

    //referenced user id
    long userId

    public String getHash(int length) {
        if (geoHashFull == null) {
            throw new IllegalStateException("Please avoid null geohash")
        }
        geoHashFull.take length
    }
}
