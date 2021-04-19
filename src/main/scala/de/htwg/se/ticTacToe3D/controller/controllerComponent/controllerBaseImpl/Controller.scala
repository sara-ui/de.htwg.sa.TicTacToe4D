package de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.google.inject.{Guice, Inject}
import de.htwg.se.ticTacToe3D.TicTacToeModule
import de.htwg.se.ticTacToe3D.controller.controllerComponent.ControllerInterface
import de.htwg.se.ticTacToe3D.model.fileIoComponent.FileIOInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Game
import de.htwg.se.ticTacToe3D.model.{FactoryProducer, WinStateStrategyTemplate}
import de.htwg.se.ticTacToe3D.util.UndoManager
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class Controller (var game: GameInterface,
                  var oneGridStrategy: Array[WinStateStrategyTemplate],
                  var allGridStrategy : Array[WinStateStrategyTemplate])
  extends ControllerInterface {

  private val undoManager = new UndoManager
  val injector = Guice.createInjector(new TicTacToeModule)
  val fileIo = injector.getInstance(classOf[FileIOInterface])
  var won: Array[Boolean] = Array(false, false)
  var myTurn: Boolean = true
  var statusMessage: String = Messages.WELCOME_MESSAGE

  @Inject()
  def this (game: GameInterface) {
    this(game,
      Array.fill(2)(FactoryProducer("oneD")),
      Array.fill(2)(FactoryProducer("fourD")))
  }
  def exit: Boolean = {
    System.exit(0)
    true
  }

  def checkData(row: Int, column: Int, grid: Int): Try[Boolean] = {
    if(row > 3 || column > 3 || grid > 3){
      statusMessage =  Messages.ERROR_MOVE
      notifyObservers
      return Failure(new Exception(Messages.ERROR_MOVE))
    }
    Success(true)
  }
  def checkForWin(i: Int, row: Int, column: Int, grid: Int): Boolean = {
    val checkWinOneGrid: Try[Boolean] = oneGridStrategy(i).checkForWin(row ,column ,grid).map(won => won)
    val checkWinFourGrid: Try[Boolean] = allGridStrategy(i).checkForWin(row ,column ,grid).map(won => won)
    matchCheckWin(checkWinOneGrid, i) || matchCheckWin(checkWinFourGrid, i)
  }

  def matchCheckWin(tryWin: Try[Boolean], i: Int): Boolean = tryWin match {
      case Success(x) =>
        this.statusMessage = game.players(i).name + Messages.WIN_MESSAGE
        notifyObservers
        true
      case Failure(error) =>
        Failure(new Exception(error.getMessage))
        false
  }

  def gameToJson(game: GameInterface, turn: Boolean): JsValue = {
    Json.obj(
      "turn" -> turn,
      "players" -> Json.toJson(
        for {
          index <- game.players.indices
        } yield {
          Json.obj(
            "name" -> game.players(index).name,
            "symbol" -> game.players(index).symbol
          )
        }
      ),
      "grids" -> Json.toJson(
        for {
          index <- game.grids.indices
        } yield {
          Json.obj(
            "cells" -> Json.toJson(
              for {
                row <- 0 until game.grids(index).size;
                col <- 0 until game.grids(index).size
              } yield {
                Json.obj(
                  "row" -> row,
                  "col" -> col,
                  "value" -> game.grids(index).cell(row, col).value
                )
              }
            )
          )
        }
      )
    )
  }

  def save = {
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")

    implicit val executionContext = system.executionContext

    val gameAsJson = Json.prettyPrint(gameToJson(game, this.myTurn))

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8080/game",
      entity = gameAsJson
    ))
    statusMessage = Messages.GAME_SAVED
    notifyObservers
  }

  def load = {
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")

    implicit val executionContext = system.executionContext

    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://localhost:8080/game"))

    responseFuture
      .onComplete {
        case Failure(_) => sys.error("Failed getting Json")
        case Success(value) => {
          Unmarshal(value.entity).to[String].onComplete {
            case Failure(_) => sys.error("Failed unmarshalling")
            case Success(value) => {
              val (loadedGame, turn) = unmarshall(value)
              game = loadedGame
              statusMessage = Messages.GAME_LOADED + "\n" +  getNextPlayer(if(turn) 0 else 1) + Messages.YOU_ARE_NEXT
              notifyObservers
            }
          }
        }
    }
  }

  def unmarshall(gameAsJson: String): (GameInterface, Boolean) = {
    var game: GameInterface = new Game()
    val source: String = gameAsJson
    val json: JsValue = Json.parse(source)
    val turn = (json \ "turn").as[Boolean]
    // players
    val players = (json \ "players").get
    val playerName1 = (players \\ "name").head.as[String]
    val playerName2 = (players \\ "name")(1).as[String]
    val playerSymbol1 = (players \\ "symbol").head.as[String]
    val playerSymbol2 = (players \\ "symbol")(1).as[String]
    game = game.setPlayers(playerName1, playerName2, playerSymbol1, playerSymbol2)
    // Grids
    val grids = (json \ "grids").get
    for (i <- 0 until 4) {
      val grid = (grids \\ "cells")(i)
      for (index <- 0 until 16) {
        val row = (grid \\ "row")(index).as[Int]
        val col = (grid \\ "col")(index).as[Int]
        val value = (grid \\ "value")(index).as[String]
        if (!value.equals("")) {
          game = game.set(
            row,
            col,
            i,
            if (value.equals(playerSymbol1)) 0 else 1
          )
        }
      }
    }
    (game,
      turn)
  }

  def setValue(row: Int, column: Int, grid: Int): Boolean = {
    if (game.players.contains(null) || "".equals(game.players(0).name)) {
      statusMessage = Messages.ERROR_GIVE_PLAYERS_START
      notifyObservers
      return false
    }
    val checkCell: Try[Boolean] = checkData(row, column, grid).map(valid => valid)
    checkCell match {
      case Success(x) =>
        if (myTurn) {
          tryToMove(0, row, column, grid)
          checkForWin(0, row, column, grid)
        } else {
          tryToMove(1, row, column, grid)
          checkForWin(1, row, column, grid)
        }
      case Failure(error) =>
        Failure(new Exception(error.getMessage))
        return false
    }
    myTurn = !myTurn
    true
  }
  override def toString: String = game.customToString
  def getNextPlayer(index: Int): String = {
    if (index == 0) {
      game.players(1).name
    } else {
      game.players(0).name
    }
  }
  private def tryToMove(playerIndex: Int, row: Int, column: Int, grid: Int): Boolean = {
    if (game.cellIsSet(row, column, grid)) {
      this.myTurn = !this.myTurn
      this.statusMessage = Messages.CELL_IS_SET
    } else {
      undoManager.doStep(new SetCommand(row, column, grid, playerIndex, this))
      this.statusMessage = Messages.playerMoveToString(game.players(playerIndex).name, row, column, grid) + getNextPlayer(playerIndex) + Messages.NEXT
    }
    notifyObservers
    true
  }
  def undo: Unit = {
    myTurn = !myTurn
    undoManager.undoStep
    this.statusMessage = Messages.UNDO_STEP
    notifyObservers
  }

  def redo: Unit = {
    myTurn = !myTurn
    undoManager.redoStep
    this.statusMessage = Messages.REDO_STEP
    notifyObservers
  }

  def restart: Boolean = {
    if (game.players.contains(null) || "".equals(game.players(0).name)) {
      this.statusMessage = Messages.ERROR_GIVE_PLAYERS_RESET
    } else {
      game = new Game(game.players(0).name, game.players(1).name, "X", "O")
      myTurn = true
      won = Array(false, false)
      oneGridStrategy = Array.fill(2)(FactoryProducer("oneD"))
      allGridStrategy = Array.fill(2)(FactoryProducer("fourD"))
      this.statusMessage = Messages.GAME_RESET_MESSAGE + game.players(0).name + Messages.INFO_ABOUT_THE_GAME
    }
    notifyObservers
    true
  }
  def reset: Boolean = {
    game = new Game()
    myTurn = true
    won = Array(false, false)
    oneGridStrategy = Array.fill(2)(FactoryProducer("oneD"))
    allGridStrategy = Array.fill(2)(FactoryProducer("fourD"))
    this.statusMessage = Messages.WELCOME_MESSAGE
    notifyObservers
    true
  }
  def setPlayers (player1: String, player2: String): Boolean = {
    if ("".equals(player1) || "".equals(player2)) {
      this.statusMessage = Messages.PLAYER_NAME
      notifyObservers
    } else {
      game = new Game(player1, player2, "X", "O")
    }
    statusMessage = Messages.PLAYER_DEFINED_MESSAGE + player1 + Messages.INFO_ABOUT_THE_GAME
    notifyObservers
    true
  }
}

