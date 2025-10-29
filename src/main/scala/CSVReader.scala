import commands.{CommandFactory, Parameters}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try, Using}

object CSVReader {
  def main(args: Array[String]): Unit = {
    CommandFactory(args) match {
      case Success(command) =>
        command.run()
      case Failure(e) => println(e)
    }
  }

  def extractParameters(args: Array[String]): Try[Parameters] = {
    argsToParameters(Success(
      Parameters(Seq.empty, Map.empty, Seq.empty, args)
    ))
  }

  @tailrec
  private def argsToParameters(extracted: Try[Parameters]): Try[Parameters] = {
    extracted match {
      case Failure(_) => extracted
      case Success(input) => input.args.headOption match {
        case None => extracted
        case Some(nextArg) if nextArg.startsWith("+") => argsToParameters(extractOption(nextArg, input))
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
        Failure(new IllegalArgumentException(s"No value passed for $nextArg flag"))
    }
  }

  private def extractFlag(nextArg: String, input:  Parameters): Try[Parameters] = {
    Success(input.copy(flags=input.flags :+ nextArg, args = input.args.drop(1)))
  }

  private def extractValue(nextArg: String, input:  Parameters): Try[Parameters] = {
    Success(input.copy(values = input.values :+ nextArg, args = input.args.drop(1)))
  }
}


