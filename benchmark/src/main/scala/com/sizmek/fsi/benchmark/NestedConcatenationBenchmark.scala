package com.sizmek.fsi.benchmark

import java.util.concurrent.TimeUnit

import com.dongxiguo.fastring.Fastring.Implicits._
import com.sizmek.fsi.benchmark_core.NestedConcatenationBenchmarkCore
import org.openjdk.jmh.annotations.{Benchmark, _}
import perfolation._

import scala.language.postfixOps

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(1)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class NestedConcatenationBenchmark extends NestedConcatenationBenchmarkCore {
  @Benchmark
  def fastInterpolator: String =
    fast"head ${(for (j <- 0 until 10 view) yield {
      fast"baz$j $a foo ${(for (i <- 0 until 4 view) yield {
        fast"$a i=$i"
      }).mkFastring(",")} bar\n"
    }).mkFastring("<hr/>")} tail".toString

  @Benchmark
  def pInterpolator: String =
    p"head ${(for (j <- 0 until 10 view) yield {
      p"baz$j $a foo ${(for (i <- 0 until 4 view) yield {
        p"$a i=$i"
      }).mkString(",")} bar\n"
    }).mkString("<hr/>")} tail"
}