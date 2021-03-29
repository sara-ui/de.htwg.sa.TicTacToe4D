package de.htwg.se.ticTacToe3D.view.gui
import de.htwg.se.ticTacToe3D.model.gameComponent.CellInterface
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color
import javafx.scene.transform.Rotate


class CellPanel(cell: CellInterface, row: Int, column: Int, d: Double, grid: Int, e: Double, f: Double, color:Color) extends Rectangle{

  setWidth(d)
  setHeight(d)
  setFill(color)
  setTranslateX(e)
  setTranslateY(-2.05 * d / 4)
  setTranslateZ(f)
  setRotationAxis(Rotate.X_AXIS)
  setRotate(90)
  setId(row + " " + column + " " + grid)
  setValue(cell)

  def setValue(cell: CellInterface): Unit = {
    if ("X" == cell.value) this.setFill(Color.RED)
    else if ("O" == cell.value) this.setFill(Color.BLUE)
  }


  def isSet: Boolean = cell.isSet

  def resetCell(color: Color): Unit = {
    this.setFill(color)
  }
}
