package org.apache.zookeeper.inspector.gui;

import javax.swing.*;
import java.awt.*;

/**
 * @author KostaPC
 * created: 06.12.2018
 */
public class HostStatusLabel extends JLabel {

  private static final String defaultText = "not connected";

  public HostStatusLabel() {
    super(defaultText);
    this.setMinimumSize(new Dimension(300, 10));
    this.setForeground(Color.gray);
  }

  public void connected(String host) {
    this.setText(host);
    this.setForeground(new Color(9, 137, 0));
  }

  public void disconnected() {
    this.setText("disconnected from " + this.getText());
    this.setForeground(Color.RED);
  }
}
