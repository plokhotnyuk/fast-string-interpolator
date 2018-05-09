package com.sizmek.fsi.macros

import com.sizmek.fsi._
import com.sizmek.fsi.macros.FastStringInterpolatorSpec._
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
      fs"\b\f\n\t\\\'" shouldBe s"\b\f\n\t\\\'"
      fs""""""" shouldBe s"""""""
      (1 to 16384).foldLeft("")((s, i) => fs"$s$i") shouldBe (1 to 16384).foldLeft("")((s, i) => s"$s$i")
    }
    "work fine with lot of params" in {
      fs"""$a1 $a2 $a3 $a4 $a5 $a6 $a7 $a8 $a9 $a10 $a11 $a12 $a13 $a14 $a15 $a16 $a17 $a18 $a19 $a20 $a21 $a22 $a23 $a24 $a25 $a26 $a27 $a28 $a29 $a30 $a31 $a32 $a33 $a34 $a35 $a36 $a37 $a38 $a39 $a40 $a41 $a42 $a43 $a44 $a45 $a46 $a47 $a48 $a49 $a50 $a51 $a52 $a53 $a54 $a55 $a56 $a57 $a58 $a59 $a60 $a61 $a62 $a63 $a64 $a65 $a66 $a67 $a68 $a69 $a70 $a71 $a72 $a73 $a74 $a75 $a76 $a77 $a78 $a79 $a80 $a81 $a82 $a83 $a84 $a85 $a86 $a87 $a88 $a89 $a90 $a91 $a92 $a93 $a94 $a95 $a96 $a97 $a98 $a99 $a100 $a101 $a102 $a103 $a104 $a105 $a106 $a107 $a108 $a109 $a110 $a111 $a112 $a113 $a114 $a115 $a116 $a117 $a118 $a119 $a120 $a121 $a122 $a123 $a124 $a125 $a126 $a127 $a128 $a129 $a130 $a131 $a132 $a133 $a134 $a135 $a136 $a137 $a138 $a139 $a140 $a141 $a142 $a143 $a144 $a145 $a146 $a147 $a148 $a149 $a150 $a151 $a152 $a153 $a154 $a155 $a156 $a157 $a158 $a159 $a160 $a161 $a162 $a163 $a164 $a165 $a166 $a167 $a168 $a169 $a170 $a171 $a172 $a173 $a174 $a175 $a176 $a177 $a178 $a179 $a180 $a181 $a182 $a183 $a184 $a185 $a186 $a187 $a188 $a189 $a190 $a191 $a192 $a193 $a194 $a195 $a196 $a197 $a198 $a199 $a200 $a201 $a202 $a203 $a204 $a205 $a206 $a207 $a208 $a209 $a210 $a211 $a212 $a213 $a214 $a215 $a216 $a217 $a218 $a219 $a220 $a221 $a222 $a223 $a224 $a225 $a226 $a227 $a228 $a229 $a230 $a231 $a232 $a233 $a234 $a235 $a236 $a237 $a238 $a239 $a240 $a241 $a242 $a243 $a244 $a245 $a246 $a247 $a248 $a249 $a250 $a251 $a252 $a253 $a254 $a255 $a256""" shouldBe " " * 511
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
      fraw"\b\f\n\t\\\'\1\11\111" shouldBe raw"\b\f\n\t\\\'\1\11\111"
      fraw""""""" shouldBe raw"""""""
      (1 to 16384).foldLeft("")((s, i) => fraw"$s$i") shouldBe (1 to 16384).foldLeft("")((s, i) => raw"$s$i")
    }
    "work fine with lot of params" in {
      fraw"""$a1 $a2 $a3 $a4 $a5 $a6 $a7 $a8 $a9 $a10 $a11 $a12 $a13 $a14 $a15 $a16 $a17 $a18 $a19 $a20 $a21 $a22 $a23 $a24 $a25 $a26 $a27 $a28 $a29 $a30 $a31 $a32 $a33 $a34 $a35 $a36 $a37 $a38 $a39 $a40 $a41 $a42 $a43 $a44 $a45 $a46 $a47 $a48 $a49 $a50 $a51 $a52 $a53 $a54 $a55 $a56 $a57 $a58 $a59 $a60 $a61 $a62 $a63 $a64 $a65 $a66 $a67 $a68 $a69 $a70 $a71 $a72 $a73 $a74 $a75 $a76 $a77 $a78 $a79 $a80 $a81 $a82 $a83 $a84 $a85 $a86 $a87 $a88 $a89 $a90 $a91 $a92 $a93 $a94 $a95 $a96 $a97 $a98 $a99 $a100 $a101 $a102 $a103 $a104 $a105 $a106 $a107 $a108 $a109 $a110 $a111 $a112 $a113 $a114 $a115 $a116 $a117 $a118 $a119 $a120 $a121 $a122 $a123 $a124 $a125 $a126 $a127 $a128 $a129 $a130 $a131 $a132 $a133 $a134 $a135 $a136 $a137 $a138 $a139 $a140 $a141 $a142 $a143 $a144 $a145 $a146 $a147 $a148 $a149 $a150 $a151 $a152 $a153 $a154 $a155 $a156 $a157 $a158 $a159 $a160 $a161 $a162 $a163 $a164 $a165 $a166 $a167 $a168 $a169 $a170 $a171 $a172 $a173 $a174 $a175 $a176 $a177 $a178 $a179 $a180 $a181 $a182 $a183 $a184 $a185 $a186 $a187 $a188 $a189 $a190 $a191 $a192 $a193 $a194 $a195 $a196 $a197 $a198 $a199 $a200 $a201 $a202 $a203 $a204 $a205 $a206 $a207 $a208 $a209 $a210 $a211 $a212 $a213 $a214 $a215 $a216 $a217 $a218 $a219 $a220 $a221 $a222 $a223 $a224 $a225 $a226 $a227 $a228 $a229 $a230 $a231 $a232 $a233 $a234 $a235 $a236 $a237 $a238 $a239 $a240 $a241 $a242 $a243 $a244 $a245 $a246 $a247 $a248 $a249 $a250 $a251 $a252 $a253 $a254 $a255 $a256""" shouldBe " " * 511
    }
  }
}

