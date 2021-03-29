package de.htwg.se.ticTacToe3D.model.gameComponent

import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Cell
import org.scalatest.{Matchers, WordSpec}

class CellSpec extends WordSpec with Matchers {

  "A Cell" when {
    "not set to any value " should {
      val emptyCell = Cell("")
      "have empty value " in {
        emptyCell.value should be("")
      }
      "have value space " in {
        emptyCell.toString should be(" ")
      }
      "not be set" in {
        emptyCell.isSet should be(false)
      }
    }
    "has a value " should {
      val emptyCell = Cell("X")
      "have X value " in {
        emptyCell.value should be("X")
      }
      "have value X " in {
        emptyCell.toString should be("X")
      }
      "not be set" in {
        emptyCell.isSet should be(true)
      }
    }
  }

}