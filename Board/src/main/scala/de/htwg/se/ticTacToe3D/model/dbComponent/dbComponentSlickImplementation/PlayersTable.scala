package de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentSlickImplementation

import slick.jdbc.MySQLProfile.api._

class PlayersTable(tag: Tag) extends Table[(Int, String, String)] (tag, "PlayersTable") {

  def id = column[Int]("Id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")
  def symbol = column[String]("symbol")

  def * = (id, name, symbol)

}
