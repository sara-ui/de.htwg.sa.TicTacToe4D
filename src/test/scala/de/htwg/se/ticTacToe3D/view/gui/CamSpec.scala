package de.htwg.se.ticTacToe3D.view.gui

import org.scalatest.{Matchers, WordSpec}

class CamSpec extends WordSpec with Matchers {

  "A Cam" should {

    "should be initialized" in {
      val cam: Cam = new Cam
      cam.init
    }
  }
}
