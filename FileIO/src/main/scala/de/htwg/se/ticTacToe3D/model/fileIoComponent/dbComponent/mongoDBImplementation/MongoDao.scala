package de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent.mongoDBImplementation

import com.mongodb.{MongoClientSettings, ServerAddress}
import de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent.DaoMongoInterface
import de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent.mongoDBImplementation.Helpers._
import de.htwg.se.ticTacToe3D.model.fileIoComponent.fileIoJsonImpl.FileIO
import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface
import org.mongodb.scala._
import org.mongodb.scala.connection.ClusterSettings
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates.{combine, set}
import org.mongodb.scala.result._
import play.api.libs.json.{JsValue, Json}

import scala.collection.JavaConverters._

case class MongoDao() extends DaoMongoInterface {

  val uri: String = "mongodb://root:rootpassword@mongodb:27017/"

  val mongoClient: MongoClient = MongoClient(uri)

  val database: MongoDatabase = mongoClient.getDatabase("mydb")
  val collection: MongoCollection[Document] = database.getCollection("MongoDBFileIO")

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

  def createAndInsertDocument(game: String): Document = {
    var document: Document = Document(game)
    document
  }

  override def update(game: String): Unit = {
    delete()
    saveGame(game)
  }

  def printCollection(): Unit = collection.find().printResults()

  override def read(): String = loadGame()

  def loadGame(): String = {
    printCollection()
    collection.find().first().toString
  }

  override def delete(): Unit = {
    val observable: Observable[DeleteResult] = collection.deleteMany(empty())
    observable.printHeadResult("Delete Result: ")

    observable.subscribe(new Observer[DeleteResult] {
      override def onSubscribe(subscription: Subscription): Unit = subscription.request(1)

      override def onNext(result: DeleteResult): Unit = println(s"onNext $result")

      override def onError(e: Throwable): Unit = println("Failed")

      override def onComplete(): Unit = println("Completed")
    })
  }
}
