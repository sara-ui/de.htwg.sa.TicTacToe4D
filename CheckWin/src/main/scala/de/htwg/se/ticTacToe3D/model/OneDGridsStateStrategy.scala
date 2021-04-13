package de.htwg.se.ticTacToe3D.model

case class OneDGridsStateStrategy(rowGridScore: List[Array[Int]] = List.fill(4)(Array(0, 0, 0, 0)),
                                  colsGridScore: List[Array[Int]] = List.fill(4)(Array(0, 0, 0, 0)),
                                  diagGridScore: List[Array[Int]] = List.fill(4)(Array(0, 0)))
  extends WinStateStrategyTemplate {

  def setGridScore(score: List[Array[Int]])(cell: Int, grid: Int): Boolean = {
    score(grid)(cell) += 1
    score(grid)(cell) == numberToWin
  }

  override def checkRow(row: Int, grid: Int): Boolean = this.setGridScore(rowGridScore)(row, grid)

  override def checkColumn(row: Int, column: Int, grid: Int): Boolean = this.setGridScore(colsGridScore)(column, grid)

  def checkCellInOtherSideOfDiagOneGrid(row: Int, column: Int, grid: Int, i: Int): Boolean = {
    if (i == numberToWin) return false
    if (row == i && column == (numberToWin - 1 - i)) {
      return this.setGridScore(diagGridScore)(1, grid)
    }
    checkCellInOtherSideOfDiagOneGrid(row, column, grid, i + 1)
  }

  override def checkDiagonal(row: Int, column: Int, grid: Int): Boolean = {
    if (row == column) {
      return this.setGridScore(diagGridScore)(0, grid)
    }
    checkCellInOtherSideOfDiagOneGrid(row, column, grid, 0)
  }
}
