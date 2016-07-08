package org.xtraktor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.xtraktor.location.LocationConfig;
import org.xtraktor.storage.RedisDataStorage;
import org.xtraktor.storage.SimpleDataStorage;
import org.xtraktor.storage.StorageUtility;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 2, time = 2)
@Fork(1)
@Threads(1)
@State(Scope.Benchmark)
public class TrackerSequentialBenchmark {

    public static final int USER_ID = 1;
    public static final int HASH_PRECISION = 8;
    private CrossTracker redisTracker;
    private CrossTracker localTracker;
    private RedisServer redisServer;

    @Setup
    public void before() throws IOException, URISyntaxException {
        int port = new StorageUtility().getFreePort();
        redisServer = new RedisServer(port);
        redisServer.start();

        LocationConfig locationConfig = new LocationConfig(1.0, 0, 10000);
        redisTracker = CrossTracker.create(locationConfig, new RedisDataStorage("localhost", port));
        localTracker = CrossTracker.create(locationConfig, new SimpleDataStorage());
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
