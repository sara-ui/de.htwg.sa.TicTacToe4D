package de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentSlickImplementation

import slick.jdbc.PostgresProfile.api._

class MovesTable(tag: Tag) extends Table[(Int, Int, Int, Int, Int)] (tag, "MovesTable") {

  def id = column[Int]("Id", O.PrimaryKey)

  def grid = column[Int]("GRID")
  def col = column[Int]("COL")
  def row = column[Int]("ROW")
  def playerId = column[Int]("PLAYERID")

  override def * = (id, grid, col, row, playerId)

  def PlayersTableForeignKey = foreignKey("PlayersTable", playerId, TableQuery[PlayersTable])(_.id)

}
