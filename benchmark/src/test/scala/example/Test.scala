package example

object Test {
  def main(args: Array[String]): Unit = {
    val bt = new BugTest

    def sample(message: => String) = bt.sample(() => message)

    val d = 12.3456
    val record = bt.example(f"Value: $d")
    println(s"Record: ${record.message}")
    val record2 = sample(f"Value: $d")
    println(s"Record: ${record2.message}")
  }
}