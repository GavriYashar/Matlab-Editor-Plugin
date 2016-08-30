package at.justin.matlab.util;

import java.awt.*;

/**
 * Created by Andreas Justin on 2016-08-29.
 */
public class ColorUtils {
    public static String colorToHex(Color color) {
        return "#" + Integer.toHexString(color.getRGB()).substring(2);
    }
}
