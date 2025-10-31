import scala.util.{Failure, Success, Try, Using}

object CSVReader {
  def main(cmdLineArgs: Array[String]): Unit = {
    Arguments(cmdLineArgs) match {
      case Success(command) => command.run()
      case Failure(exception) => println(s"Could not parse arguments: ${exception.getMessage}")
    }
  }
}


