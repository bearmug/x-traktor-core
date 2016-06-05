package org.xtraktor.location

import groovy.transform.Immutable

@Immutable
class LocationConfig {

    static int PRECISION = 6
    double tolerance = 1.0
    int hashPrecision
    long timeMin
    long timeDelta
}
