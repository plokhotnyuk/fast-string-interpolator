package specs

import com.sizmek.fsi._
import org.scalatest.{Matchers, WordSpec}
import scribe.Logging

class Issue8Spec extends WordSpec with Matchers with Logging {
  "java.util.NoSuchElementException: value d (Issue 8)" should {
    "be reproduced" in {
      val d = 12.3456
      logger.info(fs"Value: $d")
    }
  }
}
