package org.xtraktor.location

import groovy.transform.Immutable

@Immutable
class LocationConfig {

    double tolerance = 1.0
    int hashPrecision
    long timeMin
    long timeDelta
}