object Messages {
  val CELL_IS_SET: String = "This Cell has already been set, please try another Cell\n"
  val USER_ERROR: String = "Please enter the player names\n"
  val ENTER_PLAYERS: String = "Please enter two players with - between then (don't forget no spacing)"
  val WELCOME_MESSAGE: String = "Welcome to HTWG TicTacToe 4x4x4! \n" + ENTER_PLAYERS
  val PLAYER_NAME: String = "Please enter players name again"
  val WIN_ERROR: String = "No Player has won yet"
  val PLAYER_DEFINED_MESSAGE: String = "Players are defined!!!\n"
  val INFO_ABOUT_THE_GAME: String = " you can start. The grids with the number in them are\n" +
    " an example of what you should enter if you want place a your marker in this cell.\n" +
    " The 3 numbers are row, column and grid. Now, MAKE YOUR FIRST MOVE :)\n"
  val ERROR_GIVE_PLAYERS_START: String = "You can't start the Game without giving the name of the players\n" + ENTER_PLAYERS
  val ERROR_MOVE: String = "No viable Cell number!!! Please retry with the correct move"
  val NEXT: String = " you are next!! \n press z to undo"
  val YOU_ARE_NEXT: String = " you are next!! \n"
  val GAME_RESET_MESSAGE: String = "Game has been reset!!!! \n"
  val ERROR_GIVE_PLAYERS_RESET: String = "You can't reset the Game without giving the name of the players\n" + ENTER_PLAYERS
  val WIN_MESSAGE: String = " you won !! Congratulations \n "+ " If you want to start again press r + enter, if not press q + enter to quit"
  val UNDO_STEP: String = "You just undid your step, you can replay or press y to redo"
  val REDO_STEP: String = "You just redid your step, thanks"
  val TITLE: String = "TicTacToe 4x4x4"
  val GAME_SAVED: String = "Game is saved"
  val GAME_LOADED: String = "Game is loaded"
  val MOVEMENT: String = "To move the Grids: Please use the arrows\n\n"
  def playerMoveToString(player: String, row: Int, column: Int, grid: Int): String = player + " played : (" + row + "," + column + ") in Grid " + grid + "\n"
}
