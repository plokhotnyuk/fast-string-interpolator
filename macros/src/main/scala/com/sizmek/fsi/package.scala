package com.sizmek

import scala.StringContext._
import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.util.Try

package object fsi {

  /** Implicit class that introduces `fs` and `fraw` string interpolators which
    * are high-performance 100% compatible drop-in replacement of simple & raw string interpolators
    * (s"" or raw"" literals).
    *
    * Here's an example of usage:
    * {{{
    * val host = "sizmek.com"
    * val path = "blog"
    * println(fs"http://$host/$path")
    * println(fraw"http://$host/$path")
    * }}}
    * It will print 2 strings:
    * {{{
    * http://sizmek.com/blog
    * http://sizmek.com/blog
    * }}}
    *
    * Let we have defined functions: `def f(): Int` and `def g(): Double`, then in compile-time
    * for `fs"a${f()}bb${g()}"` the following code will be generated:
    * {{{
    * {
    *   val fresh$macro$1: Int = f();
    *   val fresh$macro$2: Double = g();
    *   com.sizmek.fsi.`package`.stringBuilder().append('a').append(fresh$macro$1).append("bb").append(fresh$macro$2).toString()
    * }: String
    * }}}
    */
  implicit class FastStringInterpolator(val sc: StringContext) extends AnyVal {

    /** A fast version of the simple string interpolator (s"" literal).
      *
      * It inserts its arguments between corresponding parts of the string context.
      * It also treats standard escape sequences as defined in the Scala specification.
      *
      * If a `parts` string contains a backslash (`\`) character that does not start
      * a valid escape sequence, then compilation error will be reported.
      *
      * @param `args` The arguments to be inserted into the resulting string.
      */
    def fs(args: Any*): String = macro Impl.fs

    /** A fast version of the raw string interpolator.
      *
      * It inserts its arguments between corresponding parts of the string context.
      * As opposed to the `fs` string interpolator, this one does not treat
      * standard escape sequences as defined in the Scala specification.
      *
      * @param `args` The arguments to be inserted into the resulting string.
      */
    def fraw(args: Any*): String = macro Impl.fraw
  }

  /** A method to access the thread-local pool for cached string builder instances.
    * It is used internally in generated code only.
    *
    * @return a cached instance of `java.lang.StringBuilder`
    */
  private[fsi] def stringBuilder(): java.lang.StringBuilder = pool.get()

  private object Impl {
    def fs(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[String] = fx(c)(args: _*)(treatEscapes)

    def fraw(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[String] = fx(c)(args: _*)(identity)

    private[this] def fx(c: blackbox.Context)(args: c.Expr[Any]*)(process: String => String): c.Expr[String] = {
      import c.universe._

      val constants = (c.prefix.tree match {
        case Apply(_, List(Apply(_, literals))) => literals
      }).map { case Literal(Constant(s: String)) =>
        try process(s) catch {
          case ex: InvalidEscapeException => c.abort(c.enclosingPosition, ex.getMessage)
        }
      }

      if (args.isEmpty) c.Expr(Literal(Constant(constants.mkString)))
      else {
        val (valDeclarations, values) = args.map { arg =>
          val name = TermName(c.freshName())
          val tpe = if (arg.tree.tpe <:< definitions.NullTpe) typeOf[String] else arg.tree.tpe
          (q"val $name: $tpe = $arg", Ident(name))
        }.unzip

        val stringBuilderWithAppends = constants.zipAll(values, "", null)
          .foldLeft(q"com.sizmek.fsi.stringBuilder()") { case (sb, (s, v)) =>
            val len = s.length
            if (len == 0) {
              if (v == null) sb
              else q"$sb.append($v)"
            } else if (len == 1) {
              if (v == null) q"$sb.append(${s.charAt(0)})"
              else q"$sb.append(${s.charAt(0)}).append($v)"
            } else {
              if (v == null) q"$sb.append($s)"
              else q"$sb.append($s).append($v)"
            }
          }

        c.Expr(c.typecheck(q"..$valDeclarations; $stringBuilderWithAppends.toString"))
      }
    }
  }

  private[this] final val pool = new ThreadLocal[java.lang.StringBuilder] {
    override def initialValue(): java.lang.StringBuilder = new java.lang.StringBuilder(size)

    override def get(): java.lang.StringBuilder = {
      var sb = super.get()
      if (sb.capacity > size) {
        sb = initialValue()
        set(sb)
      } else sb.setLength(0)
      sb
    }
  }

  private[this] final val size = Try(sys.props.getOrElse("com.sizmek.fsi.buffer.size", "").toInt).getOrElse(16384)
}
