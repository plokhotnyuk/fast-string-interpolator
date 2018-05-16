package example

import com.sizmek.fsi._

object Test {
  def main(args: Array[String]): Unit = {
    val bt = new BugTest

    def sample(message: => String) = bt.sample(() => message)

    val d = 12.3456
/*
    val record = bt.example(fs"Value: $d")
    println(s"Record: ${record.message}")
*/
    val record2 = sample(fs"Value: $d")
    println(s"Record: ${record2.message}")
  }
}