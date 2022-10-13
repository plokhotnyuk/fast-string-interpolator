package com.github.plokhotnyuk.fsi.benchmark_core

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class NestedConcatenationBenchmarkCoreSpec extends AnyWordSpec with Matchers {
  private val benchmark = new NestedConcatenationBenchmarkCore
  private val expected =
    """head baz0 99999 foo 99999 i=0,99999 i=1,99999 i=2,99999 i=3 bar
      |<hr/>baz1 99999 foo 99999 i=0,99999 i=1,99999 i=2,99999 i=3 bar
      |<hr/>baz2 99999 foo 99999 i=0,99999 i=1,99999 i=2,99999 i=3 bar
      |<hr/>baz3 99999 foo 99999 i=0,99999 i=1,99999 i=2,99999 i=3 bar
      |<hr/>baz4 99999 foo 99999 i=0,99999 i=1,99999 i=2,99999 i=3 bar
      |<hr/>baz5 99999 foo 99999 i=0,99999 i=1,99999 i=2,99999 i=3 bar
      |<hr/>baz6 99999 foo 99999 i=0,99999 i=1,99999 i=2,99999 i=3 bar
      |<hr/>baz7 99999 foo 99999 i=0,99999 i=1,99999 i=2,99999 i=3 bar
      |<hr/>baz8 99999 foo 99999 i=0,99999 i=1,99999 i=2,99999 i=3 bar
      |<hr/>baz9 99999 foo 99999 i=0,99999 i=1,99999 i=2,99999 i=3 bar
      | tail""".stripMargin

  "NestedConcatenationBenchmarkCore" should {
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
