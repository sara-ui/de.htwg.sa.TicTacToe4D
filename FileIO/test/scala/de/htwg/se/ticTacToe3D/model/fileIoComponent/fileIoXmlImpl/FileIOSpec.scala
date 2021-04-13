package de.htwg.se.ticTacToe3D.model.fileIoComponent.fileIoXmlImpl

import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface
import org.scalatest.{Matchers, WordSpec}

class FileIOSpec extends WordSpec with Matchers {

  "File IO XML" should {

    "load File" in {
      val fileIO = new FileIO()

      val (game: GameInterface, myTurn: Boolean) = fileIO.load

      fileIO.save(game, myTurn)
    }
  }
}