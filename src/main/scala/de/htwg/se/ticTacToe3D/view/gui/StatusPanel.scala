package de.htwg.se.ticTacToe3D.view.gui
import javafx.scene.Group
import scalafx.scene.control.Label
import javafx.scene.paint.Color


class StatusPanel extends Group{
  val statusLabel: Label = new Label("");
  def init() = {
    getChildren.add(statusLabel)
  }
  def setText(text: String) = {
    this.statusLabel.setText(text)
    statusLabel.setTextFill(Color.web("#ffffff"))
  }
  def clear = statusLabel.setText("");
}
