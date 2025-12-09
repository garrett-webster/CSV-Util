package commands

import CSV.Csv
import CSV.OutputUtils.writeCsv
import os.Path

class TrimCommand(csv: Csv, parameters: Parameters) extends Command(csv, parameters) {
    val outPath: Path = parameters.options.get("+o")
        .flatten.map(pathStr => os
        .Path(pathStr, os.pwd))
        .getOrElse(os.Path(csv.path, os.pwd))

  override def apply(): Unit = {
    val trimmedCsv = csv.withoutErrorRows
    val flagProcessedCsv = if (parameters.flags.contains("-d")) trimmedCsv.mapRows(_.distinct) else trimmedCsv
    writeCsv(flagProcessedCsv, outPath)
  }
}

object TrimCommand extends CommandObject {
  override val flags: Array[String] = Array("-d")
  override val optionMap: Map[String, Boolean] = Map("+o" -> false)
  override val nonFlagParamCount: Int = 0
  override val usage: String = "-t <path to CSV to trim> \noptions:\n " +
      "-o <path to copy to>: If left unspecified, the trimmed csv will overwrite the original csv" +
      "-d: removes duplicate rows"
  override val commandConstructor: (Csv, Parameters) => TrimCommand =
    (csv: Csv, parameters: Parameters) => new TrimCommand(csv, parameters)
}