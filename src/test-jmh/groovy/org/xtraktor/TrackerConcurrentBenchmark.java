package org.xtraktor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.xtraktor.location.LocationConfig;
import org.xtraktor.storage.SimpleDataStorage;
import org.xtraktor.storage.redis.RedisDataStorage;
import org.xtraktor.storage.redis.RedisJsonStorage;
import org.xtraktor.storage.redis.StorageUtility;
import redis.embedded.RedisServer;
import spock.lang.Shared;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * 4-threads concurrent run mode at heavy selections. Each output has about 50K elements.
 * <p>
 * Benchmark                                       (latitude)  (longitude)  (timeDelta)  (timestamp)  Mode  Cnt         Score   Error  Units
 * TrackerConcurrentBenchmark.localUserList         48.339571    54.145679       100000         1000  avgt    2    767680.898          ns/op
 * TrackerConcurrentBenchmark.localUserStream       48.339571    54.145679       100000         1000  avgt    2      2643.818          ns/op
 * TrackerConcurrentBenchmark.redisUserList         48.339571    54.145679       100000         1000  avgt    2  52927749.731          ns/op
 * TrackerConcurrentBenchmark.redisUserListJson     48.339571    54.145679       100000         1000  avgt    2  49577343.037          ns/op
 * TrackerConcurrentBenchmark.redisUserStream       48.339571    54.145679       100000         1000  avgt    2   1443626.509          ns/op
 * TrackerConcurrentBenchmark.redisUserStreamJson   48.339571    54.145679       100000         1000  avgt    2   1471547.779          ns/op
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 2, time = 2)
@Fork(1)
@Threads(4)
@State(Scope.Benchmark)
public class TrackerConcurrentBenchmark {

    private static final int USER_ID = 5;
    private static final int HASH_PRECISION = 8;
    private static final double SEQUENTIAL_SHIFT_BASE = .01;
    private static final double RANDOM_SHIFT_BASE = .005;

    @Shared
    private CrossTracker<HashPoint> pojoRedisTracker;

    @Shared
    private CrossTracker<String> jsonRedisTracker;

    @Shared
    private CrossTracker<HashPoint> localTracker;

    @Shared
    private RedisServer pojoRedisServer;

    @Shared
    private RedisServer jsonRedisServer;

    @Param({"54.145679"})
    private double longitude;

    @Param({"48.339571"})
    private double latitude;

    @Param({"1000"})
    private long timestamp;

    @Param({"100000"})
    private long timeDelta;

    @Setup
    public void before() throws IOException, URISyntaxException {
        int pojoPort = new StorageUtility().getFreePort();
        pojoRedisServer = new RedisServer(pojoPort);
        pojoRedisServer.start();

        int jsonPort = new StorageUtility().getFreePort();
        jsonRedisServer = new RedisServer(jsonPort);
        jsonRedisServer.start();

        LocationConfig locationConfig = new LocationConfig(1.0, 0, timeDelta / 3);
        pojoRedisTracker = CrossTracker.create(locationConfig, new RedisDataStorage("localhost", pojoPort));
        jsonRedisTracker = CrossTracker.create(locationConfig, new RedisJsonStorage("localhost", jsonPort));
        localTracker = CrossTracker.create(locationConfig, new SimpleDataStorage());

        generateData();
    }

    private void generateData() {
        final Random random = new Random(1);

        LongStream.rangeClosed(1, 100)
                .parallel()
                .forEach(userId -> {
                    List<RawPoint> userPoints = LongStream.rangeClosed(0, 1000)
                            .mapToObj(it -> {
                                double lon = longitude + it * SEQUENTIAL_SHIFT_BASE
                                        + (random.nextDouble() + userId / 100) * RANDOM_SHIFT_BASE;
                                double lat = latitude + it * SEQUENTIAL_SHIFT_BASE
                                        + (random.nextDouble() + userId / 100) * RANDOM_SHIFT_BASE;
                                long tim = timestamp + timeDelta * it;
                                return new RawPoint(lon, lat, tim, userId);
                            })
                            .collect(Collectors.toList());
                    jsonRedisTracker.normalize(userPoints, HASH_PRECISION);
                    pojoRedisTracker.normalize(userPoints, HASH_PRECISION);
                    localTracker.normalize(userPoints, HASH_PRECISION);
                });
    }

    @TearDown
    public void after() throws InterruptedException {
        pojoRedisServer.stop();
        jsonRedisServer.stop();
    }

    @Benchmark
    public Stream<HashPoint> redisUserStream() {
        return pojoRedisTracker.matchForUser(USER_ID, HASH_PRECISION);
    }

    @Benchmark
    public List<HashPoint> redisUserList() {
        return pojoRedisTracker.matchForUser(USER_ID, HASH_PRECISION).collect(Collectors.toList());
    }

    @Benchmark
    public Stream<String> redisUserStreamJson() {
        return jsonRedisTracker.matchForUser(USER_ID, HASH_PRECISION);
    }

    @Benchmark
    public List<String> redisUserListJson() {
        return jsonRedisTracker.matchForUser(USER_ID, HASH_PRECISION).collect(Collectors.toList());
    }

    @Benchmark
    public Stream<HashPoint> localUserStream() {
        return localTracker.matchForUser(USER_ID, HASH_PRECISION);
    }

    @Benchmark
    public List<HashPoint> localUserList() {
        return localTracker.matchForUser(USER_ID, HASH_PRECISION).collect(Collectors.toList());
    }

    public static void main(String[] args) throws RunnerException {
        org.openjdk.jmh.runner.options.Options res = new OptionsBuilder()
                .include(TrackerConcurrentBenchmark.class.getName() + ".*").build();
        new Runner(res).run();
    }
}
