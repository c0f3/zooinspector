package net.c0f3.zookeeper;

import org.apache.zookeeper.inspector.logger.LoggerFactory;

import java.awt.*;

/**
 * @author Konstantin.Lychagin
 * created: 04.09.2018
 */
public class ScreenWrapper {
  public static Dimension getScreenResolution() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] screenDevices = ge.getScreenDevices();

    if (screenDevices == null || screenDevices.length == 0) {
      LoggerFactory.getLogger().error("cannot detect screen resolution");
      return new Dimension(1024, 768);
    }

    DisplayMode dm = screenDevices[0].getDisplayMode();
    int screenWidth = dm.getWidth();
    int screenHeight = dm.getHeight();

    LoggerFactory.getLogger().debug("Cake: " + screenWidth + " " + screenHeight);
    return new Dimension(screenWidth, screenHeight);
  }

  public static Point getWindowPosition(int width, int height) {
    Dimension resolution = getScreenResolution();
    int x = (int) (resolution.getWidth() / 2 - width / 2);
    int y = (int) (resolution.getHeight() / 2 - height / 2);
    return new Point(x, y);
  }
}
