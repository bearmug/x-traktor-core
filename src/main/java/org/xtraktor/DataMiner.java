package org.xtraktor;

import java.util.List;
import java.util.stream.Stream;

/**
 * API to lookup users routes correlations and intersections. Typical use-case:
 * for given and pre-processed data we would like to find who and when located
 * nearby some specific system user along his/her route for specific time
 * interval.
 * <p/>
 * In other words, we do lookup for closest buddies at very specific moment.
 * <p/>
 * Point from the same user are not included
 */
public interface DataMiner {

    /**
     * Lookup for intersections with other users for:
     * <ul>
     * <li>given location, provided by input {@link HashPoint}</li>
     * <li>given timestamp, provided by input {@link HashPoint}</li>
     * <li>given geo precision, provided by next argument</li>
     * </ul>
     *
     * @param input         time/space location to lookup around
     * @param hashPrecision geo-precision measurement to lookup
     * @return stream with detected intersections.
     */
    Stream<HashPoint> matchForPoint(HashPoint input, int hashPrecision);

    /**
     * Lookup for intersections with other users along the chained route
     *
     * @param input         user route, provided by time/location points sequence
     * @param hashPrecision geo precision to use for lookup
     * @return resulting summary withfull intersection along the route. Sorting
     * is not guaranteed.
     */
    Stream<HashPoint> matchForRoute(List<HashPoint> input, int hashPrecision);
}
