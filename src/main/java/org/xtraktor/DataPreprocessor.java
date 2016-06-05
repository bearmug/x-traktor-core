package org.xtraktor;

import java.util.List;

/**
 * API to normalize incoming GPS ungrained data. Providing
 * output with predefined discretion time/space discretion.
 */
public interface DataPreprocessor {
    List<HashPoint> normalize(List<RawPoint> input);
}
