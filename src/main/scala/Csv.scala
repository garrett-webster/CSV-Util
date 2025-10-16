case class Csv(lines: List[String]) {
  val headers: List[String] = lines.head.split(",").toList
  val numHeaders: Int = headers.length
  val rows: List[Row] = for (row <- lines.tail) yield Row(row, Some(numHeaders))
  val numRows: Int = rows.length
  val rowsWithErrorStatus: List[Row] = for {
    row <- rows
    if row.status != Okay && row.status != Header
  } yield row
}