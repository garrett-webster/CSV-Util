import OutputUtils.{printDescribeOutput, writeCsv}

import scala.io.Source
import scala.util.Using

object CSVReader {
  def main(args: Array[String]): Unit = {
    val option: String = args(0).toLowerCase()
    val inputPath: String = args(1)

    // I want to make a "copy good rows" command that will output a new csv to the given path. That will take some work because Source does not support writing files.
    val outputPath: Option[String] = args.lift(2)

    Using(Source.fromFile(inputPath)) { source =>
      val csv = Csv(source.getLines().toList)
      option match {
        case "-d" | "-describe" => printDescribeOutput(csv)
        case "-c" | "-copy" => copyCsv(csv, outputPath)
      }
    }
  }

  private def copyCsv(csv: Csv, pathStr: Option[String]): Unit = {
    pathStr match {
      case Some(path) => writeCsv(csv, os.Path(path, os.pwd))
      case None => println("Usage: -c <source file path> <output file path>")
    }
  }
}