package de.htwg.se.ticTacToe3D.model

class FourDGridsStateStrategyFactory {
  def getInstance(): WinStateStrategyTemplate = new FourDGridsStateStrategy()
}
