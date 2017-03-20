package at.mep.util;

import java.awt.*;

/** Created by Andreas Justin on 2016-08-29. */
public class ColorUtils {
    public static String colorToHex(Color color) {
        return "#" + Integer.toHexString(color.getRGB()).substring(2);
    }

    public static Color mixColors(Color color1, Color color2, float ratio) {
        float[] rgb1 = color1.getRGBComponents(null);
        float[] rgb2 = color2.getRGBComponents(null);
        float[] rgbResult = new float[3];

        rgbResult[0] = ratio * rgb1[0] + (1.0F - ratio) * rgb2[0];
        rgbResult[1] = ratio * rgb1[1] + (1.0F - ratio) * rgb2[1];
        rgbResult[2] = ratio * rgb1[2] + (1.0F - ratio) * rgb2[2];

        return new Color(rgbResult[0], rgbResult[1], rgbResult[2]);
    }
}
