package de.htwg.se.ticTacToe3D.model

case class FourDGridsStateStrategy(verticalColumn: Array[Array[Int]] =
                                   Array.tabulate(4, 4)((row, col) => 0),
                              diagOfAllGrids: List[Array[Array[Int]]] =
                              List.fill(4)(Array.tabulate(4, 4) { (row, col) => 0}))
  extends WinStateStrategyTemplate {

  def checkCell(dir: String)(row: Int, column: Int, i : Int, j: Int, k : Int): Boolean = {
    var length = j
    var reverseLength = k
    if (numberToWin == length || numberToWin == reverseLength || this.verticalColumn(row)(column) == numberToWin) return true
    if (i == this.diagOfAllGrids.length) return false

    length = setGridScore(dir)(row, column, i, length, reverseLength)._1
    reverseLength = setGridScore(dir)(row, column, i , length, reverseLength)._2

    checkCell(dir)(row, column, i + 1, length, reverseLength)
  }

  def setGridScore(dir: String)(row: Int, column: Int, i : Int ,  j: Int, k : Int): (Int, Int) = {
    var length = j
    var reverseLength = k
    dir match {
      case "row" =>
        length += diagOfAllGrids(i)(row)(i)
        reverseLength += diagOfAllGrids(i)(row)(3 - i)
      case "col" =>
        length += diagOfAllGrids(i)(i)(column)
        reverseLength += diagOfAllGrids(i)(3 - i)(column)
      case "diagonal" =>
        length += diagOfAllGrids(i)(i)(i)
        reverseLength += diagOfAllGrids(i)(3 - i)(3 - i)
      case "diagonalReverse" =>
        length += diagOfAllGrids(i)(3 - i)(i)
        reverseLength += diagOfAllGrids(i)(i)(3 - i)
      case _ => println("ERROR")
    }
    (length, reverseLength)
  }

  override def checkRow(row: Int, grid: Int): Boolean = {
    this.checkCell("row")(row, 0, 0, 0, 0)
  }

  override def checkColumn(row: Int, column: Int, grid: Int): Boolean = {
    this.verticalColumn(row)(column) += 1
    this.checkCell("col")(row, column, 0, 0, 0)
  }

  def checkDiagOfAllGridsTogetherColumnEqualRow: Boolean = {
    this.checkCell("diagonal")(0, 0, 0, 0, 0)
  }

  def checkDiagOfAllGridsTogetherInOtherSideOfDiagOneGrid: Boolean = {
    this.checkCell("diagonalReverse")(0, 0, 0, 0, 0)
  }

  override def checkDiagonal(row: Int, column: Int, grid: Int): Boolean = {
    this.diagOfAllGrids(grid)(row)(column) += 1
    if (row == column) {
      return checkDiagOfAllGridsTogetherColumnEqualRow
    } else if (row + column == 3) {
      return checkDiagOfAllGridsTogetherInOtherSideOfDiagOneGrid
    }
    false
  }
}
