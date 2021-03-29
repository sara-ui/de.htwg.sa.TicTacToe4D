package de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl

import de.htwg.se.ticTacToe3D.util.Command

class SetCommand(row:Int, col: Int, grid:Int, playerIndex:Int, controller: Controller) extends Command {
  override def doStep: Unit = controller.game = controller.game.set(row, col, grid, playerIndex)

  override def undoStep: Unit = controller.game = controller.game.resetCell(row, col, grid)

  override def redoStep: Unit = controller.game = controller.game.set(row, col, grid, playerIndex)
}
