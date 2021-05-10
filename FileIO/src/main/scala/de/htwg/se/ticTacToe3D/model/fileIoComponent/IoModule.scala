package de.htwg.se.ticTacToe3D.model.fileIoComponent

import com.google.inject.AbstractModule
import de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent.DaoMongoInterface
import de.htwg.se.ticTacToe3D.model.fileIoComponent.dbComponent.mongoDBImplementation.MongoDao
import net.codingwell.scalaguice.ScalaModule

case class IoModule() extends AbstractModule with ScalaModule {

  override def configure() = {
    bind[DaoMongoInterface].toInstance(MongoDao())
  }
}