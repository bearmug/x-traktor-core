package org.xtraktor;

import com.google.common.math.DoubleMath;
import org.codehaus.groovy.runtime.typehandling.BigDecimalMath;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Validate two double numbers comparison performance.
 * Sample report:
 * <p>
 * Benchmark                                          (param1)  (param2)  Mode  Cnt   Score   Error  Units
 * RawPoint_CompareDoubleBenchmark.isCloseBigGuava    1.000003   2.00001  avgt    2   3.809          ns/op
 * RawPoint_CompareDoubleBenchmark.isCloseBigMath     1.000003   2.00001  avgt    2  14.035          ns/op
 * RawPoint_CompareDoubleBenchmark.isClosePlainGuava  1.000003   2.00001  avgt    2   5.093          ns/op
 * RawPoint_CompareDoubleBenchmark.isClosePlainMath   1.000003   2.00001  avgt    2   3.860          ns/op
 */

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 2, time = 2)
@Measurement(iterations = 2, time = 2)
@Fork(1)
@Threads(1)
@State(Scope.Benchmark)
public class RawPoint_CompareDoubleBenchmark {

    private static final double TOLERANCE = 1.0;

    @Param({"1.000003"})
    private double param1;

    @Param({"2.00001"})
    private double param2;

    private BigDecimal bigDecimalParam1;

    private BigDecimal bigDecimalParam2;

    @Setup
    public void before() {
        bigDecimalParam1 = BigDecimal.valueOf(param1);
        bigDecimalParam2 = BigDecimal.valueOf(param2);
    }

    public static void main(String[] args) throws RunnerException {
        org.openjdk.jmh.runner.options.Options res = new OptionsBuilder()
                .include(RawPoint_CompareDoubleBenchmark.class.getName() + ".*").build();
        new Runner(res).run();
    }

    @Benchmark
    public boolean isClosePlainMath() {
        return Math.abs(param2 - param1) < TOLERANCE;
    }

    @Benchmark
    public boolean isClosePlainGuava() {
        return DoubleMath.fuzzyEquals(param1, param2, TOLERANCE);
    }

    @Benchmark
    public boolean isCloseBigMath() {
        return bigDecimalParam2.subtract(bigDecimalParam1).abs().doubleValue() < TOLERANCE;
    }

    @Benchmark
    public boolean isCloseBigGuava() {
        return BigDecimalMath.subtract(param2, param1).doubleValue() < TOLERANCE;
    }
}
