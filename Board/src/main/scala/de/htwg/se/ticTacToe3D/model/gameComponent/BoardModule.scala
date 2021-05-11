package de.htwg.se.ticTacToe3D.model.gameComponent

import com.google.inject.AbstractModule
import de.htwg.se.ticTacToe3D.model.dbComponent.DaoInterface
import de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentMongoDBImplementation.MongoDao
import de.htwg.se.ticTacToe3D.model.dbComponent.dbComponentSlickImplementation.DaoSlick
import net.codingwell.scalaguice.ScalaModule

case class BoardModule() extends AbstractModule with ScalaModule {

  override def configure() = {
    //bind[DaoInterface].toInstance(DaoSlick())
    bind[DaoInterface].toInstance(MongoDao())
  }
}
