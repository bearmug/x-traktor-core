package org.xtraktor;

import java.util.List;
import java.util.stream.Stream;

/**
 * API to lookup users routes correlations and intersections.
 */
public interface DataMiner {
    Stream<HashPoint> matchForPoint(HashPoint input, int hashPrecision);
    Stream<HashPoint> matchForRoute(List<HashPoint> input, int hashPrecision);
}
