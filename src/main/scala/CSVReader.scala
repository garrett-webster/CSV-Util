import commands.CommandFactory
import scala.util.{Failure, Success, Try, Using}

object CSVReader {
  def main(args: Array[String]): Unit = {
    CommandFactory(args) match {
      case Success(command) =>
        command.run()
      case Failure(e) => println(e)
    }
  }

}