package org.xtraktor.preprocessing;

import com.google.common.collect.Lists;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.xtraktor.HashPoint;
import org.xtraktor.RawPoint;
import org.xtraktor.location.LocationConfig;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Single iteration takes 1000 points and build 1000 interpolated
 * subintervals for each pair. Which means ~1Mln points into iteration output.
 * <p>
 * Benchmark                                                     (latitude)  (longitude)  (timeDelta)  (timestamp)  (userId)  Mode  Cnt      Score   Error  Units
 * SimpleDataPreprocessorBenchmark.benchmarkInterpolateOrdered    48.339571    54.145679      1000000         1000       777  avgt    2  12631.529          ns/op
 * SimpleDataPreprocessorBenchmark.benchmarkInterpolateReversed   48.339571    54.145679      1000000         1000       777  avgt    2  17145.413          ns/op
 * SimpleDataPreprocessorBenchmark.benchmarkPair                  48.339571    54.145679      1000000         1000       777  avgt    2  10185.444          ns/op
 * SimpleDataPreprocessorBenchmark.benchmarkSortOrdered           48.339571    54.145679      1000000         1000       777  avgt    2   2086.020          ns/op
 * SimpleDataPreprocessorBenchmark.benchmarkSortReversed          48.339571    54.145679      1000000         1000       777  avgt    2   6720.635          ns/op
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 2, time = 2)
@Fork(1)
@Threads(1)
@State(Scope.Benchmark)
public class SimpleDataPreprocessorBenchmark {

    private static final int HASH_PRECISION = 8;
    @Param({"54.145679"})
    double longitude;

    @Param({"48.339571"})
    double latitude;

    @Param({"1000"})
    long timestamp;

    @Param({"1000000"})
    long timeDelta;

    @Param({"777"})
    long userId;

    private static final double SEQUENTIAL_SHIFT_BASE = .01;
    private static final double RANDOM_SHIFT_BASE = .005;

    private List<RawPoint> points;
    private List<RawPoint> reversedPoints;
    private SimpleDataPreprocessor preprocessor;

    @Setup
    public void before() {
        final Random random = new Random(1);
        points = LongStream.rangeClosed(0, 1000)
                .mapToObj(it -> {
                    double lon = longitude + it * SEQUENTIAL_SHIFT_BASE
                            + random.nextDouble() * RANDOM_SHIFT_BASE;
                    double lat = latitude + it * SEQUENTIAL_SHIFT_BASE
                            + random.nextDouble() * RANDOM_SHIFT_BASE;
                    long tim = timestamp + timeDelta * it;
                    return new RawPoint(lon, lat, tim, userId);
                })
                .collect(Collectors.toList());
        reversedPoints = Lists.reverse(points);

        preprocessor = new SimpleDataPreprocessor(
                new LocationConfig(1.0, 0, 1000));
    }

    public static void main(String[] args) throws RunnerException {
        org.openjdk.jmh.runner.options.Options res = new OptionsBuilder()
                .include(SimpleDataPreprocessorBenchmark.class.getName() + ".*").build();
        new Runner(res).run();
    }

    @Benchmark
    public Stream<HashPoint> benchmarkInterpolateOrdered() {
        return preprocessor.normalize(points, HASH_PRECISION);
    }

    @Benchmark
    public Stream<HashPoint> benchmarkInterpolateReversed() {
        return preprocessor.normalize(reversedPoints, HASH_PRECISION);
    }

    @Benchmark
    public List<RawPoint> benchmarkSortOrdered() {
        return preprocessor.sort(points);
    }

    @Benchmark
    public List<RawPoint> benchmarkSortReversed() {
        return preprocessor.sort(reversedPoints);
    }

    @Benchmark
    public List<RawPoint> benchmarkPair() {
        return preprocessor.pair(points);
    }
}
