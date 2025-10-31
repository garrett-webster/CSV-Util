package commands

import CSV.OutputUtils.printDescribeOutput
import CSV.Csv

class DescribeCommand(csv: Csv, parameters: Parameters) extends Command(csv, parameters) {
  override def apply(): Unit = {
    printDescribeOutput(csv)
  }
}

object DescribeCommand extends CommandObject {
  override val flags: Array[String] = Array.empty
  override val optionMap: Map[String, Boolean] = Map.empty
  override val nonFlagParamCount: Int = 0
  override val usage = "-d <path to CSV>"
  override val commandConstructor: (Csv, Parameters) => DescribeCommand =
    (csv: Csv, parameters: Parameters) => new DescribeCommand(csv, parameters)
}