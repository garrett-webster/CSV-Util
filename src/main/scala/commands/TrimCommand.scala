package commands

import CSV.Csv
import CSV.OutputUtils.writeCsv
import os.Path

class TrimCommand(csv: Csv, parameters: Parameters) extends Command(csv, parameters) {
  private val destinationPath: Option[String] = parameters.options.getOrElse("-o", None)

    val outPath: Path = parameters.options.get("+o")
        .flatten.map(pathStr => os
        .Path(pathStr, os.pwd))
        .getOrElse(os.Path(csv.path, os.pwd))

  override def apply(): Unit = {
    writeCsv(csv.withoutErrorRows, outPath)
  }
}

object TrimCommand extends CommandObject {
  override val flags: Array[String] = Array.empty
  override val optionMap: Map[String, Boolean] = Map("+o" -> false)
  override val nonFlagParamCount: Int = 0
  override val usage: String = "-t <path to CSV to trim> \noptional flags:\n " +
      "-o <path to copy to>: If left unspecified, the trimmed csv will overwrite the original csv"
  override val commandConstructor: (Csv, Parameters) => TrimCommand =
    (csv: Csv, parameters: Parameters) => new TrimCommand(csv, parameters)
}