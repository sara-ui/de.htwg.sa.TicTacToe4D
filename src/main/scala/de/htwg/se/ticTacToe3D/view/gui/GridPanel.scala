package de.htwg.se.ticTacToe3D.view.gui
import de.htwg.se.ticTacToe3D.controller.controllerComponent.ControllerInterface
import javafx.scene.Group
import javafx.scene.input.MouseEvent
import javafx.scene.transform.Rotate
import javafx.scene.paint.Color


class GridPanel(size: Double, shade: Double, id: Int, controller: ControllerInterface) extends Group {

  private val color = Color.rgb(237, 255, 250)
  val rx = new Rotate(0, Rotate.X_AXIS)
  val ry = new Rotate(0, Rotate.Y_AXIS)
  val rz = new Rotate(0, Rotate.Z_AXIS)

  var width = 0.5
  var col = 0.1
  var height = 0.335
  var index = 1

  getTransforms.addAll(rz, ry, rx)

  val cells: Vector[Vector[CellPanel]] = Vector.tabulate(4, 4) { (row, column) => {
    if (row == index && row >  0) {
      width -= 1.001
      height = 0.335
      index += 1
    }
    val cell = new CellPanel(controller.game.grids(id).cell(row, column), row,
      column, size / 4, id, width * size / 4, height * size,
      color.deriveColor(0.0, 1.0, 1 - 0.15 * col, 1.0))
    height -= 0.335
    col += 0.2
    cell.setOnMouseClicked((event: MouseEvent) => {
      val data = cell.getId.split(" ")
      if (!controller.won(0) && !controller.won(1)) controller.setValue(data(0).toInt, data(1).toInt, data(2).toInt)
    })
    cell
  } }

  getChildren.addAll(
    cells(0)(0), cells(0)(1), cells(0)(2), cells(0)(3),
    cells(1)(0), cells(1)(1), cells(1)(2), cells(1)(3),
    cells(2)(0),cells(2)(1), cells(2)(2), cells(2)(3),
    cells(3)(0),cells(3)(1), cells(3)(2), cells(3)(3))

  def resetGrid = cells.foreach(f => {
    var col: Double = 0.1
    f.foreach(c => {
      c.resetCell(color.deriveColor(0.0, 1.0, 1 - 0.3*col, 1.0))
      col += 0.2
    })
  })
}