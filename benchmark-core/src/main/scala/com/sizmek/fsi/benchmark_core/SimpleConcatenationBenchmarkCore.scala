package com.sizmek.fsi.benchmark_core

import java.util.concurrent.TimeUnit

import com.sizmek.fsi._
import org.openjdk.jmh.annotations.{Benchmark, _}

@State(Scope.Benchmark)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(value = 1, jvmArgs = Array(
  "-server",
  "-Xms1g",
  "-Xmx1g",
  "-XX:NewSize=512m",
  "-XX:MaxNewSize=512m",
  "-XX:InitialCodeCacheSize=256m",
  "-XX:ReservedCodeCacheSize=256m",
  "-XX:-UseBiasedLocking",
  "-XX:+AlwaysPreTouch"
))
class SimpleConcatenationBenchmarkCore {
  var int: Int = _
  var long: Long = _
  var float: Float = _
  var double: Double = _
  var char: Char = _
  var boolean: Boolean = _
  var string: String = _

  {
    int = 10000
    long = 10000L
    float = 10000f
    double = 10000d
    char = 'x'
    boolean = false
    string = "Sizmek is the largest independent buy-side advertising platform"
  }

  @Benchmark
  def fInterpolator: String =
    f"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string"

  @Benchmark
  def frawInterpolator: String =
    fraw"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string"

  @Benchmark
  def fsInterpolator: String =
    fs"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string"

  @Benchmark
  def javaStringBuilder: String =
    new java.lang.StringBuilder().append(int).append("xxx").append(long).append("xxx").append(float)
      .append("xxx").append(double).append("xxx").append(char).append("xxx").append(boolean)
      .append("xxx").append(string).toString

  @Benchmark
  def rawInterpolator: String =
    raw"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string"

  @Benchmark
  def scalaStringBuilder: String =
    new StringBuilder().append(int).append("xxx").append(long).append("xxx").append(float)
      .append("xxx").append(double).append("xxx").append(char).append("xxx").append(boolean)
      .append("xxx").append(string).toString

  @Benchmark
  def scalaStringConcatenation: String =
    int + "xxx" + long + "xxx" + float + "xxx" + double + "xxx" + char + "xxx" + boolean + "xxx" + string

  @Benchmark
  def sInterpolator: String =
    s"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string"
}