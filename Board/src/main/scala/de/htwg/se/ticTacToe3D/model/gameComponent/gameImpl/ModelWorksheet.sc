import de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl.{Grid, Player}
case class Cell(value: String) {
  def isSet: Boolean = !"".equals(value)
}

val cell1 = new Cell("")
cell1.value
cell1.isSet

val cell2 = new Cell("X")
cell2.value
cell2.isSet

val grid = new Grid()
grid.cells
grid.cell(1, 1)
grid.cell(1, 1).isSet
grid.set(1, 1, "X")
grid.size

val player = new Player("Sara", "X")
player.name
player.symbol