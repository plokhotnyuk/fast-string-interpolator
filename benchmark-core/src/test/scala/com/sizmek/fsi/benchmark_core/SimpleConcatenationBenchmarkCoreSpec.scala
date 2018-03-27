package com.sizmek.fsi.benchmark_core

import org.scalatest.{Matchers, WordSpec}

class SimpleConcatenationBenchmarkCoreSpec extends WordSpec with Matchers {
  private val benchmark = new SimpleConcatenationBenchmarkCore
  private val expected =
    "10000xxx10000xxx10000.0xxx10000.0xxxxxxxfalsexxxSizmek is the largest independent buy-side advertising platform"

  "StringConcatenationBenchmark" should {
    "build the same string value" in {
      benchmark.fInterpolator shouldBe expected
      benchmark.frawInterpolator shouldBe expected
      benchmark.fsInterpolator shouldBe expected
      benchmark.javaStringBuilder shouldBe expected
      benchmark.rawInterpolator shouldBe expected
      benchmark.scalaStringBuilder shouldBe expected
      benchmark.scalaStringConcatenation shouldBe expected
      benchmark.sInterpolator shouldBe expected
    }
  }
}
