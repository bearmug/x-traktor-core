package org.xtraktor.location

import groovy.transform.Immutable

@Immutable
class LocationConfig {

    long longitudeMin
    long longitudeDelta
    long longitudeSteps

    long latitudeMin
    long latitudeDelta
    long latitudeSteps

    long timeMin
    long timeDelta
}
