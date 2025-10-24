package commands

import CSV.OutputUtils.writeCsv

import scala.util.{Failure, Success, Try}

class CopyCommand(parameters: Parameters) extends Command {
  private val srcPath: String = parameters.params(0)
  private val destinationPath = parameters.params(1)

  override def run(): Unit = {
    parseCsv(srcPath) match {
      case Success(csv) => writeCsv(csv, os.Path(destinationPath, os.pwd))
      case _ => println(s"Unspecified error copying $srcPath")
    }
  }
}

object CopyCommand extends CommandObject {
  override val flagMap: Map[String, Boolean] = Map.empty
  override val nonFlagParamCount: Int = 2
  override val usage = "-d <path to CSV> <path to copy to>"

  def apply(args: Array[String]): Try[CopyCommand] = {
    convertArgsToParameters(args) match {
      case Failure(exception) => Failure(exception)
      case Success(parameters) => Success(new CopyCommand(parameters))
    }
  }
}