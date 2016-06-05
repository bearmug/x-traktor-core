package org.xtraktor

import groovy.transform.Canonical

@Canonical
class HashPoint {

    //hash with fully available precision
    BitSet geoHashFull

    long timestamp

    long userId
}
