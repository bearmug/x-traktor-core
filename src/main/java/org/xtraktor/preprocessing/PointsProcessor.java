package org.xtraktor.preprocessing;

import org.xtraktor.RawPoint;

import java.util.List;

interface PointsProcessor {
    List<RawPoint> pair(List<RawPoint> input);
    List<RawPoint> sort(List<RawPoint> input);
}
