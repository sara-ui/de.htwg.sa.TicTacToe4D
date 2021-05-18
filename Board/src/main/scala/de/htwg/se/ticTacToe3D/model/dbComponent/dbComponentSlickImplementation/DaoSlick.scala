package de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentSlickImplementation

import de.htwg.se.ticTacToe3D.model.dbComponent.DaoInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Game
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

  override def getLastMoves(): Array[(Int, Int, Int, Int, Int)] = {
    println("Board: ")
    println("  " + "ID\t" + "GRID\t" + "COL\t" + "ROW\t" + "PLAYERID")
    database.run(MovesTable.result).map(_.foreach {
      case (id, grid, col, row, playerId) =>
        println("  " + id + "\t" + grid + "\t" + col + "\t" + row + "\t" + playerId)
    })
    Await.result(database.run(MovesTable.result), Duration.Inf).toArray
  }

  override def setPlayers(player1: PlayerInterface, player2: PlayerInterface): Unit = {
    tables.foreach(e => database.run(e.delete))
    database.run(playersTable ++= Seq(
      (0, player1.name, player1.symbol),
      (1, player2.name, player2.symbol)
    ))
  }
  override def getPlayers(): Array[(Int, String, String)] = {
    println("Players: ")
    println("  " + "ID\t" + "NAME\t" + "SYMBOL")
    database.run(playersTable.result).map(_.foreach {
      case (id, name, symbol) =>
        println("  " + id + "\t" + name + "\t" + symbol)
    })
    Await.result(database.run(playersTable.result), Duration.Inf).toArray
  }

  def getPlayerById(id: Int): Option[(Int, String, String)] = {
    println("Player: ")
    println("  " + "ID\t" + "NAME\t" + "SYMBOL")
    database.run(playersTable.result).map(_.foreach {
      case (id, name, symbol) =>
        println("  " + id + "\t" + name + "\t" + symbol)
    })
    Await.result(database.run(playersTable.filter(_.id === id).result.headOption), Duration.Inf)
  }

  override def saveGame(game: GameInterface): Unit = {
    tables.foreach(e => database.run(e.delete))
    database.run(playersTable ++= Seq(
      (0, game.players(0).name, game.players(0).symbol),
      (1, game.players(1).name, game.players(1).symbol)
    ))
    var i = 0
    for (grid <- game.grids.indices) {
      println("Grids: " + grid)
      for {
        row <- 0 until game.grids(grid).size
        col <- 0 until game.grids(grid).size
      } yield {
        if (game.grids(grid).cell(row, col).isSet) {
          val id = if(game.grids(grid).cell(row, col).value == game.getPlayer(0).symbol) 0 else 1
          database.run(MovesTable += (i, grid, col, row, id))
          i += 1
        }
      }
    }
  }

  override def loadGame(game: GameInterface): GameInterface = {
    var game: GameInterface = new Game()
    val players = getPlayers()
    game = game.setPlayers(players(0)._2, players(1)._2, players(0)._3, players(1)._3)

    val lastMoves = getLastMoves()
    for (grid <- game.grids.indices) {
      println("Grids: " + grid)
      for {
        row <- 0 until game.grids(grid).size
        col <- 0 until game.grids(grid).size
      } yield {
        val value = lastMoves.find(elem => elem._2 == grid && elem._3 == col & elem._4 == row)
        if (value.nonEmpty) {
          val player = getPlayerById(value.get._5)
          game.set(
            row,
            col,
            grid,
            player.get._1
          )
        }
      }
    }
    game
  }
}