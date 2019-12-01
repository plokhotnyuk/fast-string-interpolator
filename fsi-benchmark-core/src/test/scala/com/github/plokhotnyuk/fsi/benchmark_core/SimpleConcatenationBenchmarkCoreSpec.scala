package com.github.plokhotnyuk.fsi.benchmark_core

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SimpleConcatenationBenchmarkCoreSpec extends AnyWordSpec with Matchers {
  private val benchmark = new SimpleConcatenationBenchmarkCore
  private val expected =
    "10000xxx10000xxx10000.0xxx10000.0xxxxxxxfalsexxxLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."

  "SimpleConcatenationBenchmarkCode" should {
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
