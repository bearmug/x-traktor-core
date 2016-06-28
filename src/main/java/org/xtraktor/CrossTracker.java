package org.xtraktor;

import org.xtraktor.location.LocationConfig;
import org.xtraktor.mining.SimpleDataMiner;
import org.xtraktor.preprocessing.SimpleDataPreprocessor;
import org.xtraktor.storage.RedisDataStorage;
import org.xtraktor.storage.SimpleDataStorage;

/**
 * Use this class as an entry point for library simplified use-cases
 */
public class CrossTracker {

    private final DataPreprocessor preprocessor;
    private final DataMiner miner;

    private CrossTracker(LocationConfig config, DataStorage dataStorage) {
        this.preprocessor = new SimpleDataPreprocessor(config, dataStorage);
        this.miner = new SimpleDataMiner(dataStorage);
    }

    /**
     * Create component, backed by preferred storage
     *
     * @param config configuration to obtain instance
     * @param storage storage implementation to use
     * @return instance ready to work
     */
    public static CrossTracker create(LocationConfig config, DataStorage storage) {
        return new CrossTracker(config, storage);
    }

    /**
     * @return {@link DataPreprocessor} to prepare raw data for analysis
     */
    public DataPreprocessor getPreprocessor() {
        return preprocessor;
    }

    /**
     * @return {@link DataMiner} to manipulate and lookup data
     */
    public DataMiner getMiner() {
        return miner;
    }
}
