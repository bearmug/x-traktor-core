package org.xtraktor;

import java.util.List;
import java.util.stream.Stream;

/**
 * API to normalize incoming GPS ungrained data. Providing
 * output with predefined discretion time/space discretion.
 */
public interface DataPreprocessor {

    /**
     * Consuming input {@link RawPoint} and:
     * <ul>
     * <li>bring input into the order</li>
     * <li>filter invalid elements</li>
     * <li>composing points into the sequence</li>
     * <li>for each points pair into the sequence:
     * <ul>
     * <li>lookup for a number of time-aligned intermediate locations</li>
     * <li>generate {@link HashPoint} for each time-aligned location</li>
     * <li>such time-aligned points always have strict timestamp assigned</li>
     * <li>each of generated {@link HashPoint} assigned with geohash
     * with pre-defined precision</li>
     * </ul>
     * </li>
     * </ul>
     *
     * @param input {@link RawPoint} incoming route for specific user. Pease be
     *              sure that same userId for each element there
     * @return stream with {@link HashPoint}s built for incoming route
     */
    Stream<HashPoint> normalize(List<RawPoint> input);
}
