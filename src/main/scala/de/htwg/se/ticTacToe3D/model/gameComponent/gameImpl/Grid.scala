package de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl

import de.htwg.se.ticTacToe3D.model.gameComponent.GridInterface

case class Grid(cells: Vector[Vector[Cell]]) extends GridInterface {
  def this() = this(Vector.tabulate(4, 4) { (row, col) => new Cell() })

  val size: Int = cells.size

  def cell(row: Int, col: Int): Cell = cells(row)(col)

  def cellIsSet(row: Int, col: Int): Boolean = cells(row)(col).isSet

  def set(row:Int, col:Int, value:String) = copy(cells.updated(row, cells(row).updated(col, Cell(value))))

  private def spacing(size: Int): String = {
    var res = " "
    for (i <- 0 to size) res += " "
    res
  }

  private def addCellToString(row: Int): String = {
    var str = spacing(row) + " \\"
    for {
      col <- 0 until cells(row).length
    } str += "   " + cell(row, col).toString + "   \\"
    str
  }

  private def newLine(row: Int): String = {
    val lineSep = System.lineSeparator
    var res = lineSep + spacing(row)+ "\\-------\\-------\\-------\\-------\\"
    res += " \\-------\\-------\\-------\\-------\\" + lineSep
    res
  }

  private def addExplanationCellToString(row: Int, grid: Int): String = {
    var res = " \\"
    for {
      column <- 0 until cells(row).length
    } {
      res += "  " + row + column + grid + "  \\"

    }
    res += newLine(row + 1)
    res
  }

  def customToString(grid: Int): String = {
    var res = newLine(0)
    for {
      row <- 0 until cells.length
    } {
      res += addCellToString(row) + addExplanationCellToString(row, grid)
    }
    res
  }

}
