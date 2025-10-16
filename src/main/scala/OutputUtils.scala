object OutputUtils {
  import os._
  
  def printHeaders(csv: Csv): Unit = {
    for (header <- csv.headers)
      print(header)
    print('\n')
  }

  def printDescribeOutput(csv: Csv): Unit = {
    val CHARACTERSBEFOREPIPE = 12
    println(s"Number of headers: ${csv.numHeaders}")
    println(s"Number of rows:    ${csv.numRows}")

    if (csv.rowsWithErrorStatus.nonEmpty) {
      println(s"\nNumber of bad lines:  ${csv.rowsWithErrorStatus.length}")
      println(s"Number of good lines: ${csv.numRows - csv.rowsWithErrorStatus.length} \n")
      println("Bad rows")

      val errorCodeLabel = "Error Code"
      println(s"$errorCodeLabel${" " * (CHARACTERSBEFOREPIPE - errorCodeLabel.length)}| Row Text")
      for (row <- csv.rowsWithErrorStatus) {
        val spaces = " " * (CHARACTERSBEFOREPIPE - row.status.text.length)
        println(s"${row.status}$spaces| ${row.text}")
      }
    }
  }

  def writeCsv(csv:Csv, path: os.Path): Unit = {
    os.write.over(path, "")

  }
}
