package de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentSlickImplementation

import de.htwg.se.ticTacToe3D.model.dbComponent.DaoInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Player
import de.htwg.se.ticTacToe3D.model.gameComponent.{GameInterface, PlayerInterface}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

case class DaoSlick() extends DaoInterface {

  val databaseUrl: String = "jdbc:postgresql://" + sys.env.getOrElse("DATABASE_HOST", "db:5432") + "/" + sys.env.getOrElse("POSTGRES_DB", "tictactoeTable")
  val databaseUser: String = sys.env.getOrElse("POSTGRES_USER", "test")
  val databasePassword: String = sys.env.getOrElse("POSTGRES_PASSWORD", "user")

  val database = Database.forURL(
    url = databaseUrl,
    driver = "org.postgresql.Driver",
    user = databaseUser,
    password = databasePassword
  )

  val playersTable = TableQuery[PlayersTable]
  val MovesTable = TableQuery[MovesTable]
  val tables = List(playersTable, MovesTable)

  tables.foreach(e => Await.result(database.run(e.schema.createIfNotExists), Duration.Inf))

  override def getLastMoves(): Unit = {
    println("Board: ")
    println("  " + "ID\t" + "GRID\t" + "COL\t" + "ROW\t" + "PLAYERID")
    database.run(MovesTable.result).map(_.foreach {
      case (id, grid, col, row, playerId) =>
        println("  " + id + "\t" + grid + "\t" + col + "\t" + row + "\t" + playerId)
    })
  }

  override def setPlayers(player1: PlayerInterface, player2: PlayerInterface): Unit = {
    database.run(playersTable ++= Seq(
      (0, player1.name, player1.symbol),
      (1, player2.name, player2.symbol)
    ))
  }
  override def getPlayers(): Unit = {
    println("Players: ")
    println("  " + "ID\t" + "NAME\t" + "SYMBOL")
    database.run(playersTable.result).map(_.foreach {
      case (id, name, symbol) =>
        println("  " + id + "\t" + name + "\t" + symbol)
    })
  }

  override def saveGame(game: GameInterface): Unit = {
    tables.foreach(e => database.run(e.delete))
    database.run(playersTable ++= Seq(
      (0, game.players(0).name, game.players(0).symbol),
      (1, game.players(1).name, game.players(1).symbol)
    ))
    // STILL NEED TO DO SOME STUFF
    for (x <- 0 to game.grids.length) {
      println("Grids: " + x)
      for (y <- 0 to x) {
        println("Cells: " + y)
      }
    }
  }

  override def loadGame(game: GameInterface): Unit = {

  }
}