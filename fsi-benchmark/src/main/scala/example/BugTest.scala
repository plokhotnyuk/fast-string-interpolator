package example

import scala.annotation.compileTimeOnly
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

class BugTest {
  def example(message: => String): LogRecord = macro BugTest.example
}

@compileTimeOnly("Enable macros to expand")
object BugTest {
  def example(c: blackbox.Context)(message: c.Tree): c.Tree = {
    import c.universe._
    val f = c.typecheck(q"() => $message")
    c.internal.changeOwner(message, c.internal.enclosingOwner, f.symbol)
    q"example.LogRecord($f)"
  }
}

case class LogRecord(messageFunction: () => String) {
  lazy val message: String = messageFunction()
}