package org.xtraktor.storage;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xtraktor.DataStorage;
import org.xtraktor.HashPoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class SimpleDataStorage implements DataStorage {

    private final Logger log = LoggerFactory.getLogger(SimpleDataStorage.class);

    private final Map<Long, Multimap<String, HashPoint>> timeMap =
            new ConcurrentHashMap<>();

    private final Multimap<Long, HashPoint> userMap =
            Multimaps.synchronizedSetMultimap(HashMultimap.create());

    @Override
    public boolean save(Stream<HashPoint> points, int hashPrecision) {

        final AtomicInteger counter = new AtomicInteger();
        log.trace("Points stream: {} to be saved with precision: {}", points, hashPrecision);
        points.parallel()
                .forEach(p -> {
                    // store by timestamp
                    Multimap<String, HashPoint> tMap = getByTimestamp(p.getTimestamp());
                    tMap.put(p.getHash(hashPrecision), p);

                    // store by userId
                    userMap.put(p.getUserId(), p);
                    log.trace("Counter {}", counter.incrementAndGet());
                });

        log.debug("Points stream: {} saved with precision: {}", points, hashPrecision);
        return true;
    }

    private Multimap<String, HashPoint> getByTimestamp(long timestamp) {
        Multimap<String, HashPoint> bucket = timeMap.get(timestamp);
        if (bucket == null) synchronized (timeMap) {
            bucket = timeMap.get(timestamp);
            if (bucket == null) {
                bucket = Multimaps.
                        synchronizedSetMultimap(HashMultimap.create());
                timeMap.put(timestamp, bucket);
            }
        }
        return bucket;
    }

    @Override
    public Stream<HashPoint> findByHashAndTime(HashPoint input, int hashPrecision) {

        Multimap<String, HashPoint> bucket = timeMap.get(input.getTimestamp());
        if (bucket == null) {
            return Stream.empty();
        }

        return bucket.get(input.getHash(hashPrecision))
                .stream()
                .filter(p ->
                        p != input && p.getUserId() != input.getUserId());
    }

    @Override
    public void clear() {
        timeMap.clear();
    }

    @Override
    public Stream<HashPoint> routeForUser(long userId) {
        return userMap.get(userId)
                .stream()
                .sorted((p1, p2) -> Long.compare(p1.getTimestamp(), p2.getTimestamp()));
    }
}
