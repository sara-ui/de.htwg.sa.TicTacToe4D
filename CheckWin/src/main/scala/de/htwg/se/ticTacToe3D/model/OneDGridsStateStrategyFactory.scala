package de.htwg.se.ticTacToe3D.model

class OneDGridsStateStrategyFactory {
  def getInstance(): WinStateStrategyTemplate = new OneDGridsStateStrategy()
}
