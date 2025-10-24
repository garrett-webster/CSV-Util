package CSV

sealed trait RowStatus {val text: String}
case object Header extends RowStatus {val text = "Header"}
case object Okay extends RowStatus {val text = "Okay"}
case object Empty extends RowStatus {val text = "Empty"}
case object TooManyRows extends RowStatus {val text = "TooManyRows"}
case object TooFewRows extends RowStatus {val text = "TooFewRows"}

case class Row(text: String, numColumns: Option[Int] = None){
  def entries: List[String] = text.split(",").toList

  val status: RowStatus = numColumns match {
    case None => Header
    case Some(0) => Empty
    case Some(_) => calculateStatus()
  }

  private def hasTooManyEntries: Option[RowStatus] =
    if (entries.length > numColumns.getOrElse(0)) Some(TooManyRows) else None

  private def hasTooFewEntries: Option[RowStatus] =
    if (entries.length < numColumns.getOrElse(0)) Some(TooFewRows) else None

  private def calculateStatus(): RowStatus = {
    hasTooManyEntries
      .orElse(hasTooFewEntries)
      .getOrElse(Okay)
  }
}