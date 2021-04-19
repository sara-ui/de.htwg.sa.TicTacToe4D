/*
 * Copyright (C) 2020-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package de.htwg.se.ticTacToe3D.model.fileIoComponent

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import scala.io.StdIn
import de.htwg.se.ticTacToe3D.model.fileIoComponent.fileIoJsonImpl.FileIO

case object FileIoService {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val route =
      concat (
        get {
          path("json") {
            complete(HttpEntity(ContentTypes.`application/json`, FileIO().loadJson()))
          }
        },
        post {
          path("json") {
            entity(as [String]) { game =>
              FileIO().save(game)
              println("GAME SAVED")
              complete("game saved")
            }
          }
        }
      )

    val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

    println(s"File IO Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}