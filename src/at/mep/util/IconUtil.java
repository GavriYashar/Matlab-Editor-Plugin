package at.mep.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Andreas Justin on 2016-08-25.
 */
public class IconUtil {

    public static Icon printTextOnIcon(final Icon icon, String text, int x, int y, Font font, Color color) {
        Image img = getImage(icon);
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        bGr.drawImage(img, 0, 0, null);
        bGr.setColor(color);
        bGr.setFont(font);
        bGr.drawString(text, x, y);
        bGr.dispose();

        return new ImageIcon(bimage);
    }

    public static Icon decorateIcon(final Icon icon, final Icon decorator, EIconDecorator location) {
        Image img = getImage(icon);
        Image imgDecorator = getImage(decorator);
        BufferedImage bimage;
        if (location.getLocation() < 360) {
            bimage = new BufferedImage(
                    img.getWidth(null),
                    img.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
        } else {
            bimage = getBufferedImageForLocation(img, imgDecorator, location);
        }

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        int[] xy = getXYForIcon(icon, decorator, location, 0, 0);
        int[] xyD = getXYForIconDecorator(icon, decorator, location, 0, 0);
        bGr.drawImage(img, xy[0], xy[1], null);
        bGr.drawImage(imgDecorator, xyD[0], xyD[1], null);
        bGr.dispose();

        return new ImageIcon(bimage);
    }

    /** Creates a new instance of the given icon painted in given color */
    public static Icon color(final Icon icon, Color color, float alpha) {
        final BufferedImage mask = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gbi = (Graphics2D) mask.getGraphics();
        icon.paintIcon(new JLabel(), gbi, 0, 0);
        gbi.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));
        gbi.setColor(color);
        gbi.fillRect(0, 0, mask.getWidth() - 1, mask.getHeight() - 1);

        Icon coloredIcon = new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                icon.paintIcon(c, g, x, y);
                g.drawImage(mask, x, y, c);
            }

            @Override
            public int getIconWidth() {
                return icon.getIconWidth();
            }

            @Override
            public int getIconHeight() {
                return icon.getIconHeight();
            }
        };
        return coloredIcon;
    }

    /** Creates a new instance of the given icon painted in given color */
    public static Icon color(final Icon icon, Color color) {
        return color(icon, color, 1);
    }

    public static Icon scale(final Icon icon, int width, int height) {
        Image img = getImage(icon);
        Image newimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    public static Icon scaleHeight(final Icon icon, int height, boolean lockAspectRatio) {
        if (height <= 0)
            return icon;

        Image img = getImage(icon);
        Image newImg;
        if (lockAspectRatio) {
            double ratio = (double) icon.getIconWidth() / (double) icon.getIconHeight();
            newImg = img.getScaledInstance((int) (height * ratio), height, Image.SCALE_SMOOTH);
        } else
            newImg = img.getScaledInstance(icon.getIconWidth(), height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    public static Icon scaleWidth(final Icon icon, int width, boolean lockAspectRatio) {
        if (width <= 0)
            return icon;

        Image img = getImage(icon);
        Image newImg;
        if (lockAspectRatio) {
            double ratio = (double) icon.getIconWidth() / (double) icon.getIconHeight();
            newImg = img.getScaledInstance(width, (int) (width / ratio), Image.SCALE_SMOOTH);
        } else
            newImg = img.getScaledInstance(width, icon.getIconHeight(), Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    public static Icon deepCopy(final Icon icon) {
        return new ImageIcon(getImage(icon));
    }

    private static Image getImage(Icon icon) {
        if (icon instanceof ImageIcon)
            return ((ImageIcon) icon).getImage();

        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D gbi = (Graphics2D) bi.getGraphics();
        icon.paintIcon(new JLabel(), gbi, 0, 0);
        return bi;
    }

    /** the basic icon has to be moved accordingly where the location of the decorator is. */
    private static int[] getXYForIcon(Icon icon, Icon decorator, EIconDecorator location, int xOffset, int yOffset) {
        int x = 0;
        int y = 0;
        switch (location) {
            case NORTH_EAST_INSIDE:
                break;
            case SOUTH_EAST_INSIDE:
                break;
            case SOUTH_WEST_INSIDE:
                break;
            case NORTH_WEST_INSIDE:
                break;
            case NORTH_OUTSIDE:
                x = 0;
                y = decorator.getIconHeight();
                break;
            case EAST_OUTSIDE:
                break;
            case SOUTH_OUTSIDE:
                break;
            case WEST_OUTSIDE:
                x = decorator.getIconWidth();
                y = 0;
                break;
        }
        return new int[]{x + xOffset, y + yOffset};

    }

    private static int[] getXYForIconDecorator(Icon icon, Icon decorator, EIconDecorator location, int xOffset, int yOffset) {
        int x = 0;
        int y = 0;
        switch (location) {
            case NORTH_EAST_INSIDE:
                x = icon.getIconWidth() - decorator.getIconWidth();
                y = 0;
                break;
            case SOUTH_EAST_INSIDE:
                x = icon.getIconWidth() - decorator.getIconWidth();
                y = icon.getIconHeight() - decorator.getIconHeight();
                break;
            case SOUTH_WEST_INSIDE:
                x = 0;
                y = icon.getIconHeight() - decorator.getIconHeight();
                break;
            case NORTH_WEST_INSIDE:
                x = 0;
                y = 0;
                break;
            case NORTH_OUTSIDE:
                x = (icon.getIconWidth() - decorator.getIconWidth()) / 2;
                y = -decorator.getIconHeight();
                break;
            case EAST_OUTSIDE:
                x = icon.getIconWidth();
                y = -(decorator.getIconHeight() - decorator.getIconHeight()) / 2;
                break;
            case SOUTH_OUTSIDE:
                x = (icon.getIconWidth() - decorator.getIconWidth()) / 2;
                y = icon.getIconHeight();
                break;
            case WEST_OUTSIDE:
                x = -decorator.getIconWidth();
                y = -(decorator.getIconHeight() - decorator.getIconHeight()) / 2;
                break;
        }
        return new int[]{x + xOffset, y + yOffset};
    }

    private static BufferedImage getBufferedImageForLocation(Image img, Image imgDecorator, EIconDecorator location) {
        BufferedImage b = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        switch (location) {
            case NORTH_EAST_INSIDE:
                break;
            case SOUTH_EAST_INSIDE:
                break;
            case SOUTH_WEST_INSIDE:
                break;
            case NORTH_WEST_INSIDE:
                break;
            case NORTH_OUTSIDE:
                b = new BufferedImage(
                        img.getWidth(null),
                        img.getHeight(null) + imgDecorator.getWidth(null),
                        BufferedImage.TYPE_INT_ARGB);
                break;
            case EAST_OUTSIDE:
                b = new BufferedImage(
                        img.getWidth(null) + imgDecorator.getWidth(null),
                        img.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                break;
            case SOUTH_OUTSIDE:
                b = new BufferedImage(
                        img.getWidth(null),
                        img.getHeight(null) + imgDecorator.getWidth(null),
                        BufferedImage.TYPE_INT_ARGB);
                break;
            case WEST_OUTSIDE:
                b = new BufferedImage(
                        img.getWidth(null) + imgDecorator.getWidth(null),
                        img.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);
                break;
        }
        return b;
    }
}
