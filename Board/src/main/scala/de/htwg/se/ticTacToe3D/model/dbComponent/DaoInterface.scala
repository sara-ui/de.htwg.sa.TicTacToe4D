package de.htwg.se.ticTacToe3D.model.dbComponent

import de.htwg.se.ticTacToe3D.model.gameComponent.{GameInterface, PlayerInterface}

trait DaoInterface {
  def getLastMoves(): Array[(Int, Int, Int, Int, Int)]
  def getPlayers(): Array[(Int, String, String)]
  def setPlayers(player1: PlayerInterface, player2: PlayerInterface)
  def saveGame(game: GameInterface)
  def loadGame(game: GameInterface): GameInterface
}