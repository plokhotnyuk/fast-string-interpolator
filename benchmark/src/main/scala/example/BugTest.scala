package example

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

class BugTest {
  def sample(message: () => String): LogRecord = macro BugTest.sample

  def example(message: => String): LogRecord = macro BugTest.example
}

@compileTimeOnly("Enable macros to expand")
object BugTest {
  def sample(c: blackbox.Context)(message: c.Tree): c.Tree = {
    import c.universe._

    q"example.LogRecord($message)"
  }

  def example(c: blackbox.Context)(message: c.Tree): c.Tree = {
    import c.universe._

    q"example.LogRecord(() => $message)"
  }
}

case class LogRecord(messageFunction: () => String) {
  lazy val message: String = messageFunction()
}