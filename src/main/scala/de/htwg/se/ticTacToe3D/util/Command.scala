package de.htwg.se.ticTacToe3D.util

trait Command {

  def doStep:Unit
  def undoStep:Unit
  def redoStep:Unit

}
