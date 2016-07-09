package org.xtraktor;

import java.util.stream.Stream;

/**
 * Represent data storage API to persist normalized {@link HashPoint}
 * inside some structure.
 * <p>
 * Precision argument for storing and lookup has to be the same at the
 * moment.
 */
public interface DataStorage<T> {

    /**
     * Geohash maximum length which is make sense and availableinto the system
     */
    int MAX_HASH_PRECISION = 8;

    /**
     * Persist points into the storage.
     *
     * @param points        normalized points stream
     * @param hashPrecision geohash precision to use for persistance
     * @return always true
     */
    boolean save(Stream<T> points, int hashPrecision);

    /**
     * Lookup and return {@link HashPoint} stream for given input {@link HashPoint}
     * Specifics:<ul>
     * <li>timestamp and geohash to be used from input only</li>
     * <li>there are no geohash calcs inside storage</li>
     * <li>points for same userId omitted from lookup output</li>
     * <li>points equal to input omitted from lookup output</li>
     * </ul>
     *
     * @param input         input point to match against
     * @param hashPrecision hash precision to use for lookup
     * @return stream, containing points with same timestamp and geohash with
     * given precision
     */
    Stream<T> findByHashAndTime(HashPoint input, int hashPrecision);

    /**
     * Totally cleanup storage
     */
    void clear();

    /**
     * Lookup for specific user route
     *
     * @param userId user id to lookup route for
     * @return user route points, sorted naturally by timestamp
     */
    Stream<T> routeForUser(long userId);
}
