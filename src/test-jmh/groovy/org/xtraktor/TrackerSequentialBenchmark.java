package org.xtraktor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.xtraktor.location.LocationConfig;
import org.xtraktor.storage.SimpleDataStorage;
import org.xtraktor.storage.redis.RedisDataStorage;
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
 * Single-threaded benchmark with ~50K selections for each output.
 * <p>
 * Benchmark                                   (latitude)  (longitude)  (timeDelta)  (timestamp)  Mode  Cnt          Score   Error  Units
 * TrackerSequentialBenchmark.localUserList     48.339571    54.145679       100000         1000  avgt    2    2701444.520          ns/op
 * TrackerSequentialBenchmark.localUserStream   48.339571    54.145679       100000         1000  avgt    2        247.154          ns/op
 * TrackerSequentialBenchmark.redisUserList     48.339571    54.145679       100000         1000  avgt    2  107842809.242          ns/op
 * TrackerSequentialBenchmark.redisUserStream   48.339571    54.145679       100000         1000  avgt    2    3267540.738          ns/op
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 2, time = 2)
@Fork(1)
@Threads(1)
@State(Scope.Benchmark)
public class TrackerSequentialBenchmark {

    private static final int USER_ID = 5;
    private static final int HASH_PRECISION = 8;
    private static final double SEQUENTIAL_SHIFT_BASE = .01;
    private static final double RANDOM_SHIFT_BASE = .005;

    @Shared
    private CrossTracker<HashPoint> redisTracker;

    @Shared
    private CrossTracker<HashPoint> localTracker;

    @Shared
    private RedisServer redisServer;

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
        int port = new StorageUtility(0).getFreePort();
        redisServer = new RedisServer(port);
        redisServer.start();

        LocationConfig locationConfig = new LocationConfig(1.0, 0, timeDelta / 3);
        redisTracker = CrossTracker.create(locationConfig, new RedisDataStorage("localhost", port));
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
                    redisTracker.normalize(userPoints, HASH_PRECISION);
                    localTracker.normalize(userPoints, HASH_PRECISION);
                });
    }

    @TearDown
    public void after() throws InterruptedException {
        redisServer.stop();
    }

    @Benchmark
    public Stream<HashPoint> redisUserStream() {
        return redisTracker.matchForUser(USER_ID, HASH_PRECISION);
    }

    @Benchmark
    public List<HashPoint> redisUserList() {
        return redisTracker.matchForUser(USER_ID, HASH_PRECISION).collect(Collectors.toList());
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
                .include(TrackerSequentialBenchmark.class.getName() + ".*").build();
        new Runner(res).run();
    }
}
