package commands

case class Parameters(
                               args: Seq[String],
                               options: Map[String, Option[String]],
                               flags: Seq[String],
                               values: Array[String]
                     )
