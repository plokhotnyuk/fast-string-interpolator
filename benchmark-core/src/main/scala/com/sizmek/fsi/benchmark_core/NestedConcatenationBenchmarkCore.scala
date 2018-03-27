package com.sizmek.fsi.benchmark_core

import java.util.concurrent.TimeUnit

import com.sizmek.fsi._
import org.openjdk.jmh.annotations.{Benchmark, _}

import scala.language.postfixOps

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
@Fork(1)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
class NestedConcatenationBenchmarkCore {
  val n: Char = '\n'
  var a: Int = _

  {
    a = 99999
  }

  @Benchmark
  final def fInterpolator: String =
    f"head ${(for (j <- 0 until 10 view) yield {
      f"baz$j $a foo ${(for (i <- 0 until 4 view) yield {
        f"$a i=$i"
      }).mkString(",")} bar$n"
    }).mkString("<hr/>")} tail"

  @Benchmark
  final def frawInterpolator: String =
    fraw"head ${(for (j <- 0 until 10 view) yield {
      fraw"baz$j $a foo ${(for (i <- 0 until 4 view) yield {
        fraw"$a i=$i"
      }).mkString(",")} bar$n"
    }).mkString("<hr/>")} tail"

  @Benchmark
  final def fsInterpolator: String =
    fs"head ${(for (j <- 0 until 10 view) yield {
      fs"baz$j $a foo ${(for (i <- 0 until 4 view) yield {
        fs"$a i=$i"
      }).mkString(",")} bar\n"
    }).mkString("<hr/>")} tail"

  @Benchmark
  final def javaStringBuilder: String = {
    val sb: java.lang.StringBuilder = new java.lang.StringBuilder
    sb.append("head ")
    var first = true
    for (j <- 0 until 10 view) {
      if (first) first = false
      else sb.append("<hr/>")
      sb.append("baz").append(j).append(" ").append(a).append(" foo ");
      {
        var first = true
        for (i <- 0 until 4 view) {
          if (first) first = false
          else sb.append(",")
          sb.append(a)
          sb.append(" i=")
          sb.append(i)
        }
      }
      sb.append(" bar\n")
    }
    sb.append(" tail")
    sb.toString
  }

  @Benchmark
  final def rawInterpolator: String =
    raw"head ${(for (j <- 0 until 10 view) yield {
      raw"baz$j $a foo ${(for (i <- 0 until 4 view) yield {
        raw"$a i=$i"
      }).mkString(",")} bar$n"
    }).mkString("<hr/>")} tail"

  @Benchmark
  final def scalaStringBuilder: String = {
    val sb = new StringBuilder
    sb.append("head ")
    var first = true
    for (j <- 0 until 10 view) {
      if (first) first = false
      else sb.append("<hr/>")
      sb.append("baz").append(j).append(" ").append(a).append(" foo ");
      {
        var first = true
        for (i <- 0 until 4 view) {
          if (first) first = false
          else sb.append(",")
          sb.append(a)
          sb.append(" i=")
          sb.append(i)
        }
      }
      sb.append(" bar\n")
    }
    sb.append(" tail")
    sb.toString
  }

  @Benchmark
  final def scalaStringConcatenation: String =
    "head " + (for (j <- 0 until 10 view) yield {
      "baz" + j + " " + a + " foo " + (for (i <- 0 until 4 view) yield {
        "" + a + " i=" + i
      }).mkString(",") + " bar\n"
    }).mkString("<hr/>") + " tail"

  @Benchmark
  final def sInterpolator: String =
    s"head ${(for (j <- 0 until 10 view) yield {
      s"baz$j $a foo ${(for (i <- 0 until 4 view) yield {
        s"$a i=$i"
      }).mkString(",")} bar\n"
    }).mkString("<hr/>")} tail"
}