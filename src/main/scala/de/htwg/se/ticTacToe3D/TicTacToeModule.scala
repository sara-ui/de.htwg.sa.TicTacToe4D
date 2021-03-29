package de.htwg.se.ticTacToe3D

import com.google.inject.AbstractModule
import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Game
import net.codingwell.scalaguice.ScalaModule
import com.google.inject.Singleton
import de.htwg.se.ticTacToe3D.controller.controllerComponent.ControllerInterface
import de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl.Controller
import de.htwg.se.ticTacToe3D.model.fileIoComponent._


class TicTacToeModule extends AbstractModule with ScalaModule {

  def configure() = {
    bind[GameInterface].to(classOf[Game]).in(classOf[Singleton])
    bind[ControllerInterface].to(classOf[Controller]).in(classOf[Singleton])
    bind[FileIOInterface].to[fileIoXmlImpl.FileIO]
  }
}
