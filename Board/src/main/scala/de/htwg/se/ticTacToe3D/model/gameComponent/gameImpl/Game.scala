package de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl

import java.io.{File, PrintWriter}

import de.htwg.se.ticTacToe3D.model.gameComponent.{GameInterface, PlayerInterface}
import play.api.libs.json.{JsValue, Json}

case class Game(grids: Vector[Grid], players: Vector[Player]) extends GameInterface {
  def this(player1: Player, player2: Player) = {
    this(Vector.tabulate(4) { _ => new Grid() }, Vector(player1, player2))
  }

  def this(player1: String, player2: String, symbol1: String, symbol2: String) = {
    this(Player(player1, symbol1), Player(player2, symbol2))
  }

  def this() {
    this("", "", "", "")
  }

  def getPlayer(index: Int) : PlayerInterface = this.players(index)

  def playersToString: String = players.map(p => p.toString).mkString("\n")

  def set(row: Int, col: Int, grid: Int, playerIndex: Int): Game = copy(grids.updated(grid, grids(grid).set(row, col, players(playerIndex).symbol)))

  def setPlayers(player1: String, player2: String, symbol1: String, symbol2: String): Game =
    copy(grids, Vector(Player(player1, symbol1), Player(player2, symbol2)))

  def resetCell(row: Int, col: Int, grid: Int): Game = copy(grids.updated(grid, grids(grid).set(row, col, "")))

  def cellIsSet(row: Int, column: Int, grid: Int): Boolean = grids(grid).cellIsSet(row, column)

  def customToString: String = {
    var res = ""
    for {
      i <- grids.indices
    } {
      res += grids(i).customToString(i)
    }
    res
  }

  def board(game: GameInterface, turn: Boolean): Unit = {
    val pw = new PrintWriter(new File("." + File.separator + "board.json"))
    pw.write(Json.prettyPrint(boardToJson(game, turn)))
    pw.close
  }
  def boardToJson(game: GameInterface, turn: Boolean): JsValue = {
    Json.obj(
      "turn" -> turn,
      "players" -> Json.toJson(
        for {
          index <- game.players.indices
        } yield {
          Json.obj(
            "name" -> game.players(index).name,
            "symbol" -> game.players(index).symbol
          )
        }
      ),
      "grids" -> Json.toJson(
        for {
          index <- game.grids.indices
        } yield {
          Json.obj(
            "cells" -> Json.toJson(
              for {
                row <- 0 until game.grids(index).size;
                col <- 0 until game.grids(index).size
              } yield {
                Json.obj(
                  "row" -> row,
                  "col" -> col,
                  "value" -> game.grids(index).cell(row, col).value
                )
              }
            )
          )
        }
      )
    )
  }

  def loadBoardJson(): String = {
    val file = scala.io.Source.fromFile("board.json")
    try file.mkString finally file.close()
  }

}
