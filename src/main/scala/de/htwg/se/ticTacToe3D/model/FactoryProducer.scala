package de.htwg.se.ticTacToe3D.model

object FactoryProducer {
  def apply(kind: String): WinStateStrategyTemplate = kind match {
    case "oneD" => new OneDGridsStateStrategyFactory().getInstance()
    case "fourD" => new FourDGridsStateStrategyFactory().getInstance()
  }
}
