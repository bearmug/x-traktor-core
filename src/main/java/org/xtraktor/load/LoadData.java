package org.xtraktor.load;

import org.xtraktor.DataPreprocessor;

/**
 * API used to pre-load data into the system from some external source
 */
public interface LoadData {

    /**
     * Main API method to call for data preprocessing and persistence.
     *
     * @param proc   {@link DataPreprocessor} implementation to preprocess loaded points into
     * @param precision geo hash precision to use
     */
    void load(DataPreprocessor proc, int precision);
}
