package de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl

import de.htwg.se.ticTacToe3D.model.gameComponent.CellInterface

case class Cell(value: String) extends CellInterface {
  def this() = this("")
  def isSet: Boolean = !"".equals(value)

  override def toString: String = {
    if (!isSet) return " "
    value
  }
}
