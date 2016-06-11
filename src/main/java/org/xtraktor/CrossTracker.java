package org.xtraktor;

import org.xtraktor.location.LocationConfig;
import org.xtraktor.mining.SimpleDataMiner;
import org.xtraktor.preprocessing.SimpleDataPreprocessor;

/**
 * Exm
 */
public class CrossTracker {

    private final DataPreprocessor preprocessor;
    private final DataMiner miner;

    private CrossTracker(LocationConfig config, DataStorage dataStorage) {
        this.preprocessor = new SimpleDataPreprocessor(config, dataStorage);
        this.miner = new SimpleDataMiner(dataStorage);
    }

    public static CrossTracker create(
            LocationConfig config, DataStorage dataStorage) {
        return new CrossTracker(config, dataStorage);
    }

    public DataPreprocessor getPreprocessor() {
        return preprocessor;
    }

    public DataMiner getMiner() {
        return miner;
    }
}
