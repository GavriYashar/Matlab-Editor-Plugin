package at.mep.util;

import java.awt.*;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */
public class ScreenSize {
    private static final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    public static int getWidth() {
        return gd.getDisplayMode().getWidth();
    }

    public static int getHeight() {
        return gd.getDisplayMode().getHeight();
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
