package at.mep.util;

import java.awt.*;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */
public class ScreenSize {
    public static GraphicsDevice getDefaultScreen() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    public static GraphicsDevice[] getScreens() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }

    public static GraphicsDevice getScreenOfMouse() {
        return MouseInfo.getPointerInfo().getDevice();
    }

    public static Point getScreenCenterOfMouse() {
        GraphicsDevice s = MouseInfo.getPointerInfo().getDevice();
        Rectangle rDisp = s.getDefaultConfiguration().getBounds();

        return new Point((int) rDisp.getCenterX(), (int) rDisp.getCenterY());
    }

    public static int getWidth() {
        return getDefaultScreen().getDisplayMode().getWidth();
    }

    public static int getHeight() {
        return getDefaultScreen().getDisplayMode().getHeight();
    }

    public static Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
    }

    public static Point getCenter(Dimension size) {
        int x = getWidth() / 2 - (int) size.getWidth() / 2;
        int y = getHeight() / 2 - (int) size.getHeight() / 2;
        return new Point(x, y);
    }
}
