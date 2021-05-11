/*
 * Copyright (C) 2020-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package de.htwg.se.ticTacToe3D.model.fileIoComponent

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import com.google.inject.{Guice, Injector}
import de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent.DaoMongoInterface
import de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent.mongoDBImplementation.MongoDao
import de.htwg.se.ticTacToe3D.model.fileIoComponent.fileIoJsonImpl.FileIO
import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Game
import play.api.libs.json.Json

case object FileIoService {

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem(Behaviors.empty, "my-system")
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.executionContext

    val emptyGame: GameInterface = new Game()

    val injector: Injector = Guice.createInjector(new IoModule)
    val mongoDB: DaoMongoInterface = injector.getInstance(classOf[MongoDao])

    mongoDB.create(Json.stringify(emptyGame.boardToJson(emptyGame, turn = false)))

    val route =
      concat (
        get {
          path("game") {
            complete(HttpEntity(ContentTypes.`application/json`, mongoDB.read()))
            //complete(HttpEntity(ContentTypes.`application/json`, FileIO().loadJson()))
          }
        },
        post {
          path("game") {
            entity(as [String]) { game =>
              mongoDB.update(game)
              //FileIO().save(game)
              complete("game saved")
            }
          }
        }
      )

    Http().newServerAt("0.0.0.0", 9090).bind(route)

    println(s"File IO Server online at http://game-service:9090/\nPress RETURN to stop...")
  }
}