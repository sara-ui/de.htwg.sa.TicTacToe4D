package de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentSlickImplementation

import slick.jdbc.MySQLProfile.api._

class MovesTable(tag: Tag) extends Table[(Int, Int, Int, Int, Int)] (tag, "MovesTable") {

  def id = column[Int]("Id", O.PrimaryKey, O.AutoInc)

  def grid = column[Int]("grid")
  def col = column[Int]("col")
  def row = column[Int]("row")
  def playerId = column[Int]("playerId")

  def * = (id, grid, col, row, playerId)

  def PlayersTableForeignKey = foreignKey("PlayersTable", playerId, TableQuery[PlayersTable])(_.id)

}
