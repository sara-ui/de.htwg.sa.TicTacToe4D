package de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentSlickImplementation

import slick.jdbc.PostgresProfile.api._

class PlayersTable(tag: Tag) extends Table[(Int, String, String)] (tag, "PlayersTable") {

  def id = column[Int]("ID", O.PrimaryKey)

  def name = column[String]("NAME")
  def symbol = column[String]("SYMBOL")

  override def * = (id, name, symbol)

}
