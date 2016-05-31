package org.xtraktor;

import java.util.List;

/**
 * API to lookup users routes correlations and intersections.
 */
public interface DataMiner {
    List<ShortPoint> matchForPoint(ShortPoint input);
    List<ShortPoint> matchForRoute(List<ShortPoint> input);
}
