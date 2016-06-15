package org.xtraktor.storage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SimpleDataStorage implements DataStorage {

    public static final int GEO_HASH_PRECISION = 8;
    private final Map<Long, Multimap<String, HashPoint>> map =new ConcurrentHashMap<>();

    @Override
    public boolean save(Stream<HashPoint> points) {

        points.parallel()
                .forEach(p -> {
            Multimap<String, HashPoint> nestedMap = getByTimestamp(p.getTimestamp());
            nestedMap.put(p.getHash(GEO_HASH_PRECISION), p);
        });

        return true;
    }

    private Multimap<String, HashPoint> getByTimestamp(long timestamp) {
        Multimap<String, HashPoint> res = map.get(timestamp);
        if (res == null) {
            synchronized (map) {
                res = map.get(timestamp);
                if (res == null) {
                    map.put(timestamp, Multimaps.
                            synchronizedSetMultimap(HashMultimap.create()));
                }
            }

        }
        return res;
    }

    @Override
    public Stream<HashPoint> findByHashAndTime(HashPoint input, int hashPrecision) {
        return null;
    }
}
