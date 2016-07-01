package org.xtraktor.location

import groovy.transform.Canonical

/**
 * Basic configuration to run calculations
 */
@Canonical
class LocationConfig {

    //precision for longitude/latitude usage and rounding
    public static int PRECISION = 8

    //maximum points to produce between two source points
    public static int MAX_INTERPOLATION_INTERVALS = 10

    //tolerance to filter false RawPoint locations
    double tolerance = 1.0

    //minimal time threshold to generate interpolated routes
    long timeMin

    //time-box delta for interpolation
    long timeDelta
}
