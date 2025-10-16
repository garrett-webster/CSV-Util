import OutputUtils.{printDescribeOutput, printUsage, writeCsv}

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}


object CSVReader {
  private type Handler = Array[String] => Unit

  private val optionToFunction: Map[String, Handler] = Map(
    "-d" -> ((args: Array[String]) => isValidArgLength(describeHandler, 1)(args)),
    "-c" -> ((args: Array[String]) => isValidArgLength(copyCsvHandler, 2)(args)),
    "-t" -> ((args: Array[String]) => isValidArgLength(trimCsvHandler, 1, 2)(args))
  )

  def main(args: Array[String]): Unit = {
    if (args.length == 0) printUsage()
    else {
      val option = args(0).toLowerCase()
      option match {
        case "-d" | "-describe" | "-c" | "-copy" | "-t" | "-trim" =>
          optionToFunction(option)(args.tail)
        case _ => printUsage()
      }
    }
  }

  private def isValidArgLength(function: Handler, validArgCounts: Int*)(args: Array[String]): Unit = {
    if (validArgCounts.contains(args.length)) {
      function(args)
    } else {
        println(s"Invalid number of arguments. ${args(0)} takes ${numArgumentsString(validArgCounts)} argument(s)")
    }
  }

  private def parseCsv(path: String): Try[Csv] = {
    Using(Source.fromFile(path)) {
      source => Csv(source.getLines().toList)
    }
  }


  private def describeHandler(args: Array[String]): Unit = {
    parseCsv(args(0)) match {
      case Success(csv) => printDescribeOutput(csv)
      case Failure(_) => println(s"Parse error: ${args(0)} is not a valid path")
    }
  }

  private def copyCsvHandler(args: Array[String]):Unit = {
    parseCsv(args(0)) match {
      case Success(csv) => copyCsv(csv, args(1))
      case Failure(_) => println(s"Parse error: ${args(0)} is not a valid path")
    }
  }

  private def trimCsvHandler(args: Array[String]): Unit = {
    val outPath = args.lift(1) match {
      case Some(pathStr) => os.Path(pathStr, os.pwd)
      case None => os.Path(args(0), os.pwd)
    }

    parseCsv(args(0)) match {
      case Success(csv) => writeCsv(csv.withoutErrorRows, outPath)
      case Failure(_) => println(s"Parse error: ${args(0)} is not a valid path")
    }

  }

  @tailrec
  private def numArgumentsString(validArgCounts: Seq[Int], string: String = ""): String = {
    validArgCounts.length match {
      case 0 => string //I don't think this is needed... But not bad to have for future proofing.
      case 1 =>
        if (string.isEmpty) validArgCounts.head.toString
        else s"$string or ${validArgCounts.head}"
      case _ =>
        val next =
          if (string.isEmpty) validArgCounts.head.toString
          else s"$string, ${validArgCounts.head}"
        numArgumentsString(validArgCounts.tail, next)
    }
  }

  private def copyCsv(csv: Csv, pathStr: String): Unit = {
      writeCsv(csv, os.Path(pathStr, os.pwd))
  }
}