package de.htwg.se.ticTacToe3D.model

class OneDGridsStateStrategy(var rowGridScore: List[Array[Int]],
                             var colsGridScore: List[Array[Int]],
                             var diagGridScore: List[Array[Int]])
  extends WinStateStrategyTemplate {

  def this() = {
    this(List.fill(4)(Array(0, 0, 0, 0)),
      List.fill(4)(Array(0, 0, 0, 0)),
      List.fill(4)(Array(0, 0)))
  }
  override def checkRow(row: Int, grid: Int): Boolean = {
    this.rowGridScore(grid)(row) += 1
    this.rowGridScore(grid)(row) == numberToWin
  }

  override def checkColumn(row: Int, column: Int, grid: Int): Boolean = {
    this.colsGridScore(grid)(column) += 1
    this.colsGridScore(grid)(column) == numberToWin
  }

  def checkCellInOtherSideOfDiagOneGrid(row: Int, column: Int, grid: Int): Boolean = {
    for (i <- 0 to numberToWin) {
      if (row == i && column == (numberToWin - 1 - i)) {
        this.diagGridScore(grid)(1) += 1
        return this.diagGridScore(grid)(1) == numberToWin
      }
    }
    false
  }

  override def checkDiagonal(row: Int, column: Int, grid: Int): Boolean = {
    if (row == column) {
      this.diagGridScore(grid)(0) += 1
      return this.diagGridScore(grid)(0) == numberToWin
    }
    checkCellInOtherSideOfDiagOneGrid(row, column, grid)
  }
}
