package de.htwg.se.ticTacToe3D.model.gameComponent.gameImpl

import de.htwg.se.ticTacToe3D.model.gameComponent.GridInterface

case class Grid(
                 cells: Vector[Vector[Cell]] =
                 Vector.tabulate(4, 4) { (row, col) => new Cell()})
  extends GridInterface {

  val size: Int = cells.size

  def cell(row: Int, col: Int): Cell = cells(row)(col)

  def cellIsSet(row: Int, col: Int): Boolean = cells(row)(col).isSet

  def set(row:Int, col:Int, value:String): Grid = copy(cells.updated(row, cells(row).updated(col, Cell(value))))

  private def spacing(size: Int): String = {
    Range(0,size).foldLeft(" ")((str, _) => str + " ")
  }

  private def addCellToString(row: Int): String = {
    cells(row).indices.foldLeft(spacing(row) + " \\")((acc, col) => acc + "   " + cell(row, col).toString + "   \\")
  }

  private def newLine(row: Int): String = {
    val lineSep = System.lineSeparator
    var res = Range(0,4).foldLeft(lineSep + spacing(row))((str, _) => str + "\\-------")
    res += Range(0,4).foldLeft("\\ \\")((str, _) => str + "-------\\") + lineSep
    res
  }

  private def addExplanationCellToString(row: Int, grid: Int): String = {
    cells(row).indices.foldLeft(" \\")((acc, col) => acc + "  " + row + col + grid + "  \\") + newLine(row + 1)
  }

  def customToString(grid: Int): String = {
    cells.indices.foldLeft(newLine(0))((acc, row) => acc + addCellToString(row) + addExplanationCellToString(row, grid))
  }
}
