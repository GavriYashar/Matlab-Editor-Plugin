package at.justin.matlab.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Andreas Justin on 2016 - 02 - 24.
 */
public class UndecoratedFrame extends JFrame {
    private Point initialClick;

    public final KeyListener closeListener = new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) { }
        @Override
        public void keyPressed(KeyEvent e) { }
        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                setVisible(false);
            }
        }
    };

    // drag listener
    public final MouseAdapter mlClick = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
            initialClick = e.getPoint();
            getComponentAt(initialClick);
        }
    };

    public final MouseMotionListener mlMove = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            int thisX = getLocation().x;
            int thisY = getLocation().y;

            int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
            int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

            int X = thisX + xMoved;
            int Y = thisY + yMoved;
            setLocation(X, Y);
        }
    };

    public UndecoratedFrame() {
        setUndecorated(true);
        addKeyListener(closeListener);
        addMouseListener(mlClick);
        addMouseMotionListener(mlMove);
    }


}