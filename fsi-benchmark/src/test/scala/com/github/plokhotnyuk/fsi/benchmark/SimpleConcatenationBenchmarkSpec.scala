package com.github.plokhotnyuk.fsi.benchmark

import org.scalatest.{Matchers, WordSpec}

class SimpleConcatenationBenchmarkSpec extends WordSpec with Matchers {
  private val benchmark = new SimpleConcatenationBenchmark
  private val expected =
    "10000xxx10000xxx10000.0xxx10000.0xxxxxxxfalsexxxLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."

  "SimpleConcatenationBenchmark" should {
    "build the same string value" in {
      benchmark.fInterpolator shouldBe expected
      benchmark.fastInterpolator shouldBe expected
      benchmark.frawInterpolator shouldBe expected
      benchmark.fsInterpolator shouldBe expected
      benchmark.javaStringBuilder shouldBe expected
      benchmark.pInterpolator shouldBe expected
      benchmark.rawInterpolator shouldBe expected
      benchmark.scalaStringBuilder shouldBe expected
      benchmark.scalaStringConcatenation shouldBe expected
      benchmark.sInterpolator shouldBe expected
    }
  }
}
