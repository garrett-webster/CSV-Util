import scala.util.{Failure, Success}

object CSVReader {
  def main(cmdLineArgs: Array[String]): Unit = {
    Arguments(cmdLineArgs) match {
      case Success(command) => command()
      case Failure(exception) => println(s"Could not parse arguments: ${exception.getMessage}")
    }
  }
}


