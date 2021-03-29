package de.htwg.se.ticTacToe3D.model.fileIoComponent.fileIoXmlImpl

import java.io._

import com.google.inject.Guice
import de.htwg.se.ticTacToe3D.TicTacToeModule

import scala.xml.{Node, PrettyPrinter}
import de.htwg.se.ticTacToe3D.model.fileIoComponent.FileIOInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface

class FileIO extends FileIOInterface {
  override def load: (GameInterface, Boolean) = {
    val injector = Guice.createInjector(new TicTacToeModule)
    var game: GameInterface = injector.getInstance(classOf[GameInterface])
    val file = scala.xml.XML.loadFile("." + File.separator + "game.xml")
    val turn: Boolean = (file \\ "turn").text.toBoolean
    val players = (file \\ "player")
    val playerName1 = (players.head \ "@name").text.toString
    val playerName2 = (players(1) \ "@name").text.toString
    val playerSymbol1 = (players.head \ "@symbol").text.toString
    val playerSymbol2 = (players(1) \ "@symbol").text.toString
    game = game.setPlayers(playerName1, playerName2, playerSymbol1, playerSymbol2)
    val grids = (file \\ "grid")
    for (i <- 0 until 4) {
      val grid = grids(i)
      for (index <- 0 until 16) {
        val cellNode = (grid \\ "cell")
        val row = (cellNode(index) \ "@row").text.toInt
        val col = (cellNode(index) \ "@col").text.toInt
        val value = (cellNode(index) \ "@value").text.toString
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

  def gameToXml(game: GameInterface, turn: Boolean): Node = {
    <game>
      <turn>{turn.toString}</turn>
      <players>
        {
        for {
          index <- game.players.indices
        } yield playerToXml(game, index)
        }
      </players>
      <grids>
        {
        for {
          index <- game.grids.indices
        } yield gridToXml(game, index)
        }
      </grids>
    </game>
  }

  def playerToXml (game: GameInterface, index: Int): Node = {
    <player name={game.players(index).name.toString} symbol={game.players(index).symbol.toString}></player>
  }
  def gridToXml(game: GameInterface, i: Int): Node = {
    <grid>
      {
      for {
        row <- 0 until game.grids(i).size
        col <- 0 until game.grids(i).size
      } yield cellToXml(row, col, game.grids(i).cell(row, col).value)
      }
    </grid>
  }
  def cellToXml (row: Int, col: Int, value: String): Node = {
    System.out.println(row + " " + col + " " + value)
    <cell row={row.toString} col={col.toString} value={value.toString}></cell>
  }

  def saveString(game: GameInterface, turn: Boolean): Unit = {
    val pw = new PrintWriter(new File("." + File.separator + "game.xml"))
    val prettyPrinter = new PrettyPrinter(120, 4)
    val xml = prettyPrinter.format(gameToXml(game, turn))
    pw.write(xml)
    pw.close()
  }

  override def save(game: GameInterface, turn: Boolean): Unit = saveString(game, turn)
}
