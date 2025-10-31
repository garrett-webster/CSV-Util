package commands

import CSV.Csv

import scala.util.{Failure, Success, Try, Using}

abstract class Command(csv: Csv, parameters: Parameters) {
  def run(): Unit
}

trait CommandObject {
  val flags: Array[String] // Defines the flags that a command can take
  val optionMap: Map[String, Boolean] // Defines the options that a command can have and if it is optional
  val nonFlagParamCount: Int
  val usage: String
  val commandConstructor: (Csv, Parameters) => Command

  def apply(csv: Csv, parameters: Parameters): Try[Command] = {
    validateParameters(parameters) match {
      case Success(validatedParameters) => Success(commandConstructor(csv, validatedParameters))
      case Failure(exception) => Failure(exception)
    }
  }

  def validateParameters(parameters: Parameters): Try[Parameters] = {
    for (flag <- parameters.flags) {
      if (!flags.contains(flag)) {
        return Failure(new IllegalArgumentException(s"Flag $flag not recognized"))
      }
    }

    // TODO: Validate that all required options are present
    for (option <- parameters.options.keys) {
      if (!optionMap.contains(option)) {
        return Failure(new IllegalArgumentException(s"Option $option not recognized"))
      }
    }

    if (parameters.values.length != nonFlagParamCount) {
      return Failure(new IllegalArgumentException(s"Expected $nonFlagParamCount non-flag parameters, but found ${parameters.values.length}"))
    }

    Success(parameters)
  }
}