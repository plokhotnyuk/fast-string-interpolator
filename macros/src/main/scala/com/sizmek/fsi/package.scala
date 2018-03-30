package com.sizmek

import scala.StringContext._
import scala.collection.mutable
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

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
    * Let we have defined functions: `def f(): Int` and `def g(): AnyRef`, then in compile-time
    * for `fs"a${f()}bb${g()}"` the following code will be generated:
    * {{{
    * {
    *   val fresh$macro$1: Int = f();
    *   val fresh$macro$2: String = g().toString;
    *   (new java.lang.StringBuilder(14 + fresh$macro$2.length)).append('a').append(fresh$macro$1).append("bb").append(fresh$macro$2).toString()
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

  private object Impl {
    def fs(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[String] = fx(c)(args: _*)(treatEscapes)

    def fraw(c: blackbox.Context)(args: c.Expr[Any]*): c.Expr[String] = fx(c)(args: _*)(identity)

    private[this] def fx(c: blackbox.Context)(args: c.Expr[Any]*)(process: String => String): c.Expr[String] = {
      import c.universe._

      def isPrimitive(tpe: Type): Boolean = tpe.typeSymbol.isClass && tpe.typeSymbol.asClass.isPrimitive

      val primitiveLengths = Map(
        (definitions.BooleanTpe, 5), // false.toString.length
        (definitions.ByteTpe, 4), // Byte.MinValue.toString.length
        (definitions.CharTpe, 1), // 'x'.toString.length
        (definitions.ShortTpe, 6), // Short.MinValue.toString.length
        (definitions.IntTpe, 11), // Int.MinValue.toString.length
        (definitions.LongTpe, 20), // Long.MinValue.toString.length
        (definitions.FloatTpe, 14), // Float.MinValue.toString.length + 1 (for an exponent sign)
        (definitions.DoubleTpe, 24), // Double.MinValue.toString.length + 1 (for an exponent sign)
        (definitions.UnitTpe, 2)) // ().toString.length
      val constants = (c.prefix.tree match {
        case Apply(_, List(Apply(_, literals))) => literals
      }).map { case Literal(Constant(s: String)) =>
        try process(s) catch {
          case ex: InvalidEscapeException => c.abort(c.enclosingPosition, ex.getMessage)
        }
      }
      if (args.isEmpty) c.Expr(Literal(Constant(constants.mkString)))
      else {
        var constStrLen = 0
        val varStrLenValues = new mutable.ArrayBuffer[TermName]()
        val valueDefinitions = new mutable.ArrayBuffer[Tree]()
        val values = new mutable.ArrayBuffer[Tree]()
        args.foreach { arg =>
          arg.tree match {
            case tree @ Literal(Constant(const)) =>
              values.append(if (tree.tpe <:< definitions.NullTpe) {
                constStrLen += 4 // ("" + null).length
                q"(null: String)"
              } else {
                constStrLen += const.toString.length
                tree
              })
            case tree =>
              val name = TermName(c.freshName())
              val tpe = tree.tpe
              valueDefinitions.append(if (tpe <:< definitions.NullTpe) {
                constStrLen += 4 // ("" + null).length
                q"val $name: String = $arg"
              } else if (isPrimitive(tpe)) {
                constStrLen += primitiveLengths(tpe)
                q"val $name: $tpe = $arg"
              } else {
                varStrLenValues.append(name)
                if (tpe <:< typeOf[CharSequence]) q"val $name: $tpe = $arg"
                else q"val $name: String = $arg.toString"
              })
              values.append(Ident(name))
          }
        }
        val capacity = varStrLenValues.foldLeft(q"$constStrLen")((acc, name) => q"$acc + $name.length")
        val stringBuilderWithAppends = constants.zipAll(values, "", null)
          .foldLeft(q"new java.lang.StringBuilder($capacity)") { case (sb, (constant, value)) =>
            val len = constant.length
            if (len == 0) {
              if (value == null) sb
              else q"$sb.append($value)"
            } else if (len == 1) {
              if (value == null) q"$sb.append(${constant.charAt(0)})"
              else q"$sb.append(${constant.charAt(0)}).append($value)"
            } else {
              if (value == null) q"$sb.append($constant)"
              else q"$sb.append($constant).append($value)"
            }
          }
        c.Expr(c.typecheck(q"..$valueDefinitions; $stringBuilderWithAppends.toString"))
      }
    }
  }
}
