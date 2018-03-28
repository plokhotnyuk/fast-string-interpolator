package com.sizmek.fsi.benchmark

import com.dongxiguo.fastring.Fastring.Implicits._
import com.sizmek.fsi.benchmark_core.SimpleConcatenationBenchmarkCore
import org.openjdk.jmh.annotations.Benchmark
import perfolation._

class SimpleConcatenationBenchmark extends SimpleConcatenationBenchmarkCore {
  @Benchmark
  def fastInterpolator: String =
    fast"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string".toString

  @Benchmark
  def pInterpolator: String =
    p"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string"
}