package org.xtraktor

import groovy.transform.Canonical

@Canonical
class ShortPoint {
    long longitude
    long latitude

    long longitudeIndex
    long latitudeIndex

    long timestamp

    long userId
}
