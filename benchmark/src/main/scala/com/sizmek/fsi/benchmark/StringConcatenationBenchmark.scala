package com.sizmek.fsi.benchmark

import java.util.concurrent.TimeUnit

import com.sizmek.fsi._
import org.openjdk.jmh.annotations.{Benchmark, _}

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(1)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class StringConcatenationBenchmark {
  private[this] final val stringVal = "Sizmek is the largest independent buy-side advertising platform"
  require(fInterpolator == javaStringBuilder)
  require(frawInterpolator == javaStringBuilder)
  require(fsInterpolator == javaStringBuilder)
  require(rawInterpolator == javaStringBuilder)
  require(scalaStringBuilder == javaStringBuilder)
  require(scalaStringConcatenation == javaStringBuilder)
  require(sInterpolator == javaStringBuilder)

  @Benchmark
  def fInterpolator: String =
    f"${10000}xxx${10000L}xxx${10000f}xxx${10000d}xxx${'x'}xxx${false}xxx$stringVal"

  @Benchmark
  def frawInterpolator: String =
    fraw"${10000}xxx${10000L}xxx${10000f}xxx${10000d}xxx${'x'}xxx${false}xxx$stringVal"

  @Benchmark
  def fsInterpolator: String =
    fs"${10000}xxx${10000L}xxx${10000f}xxx${10000d}xxx${'x'}xxx${false}xxx$stringVal"

  @Benchmark
  def javaStringBuilder: String =
    new java.lang.StringBuilder().append(10000).append("xxx").append(10000L).append("xxx").append(10000f)
      .append("xxx").append(10000d).append("xxx").append('x').append("xxx").append(false).append("xxx")
      .append(stringVal).toString

  @Benchmark
  def rawInterpolator: String =
    raw"${10000}xxx${10000L}xxx${10000f}xxx${10000d}xxx${'x'}xxx${false}xxx$stringVal"

  @Benchmark
  def scalaStringBuilder: String =
    new StringBuilder().append(10000).append("xxx").append(10000L).append("xxx").append(10000f)
      .append("xxx").append(10000d).append("xxx").append('x').append("xxx").append(false).append("xxx")
      .append(stringVal).toString

  @Benchmark
  def scalaStringConcatenation: String =
    10000 + "xxx" + 10000L + "xxx" + 10000f + "xxx" + 10000d + "xxx" + 'x' + "xxx" + false + "xxx" + stringVal

  @Benchmark
  def sInterpolator: String =
    s"${10000}xxx${10000L}xxx${10000f}xxx${10000d}xxx${'x'}xxx${false}xxx$stringVal"
}