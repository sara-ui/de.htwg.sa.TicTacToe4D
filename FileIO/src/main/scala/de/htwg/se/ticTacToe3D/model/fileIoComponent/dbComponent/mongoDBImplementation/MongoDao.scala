package de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent.mongoDBImplementation

import com.mongodb.{MongoClientSettings, ServerAddress}
import de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent.DaoMongoInterface
import de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent.mongoDBImplementation.Helpers._
import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface
import org.mongodb.scala._
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates.{combine, set}
import org.mongodb.scala.result._
import play.api.libs.json.{JsValue, Json}

import scala.collection.JavaConverters._

case class MongoDao() extends DaoMongoInterface {

  val mongoClient: MongoClient = MongoClient(
    MongoClientSettings.builder()
      .applyToClusterSettings((builder: ClusterSettings.Builder) => builder.hosts(List(new ServerAddress("localhost", 27017)).asJava))
      .build()
  )
  //System.setProperty("org.mongodb.async.type", "netty")

  val database: MongoDatabase = mongoClient.getDatabase("mydb")
  database.createCollection("test")
  val collection: MongoCollection[Document] = database.getCollection("test")

  override def create(game: String): Unit = {
    saveGame(game)
  }

  def saveGame(game: String): Unit = {
    val observable: Observable[InsertOneResult] = collection.insertOne(createAndInsertDocument(game))
    observable.printHeadResult("Insert Result: ")
    printCollection()

    observable.subscribe(new Observer[InsertOneResult] {
      override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

      override def onNext(result: InsertOneResult): Unit = println(s"onNext $result")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })
  }

  def createAndInsertDocument(json : String): Document = {
    Document(json)
  }

  def printCollection(): Unit = collection.find().printResults()

  override def read(): String = loadGame()

  def loadGame(): String = {
    printCollection()
    collection.find().first().toString
  }


  //Might not need it
  override def update(game: String): Unit = {
    val observable: Observable[UpdateResult] = collection.updateOne(empty(), combine(Document(game)))
    observable.printHeadResult("Update Result: ")

    observable.subscribe(new Observer[UpdateResult] {
      override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

      override def onNext(result: UpdateResult): Unit = println(s"onNext $result")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })
  }

  /*
  DELETE ALL ENTRIES
   */
  override def delete(): Unit = {
    val observable: Observable[DeleteResult] = collection.deleteOne(empty())
    observable.printHeadResult("Delete Result: ")

    observable.subscribe(new Observer[DeleteResult] {
      override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

      override def onNext(result: DeleteResult): Unit = println(s"onNext $result")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })
  }
}
