package de.htwg.se.ticTacToe3D.model

trait WinStateStrategyTemplate {
  val numberToWin: Int = 4
  def checkRow(row:Int, grid: Int): Boolean
  def checkColumn(row: Int, column:Int, grid: Int): Boolean
  def checkDiagonal(row:Int, column:Int, grid: Int): Boolean
  def checkForWin(row:Int, column:Int, grid: Int): Boolean = checkDiagonal(row, column, grid) || checkRow(row, grid) || checkColumn(row, column, grid)
}
