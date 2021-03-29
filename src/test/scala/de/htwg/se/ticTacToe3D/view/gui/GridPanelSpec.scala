package de.htwg.se.ticTacToe3D.view.gui

import de.htwg.se.ticTacToe3D.controller.controllerComponent.ControllerInterface
import de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl.Controller
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Cell
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Game
import javafx.scene.paint.Color
import org.scalatest.{Matchers, WordSpec}

class GridPanelSpec extends WordSpec with Matchers {

  "A Cell Panel" should {

    val controller: ControllerInterface = new Controller(new Game)

    "should be initialized" in {
      val gridPanel = new GridPanel(4, 1, 1, controller)
      gridPanel.resetGrid
    }
  }
}
