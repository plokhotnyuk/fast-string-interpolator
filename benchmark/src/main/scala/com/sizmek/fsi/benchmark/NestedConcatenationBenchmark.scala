package com.sizmek.fsi.benchmark

import com.dongxiguo.fastring.Fastring.Implicits._
import com.sizmek.fsi.benchmark_core.NestedConcatenationBenchmarkCore
import org.openjdk.jmh.annotations.Benchmark
import perfolation._

import scala.language.postfixOps

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