package org.xtraktor.storage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;
import org.xtraktor.location.LocationConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SimpleDataStorage implements DataStorage {

    private final Map<Long, Multimap<String, HashPoint>> map = new ConcurrentHashMap<>();

    private int precision = LocationConfig.PRECISION;

    @Override
    public boolean save(Stream<HashPoint> points) {

        points.parallel()
                .forEach(p -> {
                    Multimap<String, HashPoint> nestedMap = getByTimestamp(p.getTimestamp());
                    nestedMap.put(p.getHash(precision), p);
                });

        return true;
    }

    private Multimap<String, HashPoint> getByTimestamp(long timestamp) {
        Multimap<String, HashPoint> bucket = map.get(timestamp);
        if (bucket == null) synchronized (map) {
            bucket = map.get(timestamp);
            if (bucket == null) {
                bucket = Multimaps.
                        synchronizedSetMultimap(HashMultimap.create());
                map.put(timestamp, bucket);
            }
        }
        return bucket;
    }

    @Override
    public Stream<HashPoint> findByHashAndTime(HashPoint input, int hashPrecision) {

        Multimap<String, HashPoint> bucket = map.get(input.getTimestamp());
        if (bucket == null) {
            return Stream.empty();
        }

        return bucket.get(input.getHash(hashPrecision))
                .stream()
                .filter(p ->
                        p != input && p.getUserId() != input.getUserId());
    }
}
