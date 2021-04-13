package de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl

import de.htwg.se.ticTacToe3D.model.gameComponent.PlayerInterface

case class Player(name: String, symbol: String) extends PlayerInterface {
  override def toString:String = "name: " + name +" symbol: " + symbol
}
