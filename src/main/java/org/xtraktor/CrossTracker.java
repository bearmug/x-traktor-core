package org.xtraktor;

import org.xtraktor.location.LocationConfig;
import org.xtraktor.mining.SimpleDataMiner;
import org.xtraktor.preprocessing.SimpleDataPreprocessor;

import java.util.List;
import java.util.stream.Stream;

/**
 * Use this class as an entry point for library simplified use-cases
 */
public class CrossTracker implements DataPreprocessor, DataMiner {

    private final DataPreprocessor preprocessor;
    private final DataMiner miner;

    private CrossTracker(LocationConfig config, DataStorage dataStorage) {
        this.preprocessor = new SimpleDataPreprocessor(config, dataStorage);
        this.miner = new SimpleDataMiner(dataStorage);
    }

    /**
     * Create component, backed by preferred storage
     *
     * @param config  configuration to obtain instance
     * @param storage storage implementation to use
     * @return instance ready to work
     */
    public static CrossTracker create(LocationConfig config, DataStorage storage) {
        return new CrossTracker(config, storage);
    }

    @Override
    public Stream<HashPoint> matchForPoint(HashPoint input, int hashPrecision) {
        return miner.matchForPoint(input, hashPrecision);
    }

    @Override
    public Stream<HashPoint> matchForRoute(List<HashPoint> input, int hashPrecision) {
        return miner.matchForRoute(input, hashPrecision);
    }

    @Override
    public Stream<HashPoint> matchForUser(long userId, int hashPrecision) {
        return miner.matchForUser(userId, hashPrecision);
    }

    @Override
    public Stream<HashPoint> normalize(List<RawPoint> input) {
        return preprocessor.normalize(input);
    }
}
