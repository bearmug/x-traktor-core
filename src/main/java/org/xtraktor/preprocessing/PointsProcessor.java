package org.xtraktor.preprocessing;

import org.xtraktor.HashPoint;
import org.xtraktor.RawPoint;
import org.xtraktor.location.LocationConfig;

import java.util.List;
import java.util.stream.Stream;

interface PointsProcessor {
    List<RawPoint> pair(List<RawPoint> input);
    List<RawPoint> sort(List<RawPoint> input);
    Stream<HashPoint> interpolate(RawPoint p1, RawPoint p2);
}
