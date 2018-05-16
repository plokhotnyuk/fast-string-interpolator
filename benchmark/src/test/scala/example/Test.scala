package example

object Test {
  def main(args: Array[String]): Unit = {
    val bt = new BugTest
    val d = 12.3456
    val record = bt.example(f"Value: $d")
    println(s"Record: ${record.message}")
  }
}