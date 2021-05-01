package de.htwg.se.ticTacToe3D.model.dbComponent

import de.htwg.se.ticTacToe3D.model.gameComponent.{GameInterface, PlayerInterface}

trait DaoInterface {
  def getLastMoves()
  def getPlayers()
  def setPlayers(player1: PlayerInterface, player2: PlayerInterface)
  def saveGame(game: GameInterface)
}