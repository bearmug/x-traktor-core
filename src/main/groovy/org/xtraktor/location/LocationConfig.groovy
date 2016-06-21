package org.xtraktor.location

import groovy.transform.Canonical

@Canonical
class LocationConfig {

    public static int PRECISION = 8
    double tolerance = 1.0
    int hashPrecision
    long timeMin
    long timeDelta
}
