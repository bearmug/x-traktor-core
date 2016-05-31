package org.xtraktor.location

import groovy.transform.Immutable

@Immutable
class LocationConfig {
    int rowNumber
    int colNumber

    long minLongitude
    long maxLongitude

    long minLatitude
    long maxLatitude
}
