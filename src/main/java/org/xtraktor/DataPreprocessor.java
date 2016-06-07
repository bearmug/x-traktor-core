package org.xtraktor;

import java.util.List;
import java.util.stream.Stream;

/**
 * API to normalize incoming GPS ungrained data. Providing
 * output with predefined discretion time/space discretion.
 */
public interface DataPreprocessor {
    Stream<HashPoint> normalize(List<RawPoint> input);
}
