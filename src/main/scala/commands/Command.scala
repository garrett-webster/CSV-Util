package commands

import CSV.Csv
import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

abstract class Command {
  def run(): Unit
  def parseCsv(path: String): Try[Csv] = {
    Using(Source.fromFile(path)) {
      source => Csv(source.getLines().toList)
    }
  }
}

object CommandFactory {
  private val flagToCommand: Map[String, Array[String] => Try[Command]] = Map(
    "-d" -> DescribeCommand.apply,
    "-c" -> CopyCommand.apply,
    "-t" -> TrimCommand.apply
  )
  def apply(args: Array[String]): Try[Command] = {
    flagToCommand.get(args(0).toLowerCase()) match {
      case Some(factory) => factory(args.tail)
      case None => Failure(new IllegalArgumentException(s"Unknown command: ${args(0)}"))
    }
  }
}

trait CommandObject {
  val flagMap: Map[String, Boolean] // Defines the flags that a command can have and whether that flag takes a value
  val nonFlagParamCount: Int
  val usage: String

  def apply(args: Array[String]): Try[Command]


  private case class ArgsToParamsIntermediate(
                                      args: Array[String],
                                      extractedFlags: Map[String, Option[String]],
                                      nonFlagParams: Array[String],
                                      error: Try[Unit])

  def convertArgsToParameters(args: Array[String]): Try[Parameters] = {
    val intermediate = argsToParameters(ArgsToParamsIntermediate(args, Map.empty, Array.empty, Success(())))

    for {
      _ <- intermediate.error
      _ <- validateFlags(flagMap, intermediate.extractedFlags)
      _ <- validateNumParams(nonFlagParamCount, intermediate.nonFlagParams.length)
    } yield Parameters(intermediate.extractedFlags, intermediate.nonFlagParams)
  }

  private def validateNumParams(expected: Int, actual: Int): Try[Unit] = {
    if (expected == actual) Success(())
    else Failure(new IllegalArgumentException(s"Wrong number of arguments. Usage: $usage"))
  }

  private def validateFlags(flagMap: Map[String, Boolean], extracted: Map[String, Option[String]]): Try[Unit] = {
    val unknown = extracted.keySet.diff(flagMap.keySet)

    lazy val invalid = extracted.collect {
      case (flag, None) if flagMap(flag) => s"$flag requires a value"
      case (flag, Some(_)) if !flagMap(flag) => s"$flag should not take a value"
    }.toSeq

    if (unknown.nonEmpty)
      Failure(new IllegalArgumentException(s"Unknown flags: ${unknown.mkString(", ")}"))
    else if (invalid.nonEmpty)
      Failure(new IllegalArgumentException(invalid.mkString("; ")))
    else
      Success(())
  }

  // QUESTION: How should this be written? This feels dense and doesn't feel very self documenting
  @tailrec
  private def argsToParameters(input: ArgsToParamsIntermediate): ArgsToParamsIntermediate = {
    input.error match {
      case Failure(_) => input
      case _ => input.args.headOption match {
        case None => input
        case Some(nextArg) if nextArg.startsWith("-") && !flagMap.contains(nextArg) =>
          input.copy(error = Failure(new IllegalArgumentException(s"No such flag $nextArg")))
        case Some(nextArg) if nextArg.startsWith("-") && flagMap.get(nextArg).contains(true) =>
          argsToParameters(getFlagThatTakesValue(input))
        case Some(nextArg) if nextArg.startsWith("-") && flagMap.get(nextArg).contains(false) =>
          argsToParameters(getFlagThatDoesNotTakeValue(input))
        case _ => argsToParameters(getNextParam(input))
      }
    }
  }

  private def getFlagThatTakesValue(input: ArgsToParamsIntermediate): ArgsToParamsIntermediate = {
    val flag = input.args.head

    if (input.extractedFlags.contains(flag)) {
      input.copy(error = Failure(new IllegalArgumentException(s"$flag flag set multiple times")))
    } else {
      input.args.lift(1) match {
        case None =>
          input.copy(error = Failure(new IllegalArgumentException(s"No value passed for $flag flag")))
        case Some(value) if value.startsWith("-") =>
          input.copy(error = Failure(new IllegalArgumentException(s"No value passed for $flag flag")))
        case Some(value) => input.copy(
          extractedFlags = input.extractedFlags.updated(flag, Some(value)),
          args = input.args.drop(2)
        )
      }
    }
  }

  private def getFlagThatDoesNotTakeValue(input: ArgsToParamsIntermediate): ArgsToParamsIntermediate = {
    val flag = input.args.head
    input.copy(extractedFlags = input.extractedFlags.updated(flag, None), args = input.args.drop(1))
  }

  private def getNextParam(input: ArgsToParamsIntermediate): ArgsToParamsIntermediate = {
    val nextParam = input.args.head
    input.copy(nonFlagParams = input.nonFlagParams :+ nextParam, args = input.args.drop(1))
  }
}