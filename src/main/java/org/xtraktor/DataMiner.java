package org.xtraktor;

import java.util.List;

/**
 * API to lookup users routes correlations and intersections.
 */
public interface DataMiner {
    List<HashPoint> matchForPoint(HashPoint input);
    List<HashPoint> matchForRoute(List<HashPoint> input);
}
