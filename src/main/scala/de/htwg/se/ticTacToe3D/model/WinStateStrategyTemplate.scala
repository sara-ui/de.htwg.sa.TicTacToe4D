package de.htwg.se.ticTacToe3D.model

import de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl.Messages

import scala.util.{Failure, Success, Try}

trait WinStateStrategyTemplate {
  val numberToWin: Int = 4
  def checkRow(row:Int, grid: Int): Boolean
  def checkColumn(row: Int, column:Int, grid: Int): Boolean
  def checkDiagonal(row:Int, column:Int, grid: Int): Boolean
  def checkForWin(row:Int, column:Int, grid: Int): Try[Boolean] = {
    if (checkDiagonal(row, column, grid) || checkRow(row, grid) || checkColumn(row, column, grid)) {
      return Success(true)
    }
    Failure(new Exception(Messages.WIN_ERROR))
  }
}
