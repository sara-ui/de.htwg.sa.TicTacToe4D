package de.htwg.se.ticTacToe3D.model.gameComponent

import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.{Cell, Game, Grid, Player}

trait GameInterface {
  def grids: Vector[Grid]
  def players: Vector[Player]
  def playersToString: String
  def setPlayers(player1: String, player2: String, symbol1: String, symbol2: String): Game
  def set (row:Int, col:Int, grid: Int, playerIndex: Int): Game
  def resetCell (row:Int, col:Int, grid: Int): Game
  def cellIsSet (row: Int, column: Int, grid: Int): Boolean
  def customToString: String
}
trait GridInterface {
  def cells: Vector[Vector[Cell]]
  def set(row:Int, col:Int, value:String): GridInterface
  def size: Int
  def cell(row: Int, col: Int): CellInterface
  def cellIsSet(row: Int, col: Int): Boolean
  def customToString(grid: Int): String
}

trait CellInterface {
  def isSet: Boolean
  def toString: String
  def value: String
}
trait PlayerInterface {
  def name: String
  def symbol: String
  def toString:String
}