package de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentMongoDBImplementation

import akka.actor.TypedActor.dispatcher
import de.htwg.se.ticTacToe3D.model.dbComponent.DaoInterface
import de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentMongoDBImplementation.Helpers._
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Game
import de.htwg.se.ticTacToe3D.model.gameComponent.{GameInterface, PlayerInterface}
import org.mongodb.scala._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.result._
import play.api.libs.json.{JsValue, Json}

import scala.util.{Failure, Success}

case class MongoDao() extends DaoInterface {

  val uri: String = "mongodb://root:rootpassword@mongodb:27017/"

  val mongoClient: MongoClient = MongoClient(uri)

  val database: MongoDatabase = mongoClient.getDatabase("mydb")
  val collection: MongoCollection[Document] = database.getCollection("MongoDBBoard")

  override def getLastMoves(): Array[(Int, Int, Int, Int, Int)] = {
    val levelDocument = collection.find(Filters.ne("_id", "players")).head().onComplete({
      case Success(value) => Some(value.toJson())
      case Failure(exception) => println("ERROR")
      })
    println(levelDocument)
    val json: JsValue = Json.parse(levelDocument.toString)
    println(json)
    // players
    val _id = (json \ "_id").head.as[Int]
    val grid = (json \ "grid").head.as[Int]
    val col = (json \ "_col").head.as[Int]
    val row = (json \ "row").head.as[Int]
    val id = (json \ "id").head.as[Int]
    Array((_id, grid, col, row, id))
  }

  override def getPlayers(): Array[(Int, String, String)] = {
    val levelDocument = collection.find(
      Filters.or(Filters.eq("id", 0), Filters.eq("id", 1))).head().onComplete({
      case Success(value) => Some(value.toJson())
      case Failure(exception) => println("ERROR")
    })
    val json: JsValue = Json.parse(levelDocument.toString)
    println(json)
    // players
    val players = (json \ "players").get
    val playerName1 = (players \\ "name").head.as[String]
    val playerName2 = (players \\ "name")(1).as[String]
    val playerSymbol1 = (players \\ "symbol").head.as[String]
    val playerSymbol2 = (players \\ "symbol")(1).as[String]
    Array((0, playerName1, playerSymbol1), (1, playerName2, playerSymbol2))
  }

  override def loadGame(game: GameInterface): GameInterface = {
    var game: GameInterface = new Game()
    val players = getPlayers()
    game = game.setPlayers(players(0)._2, players(1)._2, players(0)._3, players(1)._3)

    val lastMoves = getLastMoves()
    for (grid <- game.grids.indices) {
      println("Grids: " + grid)
      for {
        row <- 0 until game.grids(grid).size;
        col <- 0 until game.grids(grid).size
      } yield {
        val value = lastMoves.find(elem => elem._2 == grid && elem._3 == col & elem._4 == row)
        if (value.nonEmpty) {
          val player = collection.find(Filters.eq("id", value.get._5)).headResult()
          game.set(
            row,
            col,
            grid,
            player.head._1.toInt
          )
        }
      }
    }
    game
  }

  //CHECK DELETION OF ENTRIES, HAVE TO DO IT MANUALLY

  override def saveGame(game: GameInterface): Unit = {
    deleteMoveEntries()
    var i = 0
    for (grid <- game.grids.indices) {
      for {
        row <- 0 until game.grids(grid).size
        col <- 0 until game.grids(grid).size
      } yield {
        if (game.grids(grid).cell(row, col).isSet) {
          val id = if(game.grids(grid).cell(row, col).value == game.getPlayer(0).symbol) 0 else 1
          val observable: Observable[InsertOneResult] = collection.insertOne(
            Document("_id" -> i, "grid" -> grid, "col" -> col, "row" -> row, "id" -> id)
          )
          observable.printHeadResult("Insert Result: ")

          observable.subscribe(new Observer[InsertOneResult] {
            override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

            override def onNext(result: InsertOneResult): Unit = println(s"onNext $result")

            override def onError(e: Throwable): Unit = println("Failed")

            override def onComplete(): Unit = println("Completed")
          })
          i += 1
        }
      }
    }
  }

  override def setPlayers(player1: PlayerInterface, player2: PlayerInterface): Unit = {
    deletePlayerEntries()
    val observable: Observable[InsertOneResult] = collection
      .insertOne(
        Document("_id" -> "players", "players" -> Document(
          "player1" -> Document("name" -> player1.name, "symbol" -> player1.symbol),
          "player2" -> Document("name" -> player2.name, "symbol" -> player2.symbol))))

    observable.printHeadResult("Insert Result: ")

    observable.subscribe(new Observer[InsertOneResult] {
      override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

      override def onNext(result: InsertOneResult): Unit = println(s"onNext $result")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })
  }

  def deletePlayerEntries(): Unit = {
    val observable: Observable[DeleteResult] = collection.deleteMany(Filters.eq("_id", "players"))
    observable.printHeadResult("Delete Result: ")

    observable.subscribe(new Observer[DeleteResult] {
      override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

      override def onNext(result: DeleteResult): Unit = println(s"onNext $result")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })
  }

  def deleteMoveEntries(): Unit = {
    val observable: Observable[DeleteResult] = collection.deleteMany(Filters.notEqual("_id", "players"))
    observable.printHeadResult("Delete Result: ")

    observable.subscribe(new Observer[DeleteResult] {
      override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

      override def onNext(result: DeleteResult): Unit = println(s"onNext $result")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })
  }


}
