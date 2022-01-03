package com.github.plokhotnyuk

import scala.StringContext._
import scala.quoted.*
import scala.util.Try

package object fsi {

  extension (inline sc: StringContext)
    inline def fs(inline args: Any*): String = ${ Impl.fs('sc, 'args) }
    inline def fraw(inline args: Any*): String = ${ Impl.fraw('sc, 'args) }

  def stringBuilder(): java.lang.StringBuilder = pool.get()

  private object Impl {
    def fs(sc: Expr[StringContext], args: Expr[Seq[Any]])(using Quotes): Expr[String] = fx(sc, args)(processEscapes)

    def fraw(sc: Expr[StringContext], args: Expr[Seq[Any]])(using Quotes): Expr[String] = fx(sc, args)(identity)

    private[this] def fx(scExpr: Expr[StringContext], argsExpr: Expr[Seq[Any]])(process: String => String)(using Quotes): Expr[String] = {
      import quotes.reflect.{*, given}
      import report.*
      
      val sc: StringContext = scExpr.valueOrAbort
      val constants: Seq[String] = sc.parts.map{ s =>
        try process(s) catch {
          case ex: InvalidEscapeException => errorAndAbort(ex.getMessage)
        }
      }
      val args = argsExpr match
        case Varargs(argExprs) => argExprs
        case _ => errorAndAbort(s"Args must be explicit", argsExpr)

      if args.isEmpty then Expr(constants.mkString)
      else {
        val (valDeclarations, values) =
          args.map{ arg =>
            arg.asTerm match
              case term @ Literal(_: Constant) =>
                (None, Some(term))
              case term =>
                val sym = Symbol.newVal(Symbol.spliceOwner, "x", term.tpe, Flags.EmptyFlags, Symbol.noSymbol)
                val valDef = ValDef(sym, Some(arg.asTerm.changeOwner(sym)))
                val value = Ref(sym)
                (Some(valDef), Some(value))
          }.unzip

        val declarations = valDeclarations.flatten.toList
        val sb: Expr[java.lang.StringBuilder] = '{ com.github.plokhotnyuk.fsi.stringBuilder() }
        val stringBuilderWithAppends = constants.zipAll(values, "", None).foldLeft(sb){ case (sb, (s, v)) => 
          val len = s.length
          if (len == 0) {
              if v.isEmpty then sb
              else '{ ${sb}.append(${v.get.asExpr}) }
          } else if (len == 1) {
              if v.isEmpty then '{ ${sb}.append(${Expr(s.charAt(0))}) }
              else '{ ${sb}.append(${Expr(s.charAt(0))}).append(${v.get.asExpr}) }
          } else {
              if v.isEmpty then '{ ${sb}.append(${Expr(s)}) }
              else '{ ${sb}.append(${Expr(s)}).append(${v.get.asExpr}) }
          }
        }
        if declarations.isEmpty then '{ ${stringBuilderWithAppends}.toString }
        else
          val builder = '{ ${stringBuilderWithAppends}.toString }.asTerm
          Block(declarations, builder).asExpr.asInstanceOf[Expr[String]]
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

  private[this] final val size = Try(sys.props.getOrElse("com.github.plokhotnyuk.fsi.buffer.size", "").toInt).getOrElse(16384)
}
