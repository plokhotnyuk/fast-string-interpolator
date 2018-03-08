package com.sizmek.fsi.macros

import com.sizmek.fsi._
import org.scalatest.exceptions.TestFailedException
import org.scalatest.{Matchers, WordSpec}

class FastStringInterpolatorSpec extends WordSpec with Matchers {
  "FastStringInterpolator.fs" should {
    "call args exactly once and in order of declaration in a string literal" in {
      var x = 0

      def f(): Null = {
        x += 1
        null
      }

      def g(): Int = {
        x *= 9
        x
      }

      fs"${f()}${g()}" shouldBe "null9"
    }
    "build the same string as a simple string interpolator" in {
      fs"${null}${1}${'A'}${"A"}" shouldBe s"${null}${1}${'A'}${"A"}"
      fs"${null}x${1}x${'A'}x${"A"}" shouldBe s"${null}x${1}x${'A'}x${"A"}"
      fs"${null}xx${1}xx${'A'}xx${"A"}" shouldBe s"${null}xx${1}xx${'A'}xx${"A"}"
      fs"[${fs"<${fs"{${fs"${1}".toInt}}"}>"}]" shouldBe s"[${s"<${s"{${s"${1}".toInt}}"}>"}]"
      fs"\b\f\n\t\1\11\111" shouldBe s"\b\f\n\t\1\11\111"
      fs""""""" shouldBe s"""""""
      (1 to 16384).foldLeft("")((s, i) => fs"$s$i") shouldBe (1 to 16384).foldLeft("")((s, i) => s"$s$i")
    }
    "don't compile in case of escaping error" in {
      assert(intercept[TestFailedException](assertCompiles(""" fs"\d" """)).getMessage.contains {
        """invalid escape '\d' not one of [\b, \t, \n, \f, \r, \\, \", \'] at index 0 in "\d". Use \\ for literal \."""
      })
    }
  }
  "FastStringInterpolator.fraw" should {
    "call args exactly once and in order of declaration in a string literal" in {
      var x = 0

      def f(): Null = {
        x += 1
        null
      }

      def g(): Int = {
        x *= 9
        x
      }

      fraw"${f()}${g()}" shouldBe "null9"
    }
    "build the same string as a raw string interpolator" in {
      fraw"${null}${1}${'A'}${"A"}" shouldBe raw"${null}${1}${'A'}${"A"}"
      fraw"${null}x${1}x${'A'}x${"A"}" shouldBe raw"${null}x${1}x${'A'}x${"A"}"
      fraw"${null}xx${1}xx${'A'}xx${"A"}" shouldBe raw"${null}xx${1}xx${'A'}xx${"A"}"
      fraw"[${fraw"<${fraw"{${fraw"${1}".toInt}}"}>"}]" shouldBe raw"[${raw"<${raw"{${raw"${1}".toInt}}"}>"}]"
      fraw"\b\f\n\t\1\11\111" shouldBe raw"\b\f\n\t\1\11\111"
      fraw""""""" shouldBe raw"""""""
      (1 to 16384).foldLeft("")((s, i) => fraw"$s$i") shouldBe (1 to 16384).foldLeft("")((s, i) => raw"$s$i")
    }
  }
}
