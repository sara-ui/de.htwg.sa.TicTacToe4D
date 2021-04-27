package de.htwg.se.ticTacToe3D.model

import scala.io.StdIn
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

import scala.util.{Failure, Success, Try}

case object CheckWinService {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    var oneGridStrategy: Array[WinStateStrategyTemplate] = Array.fill(2)(FactoryProducer("oneD"))
    var allGridStrategy : Array[WinStateStrategyTemplate] = Array.fill(2)(FactoryProducer("fourD"))

    val route =
      concat (
        get {
          path("checkWin") {
            parameters("i".as[Int], "row".as[Int], "column".as[Int], "grid".as[Int])
            { (i, row, column, grid) =>
              val checkWinOneGrid: Try[Boolean] = oneGridStrategy(i).checkForWin(row ,column ,grid).map(won => won)
              val checkWinFourGrid: Try[Boolean] = allGridStrategy(i).checkForWin(row ,column ,grid).map(won => won)
              val checkWin: Boolean = matchCheckWin(checkWinOneGrid) || matchCheckWin(checkWinFourGrid)

              complete(HttpEntity(s"$checkWin"))
            }
          }
        },
        post {
          path("checkWin") {
            oneGridStrategy = Array.fill(2)(FactoryProducer("oneD"))
            allGridStrategy = Array.fill(2)(FactoryProducer("fourD"))
            complete("Game has been reset")
          }
        }
      )


    Http().newServerAt("0.0.0.0", 9090).bind(route)
    println(s"Check Win Server online at http://checkwin-service:9090/\nPress RETURN to stop...")
  }

  def matchCheckWin(tryWin: Try[Boolean]): Boolean = tryWin match {
    case Success(x) => true
    case Failure(error) =>
      Failure(new Exception(error.getMessage))
      false
  }
}