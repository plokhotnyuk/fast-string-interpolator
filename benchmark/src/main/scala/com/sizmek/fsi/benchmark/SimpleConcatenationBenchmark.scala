package com.sizmek.fsi.benchmark

import java.util.concurrent.TimeUnit

import com.dongxiguo.fastring.Fastring.Implicits._
import com.sizmek.fsi.benchmark_core.SimpleConcatenationBenchmarkCore
import org.openjdk.jmh.annotations.{Benchmark, _}
import perfolation._

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(1)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class SimpleConcatenationBenchmark extends SimpleConcatenationBenchmarkCore {
  @Benchmark
  final def fastInterpolator: String =
    fast"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string".toString

  @Benchmark
  final def pInterpolator: String =
    p"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string"
}