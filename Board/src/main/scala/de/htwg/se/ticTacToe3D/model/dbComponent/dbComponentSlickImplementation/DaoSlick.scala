package de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentSlickImplementation

import de.htwg.se.ticTacToe3D.model.dbComponent.DaoInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.{GameInterface, PlayerInterface}
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class DaoSlick() extends DaoInterface {

  val databaseUrl: String = "jdbc:mysql://" + sys.env.getOrElse("DATABASE_HOST", "localhost:3306") + "/" + sys.env.getOrElse("MYSQL_DATABASE", "tictactoeTable") + "?serverTimezone=UTC"
  val databaseUser: String = sys.env.getOrElse("MYSQL_USER", "root")
  val databasePassword: String = sys.env.getOrElse("MYSQL_PASSWORD", "")

  val database = Database.forURL(
    url = databaseUrl,
    driver = "com.mysql.cj.jdbc.Driver",
    user = databaseUser,
    password = databasePassword
  )

  val playersTable = TableQuery[PlayersTable]
  val MovesTable = TableQuery[MovesTable]
  val setup = DBIO.seq((playersTable.schema
    ++ MovesTable.schema).createIfNotExists)
  database.run(setup)

  override def getLastMoves(): Unit = {
    val query = MovesTable.result
    Await.result(database.run(query), Duration.Inf)
  }

  override def setPlayers(player1: PlayerInterface, player2: PlayerInterface): Unit = {
    database.run(playersTable ++= Seq(
      (0, player1.name, player1.symbol),
      (0, player2.name, player2.symbol)
    ))
  }
  override def getPlayers(): Unit = {
    val query = playersTable.result
    Await.result(database.run(query), Duration.Inf)
  }

  override def saveGame(game: GameInterface): Unit = {

  }
}