object FastStringInterpolatorSpec {
  val a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18, a19, a20, a21, a22, a23, a24, a25, a26, a27, a28, a29, a30, a31, a32, a33, a34, a35, a36, a37, a38, a39, a40, a41, a42, a43, a44, a45, a46, a47, a48, a49, a50, a51, a52, a53, a54, a55, a56, a57, a58, a59, a60, a61, a62, a63, a64, a65, a66, a67, a68, a69, a70, a71, a72, a73, a74, a75, a76, a77, a78, a79, a80, a81, a82, a83, a84, a85, a86, a87, a88, a89, a90, a91, a92, a93, a94, a95, a96, a97, a98, a99, a100, a101, a102, a103, a104, a105, a106, a107, a108, a109, a110, a111, a112, a113, a114, a115, a116, a117, a118, a119, a120, a121, a122, a123, a124, a125, a126, a127, a128, a129, a130, a131, a132, a133, a134, a135, a136, a137, a138, a139, a140, a141, a142, a143, a144, a145, a146, a147, a148, a149, a150, a151, a152, a153, a154, a155, a156, a157, a158, a159, a160, a161, a162, a163, a164, a165, a166, a167, a168, a169, a170, a171, a172, a173, a174, a175, a176, a177, a178, a179, a180, a181, a182, a183, a184, a185, a186, a187, a188, a189, a190, a191, a192, a193, a194, a195, a196, a197, a198, a199, a200, a201, a202, a203, a204, a205, a206, a207, a208, a209, a210, a211, a212, a213, a214, a215, a216, a217, a218, a219, a220, a221, a222, a223, a224, a225, a226, a227, a228, a229, a230, a231, a232, a233, a234, a235, a236, a237, a238, a239, a240, a241, a242, a243, a244, a245, a246, a247, a248, a249, a250, a251, a252, a253, a254, a255, a256 = " "
}