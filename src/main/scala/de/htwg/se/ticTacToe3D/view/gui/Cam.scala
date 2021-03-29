package de.htwg.se.ticTacToe3D.view.gui
import javafx.scene.Group
import javafx.scene.transform.{Rotate, Scale, Translate}


class Cam extends Group{

  val t: Translate = new Translate()
  val p: Translate = new Translate()
  val ip: Translate = new Translate()
  val rx: Rotate = new Rotate()
  val ry: Rotate = new Rotate()
  val rz: Rotate = new Rotate()
  val s: Scale = new Scale()

  def init = {
    rx.setAxis(Rotate.X_AXIS)
    ry.setAxis(Rotate.Y_AXIS)
    rz.setAxis(Rotate.Z_AXIS)
    this.getTransforms.addAll(t, p, rx, rz, ry, s, ip)
  }

}
