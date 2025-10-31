package commands
import CSV.Csv
import CSV.OutputUtils.writeCsv

class CopyCommand(csv: Csv, parameters: Parameters) extends Command(csv, parameters) {
  private val destinationPath: String = parameters.values(0)

  override def apply(): Unit = {
    writeCsv(csv, os.Path(destinationPath, os.pwd))
  }
}

object CopyCommand extends CommandObject {
  override val flags: Array[String] = Array.empty
  override val optionMap: Map[String, Boolean] = Map.empty
  override val nonFlagParamCount: Int = 1
  override val usage = "-d <path to CSV>"
  override val commandConstructor: (Csv, Parameters) => CopyCommand =
    (csv: Csv, parameters: Parameters) => new CopyCommand(csv, parameters)
}