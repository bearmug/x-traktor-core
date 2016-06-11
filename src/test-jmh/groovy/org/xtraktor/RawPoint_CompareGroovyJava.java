package org.xtraktor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.xtraktor.location.LocationConfig;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Compare groovy dynamic / groovy static / javacode performance
 * Sample output
 * <p>
 * Benchmark                                           (latitude)  (longitude)  (timeDelta)  (timestamp)  (userId)  Mode  Cnt     Score   Error  Units
 * RawPoint_CompareGroovyJava.interpolateGroovy         48.339571    54.145679       200000         1000       777  avgt    2   638.319          ns/op
 * RawPoint_CompareGroovyJava.interpolateGroovyStatic   48.339571    54.145679       200000         1000       777  avgt    2  2917.649          ns/op
 * RawPoint_CompareGroovyJava.interpolateJava           48.339571    54.145679       200000         1000       777  avgt    2    74.582          ns/op
 * RawPoint_CompareGroovyJava.isValidGroovy             48.339571    54.145679       200000         1000       777  avgt    2    18.093          ns/op
 * RawPoint_CompareGroovyJava.isValidGroovyStatic       48.339571    54.145679       200000         1000       777  avgt    2    17.871          ns/op
 * RawPoint_CompareGroovyJava.isValidJava               48.339571    54.145679       200000         1000       777  avgt    2    12.344          ns/op
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 2, time = 2)
@Fork(1)
@Threads(1)
@State(Scope.Benchmark)
public class RawPoint_CompareGroovyJava {

    @Param({"54.145679"})
    double longitude;

    @Param({"48.339571"})
    double latitude;

    @Param({"1000"})
    long timestamp;

    @Param({"200000"})
    long timeDelta;

    @Param({"777"})
    long userId;

    private LocationConfig config;

    private RawPointStatic pointStatic;

    private RawPoint pointDynamic;

    private RawPointJava pointJava;

    @Setup
    public void before() {
        config = new LocationConfig(1.0, 8, 0, 1000);

        RawPoint nextPoint = new RawPoint(
                longitude + .5, latitude + .5, timestamp + timeDelta, userId);
        pointDynamic = new RawPoint(longitude, latitude, timestamp, userId, nextPoint);

        RawPointStatic nextPointStatic = new RawPointStatic();
        nextPointStatic.setLongitude(longitude + .5);
        nextPointStatic.setLatitude(latitude + .5);
        nextPointStatic.setTimestamp(timestamp + timeDelta);
        nextPointStatic.setUserId(userId);
        pointStatic = new RawPointStatic();
        pointStatic.setLongitude(longitude);
        pointStatic.setLatitude(latitude);
        pointStatic.setTimestamp(timestamp);
        pointStatic.setUserId(userId);
        pointStatic.setNextPoint(nextPointStatic);

        RawPointJava nextPointJava = new RawPointJava(
                longitude + .5, latitude + .5, timestamp + timeDelta, userId, null);
        pointJava = new RawPointJava(longitude, latitude, timestamp, userId, nextPointJava);
    }

    public static void main(String[] args) throws RunnerException {
        org.openjdk.jmh.runner.options.Options res = new OptionsBuilder()
                .include(RawPoint_CompareGroovyJava.class.getName() + ".*").build();
        new Runner(res).run();
    }

    @Benchmark
    public boolean isValidGroovyStatic() {
        return pointStatic.isValid(config);
    }

    @Benchmark
    public boolean isValidGroovy() {
        return pointDynamic.isValid(config);
    }

    @Benchmark
    public boolean isValidJava() {
        return pointJava.isValid(config);
    }

    @Benchmark
    public Stream interpolateGroovyStatic() {
        return pointStatic.interpolate(config);
    }

    @Benchmark
    public Stream interpolateGroovy() {
        return pointDynamic.interpolate(config);
    }

    @Benchmark
    public Stream interpolateJava() {
        return pointJava.interpolate(config);
    }
}
