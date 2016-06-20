package org.xtraktor

import groovy.transform.Canonical

@Canonical
class HashPoint {

    //hash with fully available precision
    String geoHashFull

    double longitude
    double latitude
    long timestamp
    long userId

    public String getHash(int length) {
        if (geoHashFull == null) {
            throw new IllegalStateException("Please avoid null geohash")
        }
        geoHashFull.take length
    }
}
