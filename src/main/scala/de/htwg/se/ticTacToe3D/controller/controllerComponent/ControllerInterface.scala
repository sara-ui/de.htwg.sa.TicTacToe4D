package de.htwg.se.ticTacToe3D.controller.controllerComponent

import de.htwg.se.ticTacToe3D.model.WinStateStrategyTemplate
import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface
import de.htwg.se.ticTacToe3D.util.Observable

trait ControllerInterface extends Observable{
  def game: GameInterface
  def oneGridStrategy: Array[WinStateStrategyTemplate]
  def allGridStrategy : Array[WinStateStrategyTemplate]
  def exit: Boolean
  def checkData(row: Int, column: Int, grid: Int): Boolean
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

}
