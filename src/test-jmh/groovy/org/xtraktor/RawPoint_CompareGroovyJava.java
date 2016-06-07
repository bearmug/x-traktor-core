package org.xtraktor;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.xtraktor.location.LocationConfig;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Compare groovy dynamic / groovy static / javacode performance
 * Sample output
 * <p>
 * Benchmark                                           (latitude)  (longitude)  (timeDelta)  (timestamp)  (userId)  Mode  Cnt       Score   Error  Units
 * RawPoint_CompareGroovyJava.interpolateGroovy         48.339571    54.145679       200000         1000       777  avgt    2  729469.434          ns/op
 * RawPoint_CompareGroovyJava.interpolateGroovyStatic   48.339571    54.145679       200000         1000       777  avgt    2  601408.884          ns/op
 * RawPoint_CompareGroovyJava.interpolateJava           48.339571    54.145679       200000         1000       777  avgt    2  322023.285          ns/op
 * RawPoint_CompareGroovyJava.isValidGroovy             48.339571    54.145679       200000         1000       777  avgt    2     129.492          ns/op
 * RawPoint_CompareGroovyJava.isValidGroovyStatic       48.339571    54.145679       200000         1000       777  avgt    2      12.357          ns/op
 * RawPoint_CompareGroovyJava.isValidJava               48.339571    54.145679       200000         1000       777  avgt    2      12.756          ns/op
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

    private RawPoint pointStatic;

    private RawPointDynamic pointDynamic;

    private RawPointJava pointJava;

    @Setup
    public void before() {
        config = new LocationConfig(1.0, 8, 0, 1000);

        RawPoint nextPoint = new RawPoint(
                longitude + .5, latitude + .5, timestamp + timeDelta, userId);
        pointStatic = new RawPoint(longitude, latitude, timestamp, userId, nextPoint);

        RawPointDynamic nextPointDynamic = new RawPointDynamic();
        nextPointDynamic.setLongitude(longitude + .5);
        nextPointDynamic.setLatitude(latitude + .5);
        nextPointDynamic.setTimestamp(timestamp + timeDelta);
        nextPointDynamic.setUserId(userId);
        pointDynamic = new RawPointDynamic();
        pointDynamic.setLongitude(longitude);
        pointDynamic.setLatitude(latitude);
        pointDynamic.setTimestamp(timestamp);
        pointDynamic.setUserId(userId);
        pointDynamic.setNextPoint(nextPointDynamic);

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
    public List interpolateGroovyStatic() {
        return pointStatic.interpolate(config);
    }

    @Benchmark
    public List interpolateGroovy() {
        return pointDynamic.interpolate(config);
    }

    @Benchmark
    public List interpolateJava() {
        return pointJava.interpolate(config);
    }
}
