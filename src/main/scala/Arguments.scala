import CSV.Csv
import commands.{Command, DescribeCommand, Parameters}

import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

case class Arguments(
  command: (Csv, Parameters) => Try[Command],
  csvPath: String,
  parameters: Parameters
)

object Arguments {
  def apply(cmdLineArgs:Array[String]): Try[Command] = {
    val commandResult: Try[(Csv, Parameters) => Try[Command]] = cmdLineArgs.headOption match {
      case Some("describe") => Success(DescribeCommand.apply)
      case Some(other)      => Failure(new IllegalArgumentException(s"Unknown command: $other"))
      case None             => Failure(new IllegalArgumentException("No command specified"))
    }

    val csvPathResult: Try[String] = cmdLineArgs.drop(1).headOption match {
      case Some(path) => Success(path)
      case None       => Failure(new IllegalArgumentException("No CSV path specified"))
    }

    val parametersResult: Try[Parameters] = Parameters(cmdLineArgs.drop(2))

    def buildCommand(csv: Try[Csv], arguments: Try[Arguments]): Try[Command] = {
      {for {
        c <- csv
        a <- arguments
      } yield a.command(c, a.parameters)} match {
        case Success(command) => command
        case Failure(exception) => Failure(exception)
      }
    }

    val arguments: Try[Arguments] = for {
      cmd    <- commandResult
      path   <- csvPathResult
      params <- parametersResult
    } yield Arguments(cmd, path, params)

    val csv: Try[Csv] = arguments match{
      case Success(args) => parseCsv(args.csvPath)
      case Failure(error) => Failure(error)
    }

    buildCommand(csv, arguments)
  }

  def parseCsv(path: String): Try[Csv] = {
    Using(Source.fromFile(path)) {
      source => Csv(source.getLines().toList)
    }
  }
}
