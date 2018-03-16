package com.sizmek.fsi.benchmark

import java.util.concurrent.TimeUnit

import com.dongxiguo.fastring.Fastring.Implicits._
import com.sizmek.fsi._
import org.openjdk.jmh.annotations.{Benchmark, _}
import perfolation._

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(1)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class StringConcatenationBenchmark {
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
  def fastInterpolator: String =
    fast"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string".toString

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
  def pInterpolator: String =
    p"${int}xxx${long}xxx${float}xxx${double}xxx${char}xxx${boolean}xxx$string"

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