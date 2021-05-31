package de.htwg.se.ticTacToe3D.controller.controllerComponent

import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface
import de.htwg.se.ticTacToe3D.util.Observable

import scala.concurrent.Future
import scala.util.Try

trait ControllerInterface extends Observable{
  def game: GameInterface
  def exit: Boolean
  def checkData(row: Int, column: Int, grid: Int): Future[Boolean]
  def checkForWin(i: Int, row: Int, column: Int, grid: Int): Boolean
  def setValue(row: Int, column: Int, grid: Int): Boolean
  def toString: String
  def getNextPlayer(index: Int): String
  def undo: Unit
  def redo: Unit
  def save: Unit
  def load: Unit
  def reset: Boolean
  def restart: Boolean
  def won: Array[Boolean]
  def setPlayers (player1: String, player2: String): Boolean
  def statusMessage: String
  def getLastMoves(): String
  def getPlayers(): String
  def saveGameToDB(): String
  def loadGameToDB(): String
}
