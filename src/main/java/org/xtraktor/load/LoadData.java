package org.xtraktor.load;

import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;

import java.util.stream.Stream;

/**
 * API used to pre-load data into the system from some external source
 */
public interface LoadData {

    /**
     * Main API method to call for data persistence.
     *
     * @param storage   {@link DataStorage} implementation to save loaded points into
     * @param precision geo hash precision to use
     */
    default void load(DataStorage storage, int precision) {
        storage.save(openDataStream(), precision);
    }

    /**
     * Design shortcut to declare underlying data abstraction
     *
     * @return {@link HashPoint} stream to save
     */
    Stream<HashPoint> openDataStream();
}
