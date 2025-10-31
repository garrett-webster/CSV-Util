//package commands
//
//import CSV.OutputUtils.writeCsv
//import os.Path
//
//import scala.util.{Failure, Success, Try}
//
//class TrimCommand(parameters: Parameters) extends Command {
//  val srcPath: String = parameters.params(0)
//
//  val outPath: Path = parameters.flags.get("-o")
//      .flatten.map(pathStr => os
//      .Path(pathStr, os.pwd))
//      .getOrElse(os.Path(srcPath, os.pwd))
//
//  override def run(): Unit = {
//    parseCsv(srcPath) match {
//      case Success(csv) => writeCsv(csv.withoutErrorRows, outPath)
//      case _ => println(s"Unspecified error trimming $srcPath")
//    }
//  }
//}
//
//object TrimCommand extends CommandObject {
//  override val flagMap: Map[String, Boolean] = Map(("-o", true))
//  override val nonFlagParamCount: Int = 1
//  override val usage: String = "-t <path to CSV to trim> \noptional flags:\n " +
//    "-o <path to copy to>: If left unspecified, the trimmed csv will overwrite the original csv"
//
//  override def apply(args: Array[String]): Try[Command] = {
//    convertArgsToParameters(args) match {
//      case Failure(exception) => Failure(exception)
//      case Success(parameters) => Success(new TrimCommand(parameters))
//    }
//  }
//}