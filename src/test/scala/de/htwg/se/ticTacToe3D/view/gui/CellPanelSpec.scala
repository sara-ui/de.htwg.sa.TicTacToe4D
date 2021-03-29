package de.htwg.se.ticTacToe3D.view.gui

import org.scalatest.{Matchers, WordSpec}
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Cell
import javafx.scene.paint.Color

class CellPanelSpec extends WordSpec with Matchers {

  "A Cell Panel" should {

    "should be initialized" in {
      val cell = Cell("")
      val cellPanel = new CellPanel(cell, 1, 1, 1, 1, 1, 1, Color.ALICEBLUE)

      cellPanel.isSet should be(false)

      val cell2 = Cell("X")

      cellPanel.setValue(cell2)

      cellPanel.resetCell(Color.ANTIQUEWHITE)
    }
  }
}
