package org.xtraktor.preprocessing;

import org.xtraktor.RawPoint;

import java.util.List;

/**
 * Internal API to decouple points processing.
 */
interface PointsProcessor {
    List<RawPoint> pair(List<RawPoint> input);

    List<RawPoint> sort(List<RawPoint> input);
}
