package de.htwg.se.ticTacToe3D.view

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl.{Controller, Messages, SetCommand}
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Game
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class TuiSpec extends WordSpec with Matchers{

  "A TicTacToe Tui" should {
    val controller = new Controller(new Game())
    val tui = new Tui(controller)
    "do nothing on input 'q'" in {
      tui.processInputLine("q")
    }
    "create new players 'player1-player2'" in {
      implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "SingleRequest")

      implicit val executionContext: ExecutionContextExecutor = system.executionContext

      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        method = HttpMethods.POST,
        uri = s"http://localhost:8080/board/setPlayers?player1=player1&player2=player2&myTurn=false"
      ))

      responseFuture
        .onComplete {
          case Failure(_) => sys.error("Failed setting Players")
          case Success(value) => {
            Unmarshal(value.entity).to[String].onComplete {
              case Failure(_) => sys.error("Failed unmarshalling")
              case Success(value) => {
                controller.unmarshall(value)
                controller.game.players(0).name should be("player1")
                controller.game.players(1).name should be("player2")
              }
            }
          }
        }
    }
    "set a cell on input '303'" in {
      implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")

      implicit val executionContext = system.executionContext

      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
        method = HttpMethods.GET,
        uri = s"http://localhost:8080/cellIsSet?row=3&col=0&grid=3"
      ))

      responseFuture.onComplete {
        case Failure(_) => sys.error("Error checking for Cell")
        case Success(value) => {
          Unmarshal(value.entity).to[String].onComplete {
            case Failure(_) => sys.error("Failed unmarshalling")
            case Success(value) => {
              value should be("false")
            }
          }
        }
      }
    }
    "reset the game on input 'r'" in {
      tui.processInputLine("r")
      controller.game.cellIsSet(3, 0, 3) should be(false)
      controller.game.grids(3).cell(3, 0).value should be("")
    }
  }

}
