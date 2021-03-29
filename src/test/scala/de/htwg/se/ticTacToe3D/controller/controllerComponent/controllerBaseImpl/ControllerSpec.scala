package de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl

import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.{Cell, Game}
import de.htwg.se.ticTacToe3D.util.Observer
import org.scalatest.{Matchers, WordSpec}

import scala.language.reflectiveCalls

class ControllerSpec extends WordSpec with Matchers {

  "A Controller" when {
    "observed by an Observer" should {
      val game = new Game()
      val controller = new Controller(game)
      val observer = new Observer {
        var updated: Boolean = false
        def isUpdated: Boolean = updated
        override def update: Boolean = {updated = true; updated}
      }
      controller.add(observer)
      "notify its Observer after players creation" in {
        controller.reset
        observer.updated should be(true)
        controller.setValue(1, 1, 1)
        observer.updated should be(true)
        controller.setPlayers("", "")
        controller.setPlayers("player1", "player2")
        observer.updated should be(true)
        controller.game.players(0).name should be("player1")
        controller.game.players(1).name should be("player2")
      }
      "notify its Observer after setting value" in {
        val oldGame = controller.game
        controller.setValue(1, 1, 1)
        observer.updated should be(true)
        controller.game.grids(1).cell(1, 1) should be(Cell("X"))
        oldGame.grids(1).cell(1, 1) should be(Cell(""))
        controller.setValue(1, 1, 1)
        controller.setValue(1, 2, 1)
        observer.updated should be(true)
        controller.game.grids(1).cell(1, 2) should be(Cell("O"))
        oldGame.grids(1).cell(1, 2) should be(Cell(""))
      }
      "handle undo/redo correctly on an empty undo-stack" in {
        controller.reset
        controller.setPlayers("player1", "player2")
        observer.updated should be(true)

        System.out.println(controller.setValue(2, 2, 2))
        observer.updated should be(true)
        controller.game.cellIsSet(2, 2, 2) should be(true)
        controller.game.grids(2).cell(2, 2) should be(Cell("X"))
        controller.undo
        observer.updated should be(true)
        controller.game.cellIsSet(2, 2, 2) should be(false)
        controller.game.grids(2).cell(2, 2) should be(Cell(""))
        controller.redo
        observer.updated should be(true)
        controller.game.grids(2).cell(2, 2) should be(Cell("X"))
        controller.game.cellIsSet(2, 2, 2) should be(true)
      }
      "toString should not be empty" in {
        controller.toString should not equal ""
      }
      "notify its Observer after playing 4,4,4" in {
        controller.setValue(4, 4, 4)
        observer.updated should be(true)
      }
      "notify its Observer after winning rows" in {
        controller.reset
        controller.setValue(0, 0, 0)
        controller.setValue(2, 0, 3)
        controller.setValue(0, 1, 0)
        controller.setValue(2, 1, 3)
        controller.setValue(0, 2, 0)
        controller.setValue(2, 2, 3)
        controller.setValue(0, 3, 0)
        controller.setValue(2, 3, 3)
        observer.updated should be(true)
      }
      "notify its Observer after winning columns" in {
        controller.reset
        controller.setValue(0, 0, 3)
        controller.setValue(2, 0, 3)
        controller.setValue(1, 0, 3)
        controller.setValue(2, 1, 3)
        controller.setValue(2, 0, 3)
        controller.setValue(2, 2, 3)
        controller.setValue(3, 0, 3)
        controller.setValue(2, 3, 3)
        observer.updated should be(true)
      }
      "notify its Observer after winning Diagonal" in {
        controller.reset
        controller.setValue(0, 0, 0)
        controller.setValue(2, 0, 3)
        controller.setValue(1, 1, 0)
        controller.setValue(2, 1, 3)
        controller.setValue(2, 2, 0)
        controller.setValue(2, 2, 3)
        controller.setValue(3, 3, 0)
        controller.setValue(2, 3, 3)
        observer.updated should be(true)
      }
      "notify its Observer after resetting" in {
        controller.reset
        observer.updated should be(true)
        controller.game.grids(1).cell(1, 1) should be(Cell(""))
      }
    }
  }
}