package commands

import CSV.OutputUtils.printDescribeOutput

import scala.util.{Failure, Success, Try}

class DescribeCommand(parameters: Parameters) extends Command {
  val path: String = parameters.params(0)
  override def run(): Unit = {
      parseCsv(path) match {
        case Success(csv) => printDescribeOutput(csv)
        case _ => println(s"Unspecified error describing $path")
      }
  }
}

object DescribeCommand extends CommandObject {
  override val flagMap: Map[String, Boolean] = Map.empty
  override val nonFlagParamCount: Int = 1
  override val usage = "-d <path to CSV>"

  def apply(args: Array[String]): Try[DescribeCommand] = {
    convertArgsToParameters(args) match {
      case Failure(exception) => Failure(exception)
      case Success(parameters) => Success(new DescribeCommand(parameters))
    }
  }
}