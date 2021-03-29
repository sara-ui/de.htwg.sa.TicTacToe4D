package de.htwg.se.ticTacToe3D.view.gui

import java.util

import de.htwg.se.ticTacToe3D.TicTacToe
import de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl.Messages
import de.htwg.se.ticTacToe3D.util.Observer
import javafx.application.Application
import javafx.stage.Stage
import javafx.scene._
import javafx.geometry.Bounds
import javafx.application.Platform
import javafx.scene.paint.{Color, CycleMethod, RadialGradient, Stop}
import javafx.scene.transform.Shear
import javafx.scene.input._


class GameGui() extends Application with Observer{
  var stage: Stage = null
  var appGroup: Group = new Group()
  var statusPanel: StatusPanel = new StatusPanel
  var camOffset: Cam = new Cam
  var cam: Cam = new Cam
  var scene: Scene = null
  final val shear: Shear = new Shear()
  def init(stage: Stage): Unit = {
    this.stage = stage
    this.statusPanel.init()
    this.statusPanel.setText(Messages.MOVEMENT + TicTacToe.controller.statusMessage)
    camOffset.init
    cam.init
  }

  def setCamPivot(cam: Cam): Unit = {
    val bounds: Bounds = cam.getBoundsInLocal
    val pivotX: Double = bounds.getMinX + bounds.getWidth / 2
    val pivotY: Double = bounds.getMinY + bounds.getHeight / 2
    val pivotZ: Double = bounds.getMinZ + bounds.getDepth / 2
    cam.p.setX(pivotX)
    cam.p.setY(pivotY)
    cam.p.setZ(pivotZ)
    cam.ip.setX(-pivotX)
    cam.ip.setY(-pivotY)
    cam.ip.setZ(-pivotZ)
  }

  def resetCam(): Unit = {
    cam.t.setX(0.0)
    cam.t.setY(0.0)
    cam.t.setZ(0.0)
    cam.s.setX(0.0)
    cam.s.setY(0.0)
    cam.s.setZ(0.0)
    cam.rx.setAngle(27.0)
    cam.ry.setAngle(-92.0)
    cam.rz.setAngle(0.0)
    setCamPivot(cam)
  }

  def setGrids() = {
    val gridsGroup: Group = new Group()
    gridsGroup.getTransforms.add(shear)
    gridsGroup.setDepthTest(DepthTest.ENABLE)
    var position = -30
    var grids: Vector[GridPanel] = Vector.tabulate(4){ (i) => {
      val gridPanel = new GridPanel(1, 1, i, TicTacToe.controller)
      gridPanel.setTranslateX(50)
      gridPanel.setTranslateY(300)
      gridPanel.setTranslateZ(300)
      gridPanel.setTranslateY(position)
      gridPanel.setScaleX(50)
      gridPanel.setScaleZ(50)
      gridPanel.setScaleY(5)
      position += 15
      gridPanel
    }}

    gridsGroup.getChildren.addAll(grids(0), grids(1), grids(2), grids(3))
    gridsGroup.setScaleX(2.5)
    gridsGroup.setScaleY(2.5)
    gridsGroup.setScaleZ(2.5)
    gridsGroup
  }

  def setCamOffset(camOffset: Cam, scene: Scene) = {
    val width: Double = scene.getWidth
    val height: Double = scene.getHeight
    camOffset.t.setX(width / 2.0)
    camOffset.t.setY(height / 2.0)
  }

  def setCamScale(cam: Cam, scene: Scene): Unit = {
    val bounds: Bounds = cam.getBoundsInLocal
    val width: Double = scene.getWidth
    val height: Double = scene.getHeight
    var scaleFactor = 1.0
    var scaleFactorY = 1.0
    var scaleFactorX = 1.0
    if (bounds.getWidth > 0.0001) scaleFactorX = width / bounds.getWidth
    if (bounds.getHeight > 0.0001) scaleFactorY = height / bounds.getHeight
    if (scaleFactorX > scaleFactorY) scaleFactor = scaleFactorY
    else scaleFactor = scaleFactorX
    cam.s.setX(scaleFactor)
    cam.s.setY(scaleFactor)
    cam.s.setZ(scaleFactor)
  }

  def setCamTranslate(cam: Cam): Unit = {
    val bounds = cam.getBoundsInLocal
    val pivotX = bounds.getMinX + bounds.getWidth / 2
    val pivotY = bounds.getMinY + bounds.getHeight / 2
    cam.t.setX(-pivotX)
    cam.t.setY(-pivotY)
  }
  def frameCam(stage: Stage, scene: Scene): Unit = {
    setCamOffset(camOffset, scene)
    setCamPivot(cam)
    setCamTranslate(cam)
    setCamScale(cam, scene)
  }

  override def start(stage: Stage): Unit = {
    this.init(stage)
    TicTacToe.controller.add(this)
    stage.setTitle(Messages.TITLE)
    camOffset.getChildren.add(cam)
    appGroup.getChildren.addAll(camOffset, statusPanel)
    resetCam()
    this.scene = new Scene(appGroup, 800, 600, true)
    val stops = new util.ArrayList[Stop]()
      stops.add(new Stop(0f, Color.ORANGE))
      stops.add(new Stop(1f, Color.LIGHTGREEN))
    scene.setFill(new RadialGradient(225, 0.85, 300,
      300, 500, false, CycleMethod.NO_CYCLE, stops));

    scene.setCamera(new PerspectiveCamera())
    cam.setScaleX(0.5)
    cam.setScaleY(0.5)
    cam.getChildren.addAll(setGrids())
    frameCam(stage, scene)
    scene.setOnKeyPressed((ke: KeyEvent) => {
      if (KeyCode.Q.equals(ke.getCode)) TicTacToe.controller.exit
      if (KeyCode.R.equals(ke.getCode)) TicTacToe.controller.restart
      if (KeyCode.Z.equals(ke.getCode)) TicTacToe.controller.undo
      if (KeyCode.S.equals(ke.getCode)) TicTacToe.controller.save
      if (KeyCode.L.equals(ke.getCode)) TicTacToe.controller.load
      if (KeyCode.Y.equals(ke.getCode)) TicTacToe.controller.redo
      if (KeyCode.RIGHT.equals(ke.getCode)) cam.ry.setAngle(cam.ry.getAngle - 5)
      if (KeyCode.LEFT.equals(ke.getCode)) cam.ry.setAngle(cam.ry.getAngle + 5)
      if (KeyCode.UP.equals(ke.getCode) && cam.rx.getAngle > -8) cam.rx.setAngle(cam.rx.getAngle - 5)
      if (KeyCode.DOWN.equals(ke.getCode) && cam.rx.getAngle < 47) cam.rx.setAngle(cam.rx.getAngle + 5)
    })
    stage.setScene(scene)
    stage.show()
  }

  override def update: Boolean = {
    Platform.runLater(() => {
      cam.getChildren.addAll(setGrids())
      statusPanel.setText(Messages.MOVEMENT + TicTacToe.controller.statusMessage)
    })
    true
  }
}
