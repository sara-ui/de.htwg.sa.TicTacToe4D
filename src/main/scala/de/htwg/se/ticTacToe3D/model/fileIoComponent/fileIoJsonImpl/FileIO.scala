package de.htwg.se.ticTacToe3D.model.fileIoComponent.fileIoJsonImpl

import java.io._

import com.google.inject.Guice
import de.htwg.se.ticTacToe3D.TicTacToeModule
import de.htwg.se.ticTacToe3D.model.fileIoComponent.FileIOInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface
import play.api.libs.json.{JsValue, Json}

import scala.io.Source

class FileIO  extends FileIOInterface{
  override def load: (GameInterface, Boolean) = {
    val injector = Guice.createInjector(new TicTacToeModule)
    var game: GameInterface = injector.getInstance(classOf[GameInterface])
    val source: String = Source.fromFile("." + File.separator + "game.json").getLines.mkString
    val json: JsValue = Json.parse(source)
    val turn = (json \ "turn").as[Boolean]
    // players
    val players = (json \ "players").get
    val playerName1 = (players \\ "name").head.as[String]
    val playerName2 = (players \\ "name")(1).as[String]
    val playerSymbol1 = (players \\ "symbol").head.as[String]
    val playerSymbol2 = (players \\ "symbol")(1).as[String]
    game = game.setPlayers(playerName1, playerName2, playerSymbol1, playerSymbol2)
    // Grids
    val grids = (json \ "grids").get
    for (i <- 0 until 4) {
      val grid = (grids \\ "cells")(i)
      for (index <- 0 until 16) {
        val row = (grid \\ "row")(index).as[Int]
        val col = (grid \\ "col")(index).as[Int]
        val value = (grid \\ "value")(index).as[String]
        if (!value.equals("")) {
          game = game.set(
            row,
            col,
            i,
            if (value.equals(playerSymbol1)) 0 else 1
          )
        }
      }
    }
    (game,
      turn)
  }

  def gameToJson(game: GameInterface, turn: Boolean): JsValue = {
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

  override def save(game: GameInterface, turn: Boolean): Unit = {
    val pw = new PrintWriter(new File("." + File.separator + "game.json"))
    pw.write(Json.prettyPrint(gameToJson(game, turn)))
    pw.close
  }
}
