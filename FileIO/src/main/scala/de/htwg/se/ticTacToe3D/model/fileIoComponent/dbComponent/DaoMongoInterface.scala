package de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent

import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface

trait DaoMongoInterface {
  def create(game: String): Unit //SAVE GAME
  def update(game: String): Unit
  def read(): String //LOAD GAME
  def delete(): Unit
}
