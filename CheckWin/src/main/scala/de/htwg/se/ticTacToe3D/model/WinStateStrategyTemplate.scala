package de.htwg.se.ticTacToe3D.model

import scala.concurrent.Future

trait WinStateStrategyTemplate {
  val numberToWin: Int = 4

  def checkRow(row: Int, grid: Int): Boolean

  def checkColumn(row: Int, column: Int, grid: Int): Boolean

  def checkDiagonal(row: Int, column: Int, grid: Int): Boolean

  def checkForWin(row:Int, column:Int, grid: Int): Future[Boolean] = {
    if (checkDiagonal(row, column, grid) || checkRow(row, grid) || checkColumn(row, column, grid)) {
      return Future.successful(true)
    }
    Future.failed(new Exception("Not won yet"))
  }
}
