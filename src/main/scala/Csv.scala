case class Csv(lines: List[String]) {
  val headers: List[String] = lines.head.split(",").toList
  val numHeaders: Int = headers.length
  val rows: List[Row] = for (row <- lines.tail) yield Row(row, Some(numHeaders))
  val numRows: Int = rows.length
}