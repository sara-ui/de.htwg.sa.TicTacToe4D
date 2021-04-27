package de.htwg.se.ticTacToe3D.model.fileIoComponent

import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface

trait FileIoInterface {

  def load: (GameInterface, Boolean)
  def save(game: GameInterface, turn: Boolean): Unit

}
