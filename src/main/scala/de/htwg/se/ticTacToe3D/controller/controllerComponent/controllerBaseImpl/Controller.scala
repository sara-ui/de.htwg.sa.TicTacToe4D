package de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl

import com.google.inject.{Guice, Inject}
import de.htwg.se.ticTacToe3D.TicTacToeModule
import de.htwg.se.ticTacToe3D.controller.controllerComponent.ControllerInterface
import de.htwg.se.ticTacToe3D.model.fileIoComponent.FileIOInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.GameInterface
import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.Game
import de.htwg.se.ticTacToe3D.model.{FactoryProducer, WinStateStrategyTemplate}
import de.htwg.se.ticTacToe3D.util.UndoManager

class Controller (var game: GameInterface,
                            var oneGridStrategy: Array[WinStateStrategyTemplate],
                            var allGridStrategy : Array[WinStateStrategyTemplate])
  extends ControllerInterface {
  private val undoManager = new UndoManager
  val injector = Guice.createInjector(new TicTacToeModule)
  val fileIo = injector.getInstance(classOf[FileIOInterface])
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

  var won: Array[Boolean] = Array(false, false)
  var myTurn: Boolean = true
  var statusMessage: String = Messages.WELCOME_MESSAGE
  def checkData(row: Int, column: Int, grid: Int): Boolean = {
    if(row > 3 || column > 3 || grid > 3){
      statusMessage =  Messages.ERROR_MOVE
      notifyObservers
      return false
    }
    true
  }
  def checkForWin(i: Int, row: Int, column: Int, grid: Int): Boolean = {
    won(i) = oneGridStrategy(i).checkForWin(row, column, grid) || allGridStrategy(i).checkForWin(row, column, grid)
    if(won(i)) {
      this.statusMessage = game.players(i).name + Messages.WIN_MESSAGE
      notifyObservers
    }
    true
  }

  def save = {
    fileIo.save(game, this.myTurn)
    statusMessage = Messages.GAME_SAVED
    notifyObservers
  }

  def load = {
    val (loadedGame, turn) = fileIo.load
    game = loadedGame
    statusMessage = Messages.GAME_LOADED + "\n" +  getNextPlayer(if(turn) 0 else 1) + Messages.YOU_ARE_NEXT
    notifyObservers
  }

  def setValue(row: Int, column: Int, grid: Int): Boolean = {
    if (game.players.contains(null) || "".equals(game.players(0).name)) {
      statusMessage = Messages.ERROR_GIVE_PLAYERS_START
      notifyObservers
      return false
    }
    if (checkData(row, column, grid)) {
      if(myTurn){
        tryToMove(0, row, column, grid)
        checkForWin(0, row, column, grid)
      }else{
        tryToMove(1, row, column, grid)
        checkForWin(1, row, column, grid)
      }
      myTurn = !myTurn
    }
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
  val CELL_IS_SET: String = "This Cell is Already been setted, please try another one \n"
  val USER_ERROR: String = "Please give the two players names \n"
  val ENTER_PLAYERS: String = "please enter the two players name with - between then (don't forget no spacing)"
  val WELCOME_MESSAGE: String = "Welcome to HTWG TicTacToe 4x4x4! \n" + ENTER_PLAYERS
  val PLAYER_NAME: String = "please enter players name again"
  val PLAYER_DEFINED_MESSAGE: String = "Players are defined!!!! \n"
  val INFO_ABOUT_THE_GAME: String = " you can start, the grids with the number in them are\n" +
    " an example of what you should give here if you want to player in one of the cells,\n" +
    " the 3 numbers are the row, column, grid. Now, MAKE YOU FIRST MOVE :) !!\n"
  val ERROR_GIVE_PLAYERS_START: String = "you can't start the Game without giving the name of the players\n" + ENTER_PLAYERS
  val ERROR_MOVE: String = "you are out of the limit of the grid!! please retry with correct move"
  val NEXT: String = " you are next!! \n press z to undo"
  val YOU_ARE_NEXT: String = " you are next!! \n"
  val GAME_RESET_MESSAGE: String = "Game was reseted!!!! \n"
  val ERROR_GIVE_PLAYERS_RESET: String = "you can't reset the Game without giving the name of the players\n" + ENTER_PLAYERS
  val WIN_MESSAGE: String = " you won !! congratulation \n "+ " if you want to start again press r + enter, if not press q + enter to quit"
  val UNDO_STEP: String = "you just undid your step, you can replay or press y to redo"
  val REDO_STEP: String = "you just redid your step, thanks"
  val TITLE: String = "TicTacToe 4x4x4"
  val GAME_SAVED: String = "Game is Saved"
  val GAME_LOADED: String = "Game is loaded"
  val MOVEMENT: String = "To move the Grids : Please use the arrows\n\n"
  def playerMoveToString(player: String, row: Int, column: Int, grid: Int): String = player + " played : (" + row + "," + column + ") in Grid " + grid + "\n"
}
