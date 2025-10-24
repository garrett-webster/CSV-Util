package CSV

case class Csv(lines: List[String]) {
//  Question: Should case classes be primarily vals? Is it like a java record, or should there be lots of methods? How do you think about case classes?
  val headers: List[String] = lines.head.split(",").toList
  val numHeaders: Int = headers.length
  val rows: List[Row] = for (row <- lines.tail) yield Row(row, Some(numHeaders))
  val numRows: Int = rows.length
  val rowsWithErrorStatus: List[Row] = rows.filter(row => row.status != Okay && row.status != Header)
  private val rowsWithoutErrorStatus: List[Row] = rows.filter(row => row.status == Okay || row.status == Header)

//  Question: Should this be a lazy val or a method?
  lazy val withoutErrorRows: Csv = Csv(headers.mkString(",") :: rowsWithoutErrorStatus.map(_.text))
}