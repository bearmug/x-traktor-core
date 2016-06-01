package org.xtraktor.preprocess;

import org.xtraktor.LongPoint;
import org.xtraktor.ShortPoint;

import java.util.List;

public interface PointsInterpolation {
    List<ShortPoint> runInterpolation(LongPoint point);
}
