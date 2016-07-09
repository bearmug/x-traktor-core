package org.xtraktor

import org.xtraktor.location.LocationConfig
import org.xtraktor.mining.SimpleDataMiner
import org.xtraktor.preprocessing.SimpleDataPreprocessor

/**
 * Use this class as an entry point for library simplified use-cases
 */
class CrossTracker<T> implements DataMiner<T>, DataPreprocessor {

    /**
     * {@link DataPreprocessor} interface implemented with delegation
     */
    @Delegate
    private final DataPreprocessor preprocessor;

    /**
     * {@link DataMiner} interface implemented with delegation
     */
    @Delegate
    private final DataMiner<T> miner;

    private CrossTracker(LocationConfig config, DataStorage<T> dataStorage) {
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
    public static <T> CrossTracker<T> create(LocationConfig config, DataStorage<T> storage) {
        return new CrossTracker<T>(config, storage);
    }
}
