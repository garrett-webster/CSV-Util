package CSV

case class Csv(path: String, lines: List[String], delimiter: Char = ',') {
//  Question: Should case classes be primarily vals? Is it like a java record, or should there be lots of methods? How do you think about case classes?
  val headers: List[String] = lines.head.split(delimiter).toList
  val numHeaders: Int = headers.length
  val rows: List[Row] = for (row <- lines.tail) yield Row(row, delimiter, Some(numHeaders))
  val numRows: Int = rows.length
  val rowsWithErrorStatus: List[Row] = rows.filter(row => row.status != Okay && row.status != Header)
  private val rowsWithoutErrorStatus: List[Row] = rows.filter(row => row.status == Okay || row.status == Header)

//  Question: Should this be a lazy val or a method?
  lazy val withoutErrorRows: Csv = Csv(path, headers.mkString(delimiter.toString) :: rowsWithoutErrorStatus.map(_.text), delimiter)

  def mapRows(func: List[Row] => List[Row]): Csv = {
   Csv(path, headers.mkString(delimiter.toString) :: func(rowsWithoutErrorStatus).map(_.text), delimiter)
  }
}