package de.htwg.se.ticTacToe3D

import scala.io.StdIn
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives.{get, _}
import com.google.inject.{Guice, Injector}
import de.htwg.se.ticTacToe3D.controller.controllerComponent.ControllerInterface

case class TicTacToeService() {

  def start(): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val injector: Injector = Guice.createInjector(new TicTacToeModule)
    val controller: ControllerInterface = injector.getInstance(classOf[ControllerInterface])

    val route =
      concat (
        (get & path("game" / "load")) {
          controller.load
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, controller.toString))
        },
        (get & path("game" / "save")) {
          controller.save
          complete("game saved")
        },
        ( get & path("game" / "checkWin")) {
          parameters("i".as[Int], "row".as[Int], "column".as[Int], "grid".as[Int])
          { (i, row, column, grid) =>
            val checkForWin: Boolean = controller.checkForWin(i, row, column, grid)
            complete(HttpEntity(s"$checkForWin"))
          }
        },
        (get & path("board")) {
          complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, controller.toString))
        },
        (post & path("board" / "move")) {
          parameters("row".as[Int], "col".as[Int], "grid".as[Int]) {
            (row, col, grid) =>
              controller.setValue(row, col, grid)
              complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, controller.toString))
          }
        },
        (post & path("board" / "setPlayers")) {
          parameters("player1".as[String], "player2".as[String]) {
            (player1, player2) =>
              controller.setPlayers(player1, player2)
              complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, controller.toString))
          }
        },
        (get & path("game" / "restart")) {
            controller.restart
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, controller.toString))
        },
        (get & path("game" / "database" / "moves")) {
          controller.getLastMoves()
          complete(StatusCodes.OK)
        },
        (get & path("game" / "database" / "players")) {
          controller.getPlayers()
          complete(StatusCodes.OK)
        },
        (get & path("game" / "database" / "save")) {
          controller.saveGameToDB()
          complete(StatusCodes.OK)
        },
        (get & path("game" / "database" / "load")) {
          controller.loadGameToDB()
          complete(StatusCodes.OK)
        },
      )



    Http().newServerAt("0.0.0.0", 9090).bind(route)
    println(s"Check Win Server online at http://localhost:9090/\nPress RETURN to stop...")
  }
}
