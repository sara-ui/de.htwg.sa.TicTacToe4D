package de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import de.htwg.se.ticTacToe3D.util.Command

import scala.concurrent.Future
import scala.util.{Failure, Success}

class SetCommand(row:Int, col: Int, grid:Int, playerIndex:Int, controller: Controller) extends Command {
  override def doStep: Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")

    implicit val executionContext = system.executionContext

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://localhost:8080/board/setCell?row=$row&col=$col&grid=$grid&playerIndex=$playerIndex&myTurn=${controller.myTurn}"
    ))

    responseFuture
      .onComplete {
        case Failure(_) => sys.error("Failed setting Cell")
        case Success(value) => {
          Unmarshal(value.entity).to[String].onComplete {
            case Failure(_) => sys.error("Failed unmarshalling")
            case Success(value) => {
              val (loadedGame, turn) = controller.unmarshall(value)
              controller.game = loadedGame
              controller.notifyObservers
            }
          }
        }
      }
  }

  override def undoStep: Unit = {
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")

    implicit val executionContext = system.executionContext

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = s"http://localhost:8080/board/resetCell?row=$row&col=$col&grid=$grid&myTurn=${controller.myTurn}"
    ))

    responseFuture
      .onComplete {
        case Failure(_) => sys.error("Failed resetting Cell")
        case Success(value) => {
          Unmarshal(value.entity).to[String].onComplete {
            case Failure(_) => sys.error("Failed unmarshalling")
            case Success(value) => {
              val (loadedGame, turn) = controller.unmarshall(value)
              controller.game = loadedGame
            }
          }
        }
      }
  }

  override def redoStep: Unit = controller.game = controller.game.set(row, col, grid, playerIndex)
}
