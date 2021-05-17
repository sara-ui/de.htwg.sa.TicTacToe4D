package de.htwg.se.ticTacToe3D.model.gameComponent

import scala.io.StdIn
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.google.inject.{Guice, Injector}
import de.htwg.se.ticTacToe3D.model.dbComponent.DaoInterface
import de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentMongoDBImplementation.MongoDao
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Game

case object BoardService {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val injector: Injector = Guice.createInjector(new BoardModule)
    val db: DaoInterface = injector.getInstance(classOf[DaoInterface])

    val mongoDB: DaoInterface = injector.getInstance(classOf[MongoDao])

    var game: GameInterface = new Game()
    game.board(game, turn = true)

    val route =
      concat (
        get {
          path("board") {
              complete(HttpEntity(ContentTypes.`application/json`, game.loadBoardJson()))
          }
        },
        post {
          path("board") {
            game = new Game(game.players(0).name, game.players(1).name, "X", "O")
            game.board(game, turn = true)
            complete(HttpEntity(ContentTypes.`application/json`, game.loadBoardJson()))
          }
        },
        post {
          path("board" / "setPlayers") {
            parameters("player1".as[String], "player2".as[String], "myTurn".as[Boolean]) {
              (player1, player2, myTurn) =>
                game = new Game()
                game = game.setPlayers(player1, player2, "X", "O")
                game.board(game, myTurn)
                //mongoDB.setPlayers(game.getPlayer(0), game.getPlayer(1))
                db.setPlayers(game.getPlayer(0), game.getPlayer(1))
                complete(HttpEntity(ContentTypes.`application/json`, game.loadBoardJson()))
            }
          }
        },
        post {
          path("board" / "setCell") {
            parameters("row".as[Int], "col".as[Int], "grid".as[Int], "playerIndex".as[Int], "myTurn".as[Boolean]) {
              (row, col, grid, playerIndex, myTurn) =>
                if (game.players.contains(null) || "".equals(game.players(0).name)) {
                  complete(HttpResponse(InternalServerError, entity = "No players are defined!!"))
                } else {
                  game = game.set(row, col, grid, playerIndex)
                  game.board(game, myTurn)
                  complete(HttpEntity(ContentTypes.`application/json`, game.loadBoardJson()))
                }
            }
          }
        },
        post {
          path("board" / "resetCell") {
            parameters("row".as[Int], "col".as[Int], "grid".as[Int], "myTurn".as[Boolean]) {
              (row, col, grid, myTurn) =>
                game = game.resetCell(row, col, grid)
                game.board(game, myTurn)
                complete(HttpEntity(ContentTypes.`application/json`, game.loadBoardJson()))
            }
          }
        },
        get {
          path("board" / "cellIsSet") {
            parameters("row".as[Int], "col".as[Int], "grid".as[Int]) {
              (row, col, grid) =>
                val cellIsSet = game.cellIsSet(row, col, grid)
                complete(HttpEntity(s"$cellIsSet"))
            }
          }
        },
        (get & path("game" / "database" / "save")) {
          //mongoDB.saveGame(game)
          db.saveGame(game)
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, db.saveGame(game).toString))
        },
        (get & path("game" / "database" / "load")) {
          //game = mongoDB.loadGame(game)
          game = db.loadGame(game)
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, db.loadGame(game).customToString))
        },
        (get & path("game" / "database" / "players")) {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, db.getPlayers().mkString("Array(", ", ", ")")))
        },
        (get & path("game" / "database" / "moves")) {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, db.getLastMoves().mkString("Array(", ", ", ")")))
        },
      )


    Http().newServerAt("0.0.0.0", 9090).bind(route)

    println(s"Board Server online at http://board-service:9090/\nPress RETURN to stop...")
  }
}