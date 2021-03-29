package de.htwg.se.ticTacToe3D.view.gui

import java.awt.{BorderLayout, Dimension, GridLayout}

import de.htwg.se.ticTacToe3D.controller.controllerComponent.ControllerInterface
import de.htwg.se.ticTacToe3D.controller.controllerComponent.controllerBaseImpl.Messages
import de.htwg.se.ticTacToe3D.util.Observer
import javafx.application.Application
import javax.swing._

class TicTacToeGUI(controller: ControllerInterface) extends JFrame with Observer{
  setTitle(Messages.TITLE)
  setLocationRelativeTo(null)
  controller.add(this)

  // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setMinimumSize(new Dimension(600, 600))

  def constructTicTacToePane(controller: ControllerInterface) = {
    getContentPane.add(getLoginPanel(controller), BorderLayout.CENTER)
    setVisible(true)
  }

  def getLoginPanel(controller: ControllerInterface): JPanel = {
    val start = new JButton("Start")
    val load = new JButton("load")
    val player1 = new JTextField
    val user1 = new JLabel("First Player")
    val user2 = new JLabel("Second Player")
    val player2 = new JTextField
    start.addActionListener((e: _root_.java.awt.event.ActionEvent) => {
        if (!"".equals(player1.getText().trim()) && !"".equals(player2.getText().trim())) {
          controller.setPlayers(player1.getText(), player2.getText())
          this.dispose()
          Application.launch(classOf[GameGui])
        } else {
          JOptionPane.showMessageDialog(null, Messages.USER_ERROR)
        }
    })
    load.addActionListener((e: _root_.java.awt.event.ActionEvent) => {
        controller.load
    })
    val info = new JLabel("<html><div WIDTH=285>" + controller.statusMessage + "</div></html>")
    val panel = new JPanel
    panel.setLayout(new GridLayout(4, 2))
    panel.add(new JLabel("Load Game"))
    panel.add(load)
    panel.add(user1)
    panel.add(player1)
    panel.add(user2)
    panel.add(player2)
    panel.add(start)
    val mainPanel = new JPanel
    mainPanel.setLayout(new GridLayout(2, 1))
    mainPanel.add(info)
    mainPanel.add(panel)
    mainPanel
  }

  constructTicTacToePane(controller)

  override def update: Boolean = {
    if (!controller.game.players.contains(null) && !"".equals(controller.game.players(0).name)) {
      controller.remove(this)
      this.dispose()
      Application.launch(classOf[GameGui])
    }
    true
  }
}
