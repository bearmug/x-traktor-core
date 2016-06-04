package org.xtraktor.preprocessing;

import org.xtraktor.LongPoint;

import java.util.List;

interface PointsProcessor {
    List<LongPoint> pair(List<LongPoint> input);
    List<LongPoint> sort(List<LongPoint> input);
}
