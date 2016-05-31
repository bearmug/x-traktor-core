package org.xtraktor.preprocess;

import org.xtraktor.DataPreprocessor;
import org.xtraktor.LongPoint;
import org.xtraktor.ShortPoint;
import org.xtraktor.location.LocationConfig;

import java.util.List;

public class SimpleDataPreprocessor implements DataPreprocessor {

    final LocationConfig config;

    public SimpleDataPreprocessor(LocationConfig config) {
        this.config = config;
    }

    @Override
    public List<ShortPoint> normalize(List<LongPoint> input) {
        return null;
    }
}
