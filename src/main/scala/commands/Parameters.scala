package commands

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

case class Parameters(
                               args: Seq[String],
                               options: Map[String, Option[String]],
                               globalOptions: Map[String, Option[String]],
                               flags: Seq[String],
                               globalFlags: Seq[String],
                               values: Array[String],
                               warnings: Seq[String]
                     )

object Parameters {
  private val globalFlags: Array[String] = Array()

  private val globalOptions: Array[String] = Array(
    "+d" // Delimiter
  )

  def apply(args: Array[String]): Try[Parameters] = {
    extractParameters(args)
  }

  private def extractParameters(args: Array[String]): Try[Parameters] = {
    argsToParameters(Success(
      Parameters(args, Map.empty, Map.empty, Seq.empty, Seq.empty, Array.empty, Seq.empty)
    ))
  }

  @tailrec
  private def argsToParameters(extracted: Try[Parameters]): Try[Parameters] = {
    extracted match {
      case Failure(_) => extracted
      case Success(input) => input.args.headOption match {
        case None => extracted
        case Some(nextArg) if nextArg.startsWith("+") && globalOptions.contains(nextArg) =>
          argsToParameters(extractGlobalOption(nextArg, input))
        case Some(nextArg) if nextArg.startsWith("+") => argsToParameters(extractOption(nextArg, input))
        case Some(nextArg) if nextArg.startsWith("-") && globalOptions.contains(nextArg) =>
          argsToParameters(extractGlobalFlag(nextArg, input))
        case Some(nextArg) if nextArg.startsWith("-") => argsToParameters(extractFlag(nextArg, input))
        case Some(nextArg)                            => argsToParameters(extractValue(nextArg, input))
      }
    }
  }

  private def extractOption(nextArg: String, input: Parameters): Try[Parameters] = {
    input.args.lift(1) match {
      case Some(value) if !value.startsWith("-") && !value.startsWith("+") => Success(
        input.copy(
          options = input.options.updated(nextArg, Some(value)),
          args = input.args.drop(2)
        ))
      case _ =>
        Failure(new IllegalArgumentException(s"No value passed for $nextArg option"))
    }
  }

  private def extractGlobalOption(nextArg: String, input: Parameters): Try[Parameters] = {
    input.args.lift(1) match {
      case Some(value) =>
        val updatedWarnings =
          if ((value.startsWith("-") || value.startsWith("+")) && nextArg != "+d")
            input.warnings :+ s"option $nextArg was passed a value that could be an option or flag"
          else input.warnings
        Success(
          input.copy(
            globalOptions = input.globalOptions.updated(nextArg, Some(value)),
            args = input.args.drop(2),
            warnings = updatedWarnings
          )
        )
      case _ =>
        Failure(new IllegalArgumentException(s"No value passed for $nextArg option"))
    }
  }

  private def extractFlag(nextArg: String, input:  Parameters): Try[Parameters] = {
    Success(input.copy(flags=input.flags :+ nextArg, args = input.args.drop(1)))
  }

  private def extractGlobalFlag(nextArg: String, input:  Parameters): Try[Parameters] = {
    Success(input.copy(globalFlags=input.globalFlags :+ nextArg, args = input.args.drop(1)))
  }

  private def extractValue(nextArg: String, input:  Parameters): Try[Parameters] = {
    Success(input.copy(values = input.values :+ nextArg, args = input.args.drop(1)))
  }
}