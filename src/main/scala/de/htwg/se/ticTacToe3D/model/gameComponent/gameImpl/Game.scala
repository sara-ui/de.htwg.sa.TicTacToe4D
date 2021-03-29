package de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl

import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface

case class Game(grids: Vector[Grid], players: Vector[Player]) extends GameInterface {
  def this(player1: Player, player2: Player) = {
    this(Vector.tabulate(4){ _ => new Grid()}, Vector(player1, player2))
  }
  def this(player1: String, player2: String, symbol1: String, symbol2: String) = {
    this(Player(player1, symbol1), Player(player2, symbol2))
  }
  def this () {
    this("", "", "", "")
  }

  def playersToString: String = players.map(p => p.toString).mkString("\n")

  def set (row:Int, col:Int, grid: Int, playerIndex: Int): Game = copy(grids.updated(grid, grids(grid).set(row, col, players(playerIndex).symbol)))

  def setPlayers (player1: String, player2: String, symbol1: String, symbol2: String): Game =
    copy(grids, Vector(Player(player1, symbol1), Player(player2, symbol2)))

  def resetCell (row:Int, col:Int, grid: Int): Game = copy(grids.updated(grid, grids(grid).set(row, col, "")))

  def cellIsSet (row: Int, column: Int, grid: Int): Boolean =  grids(grid).cellIsSet(row, column)

  def customToString: String = {
    var res = ""
    for {
      i <- grids.indices
    } {
      res += grids(i).customToString(i)
    }
    res
  }
}
