package at.justin.matlab.Clipboard;

import at.justin.matlab.util.ScreenSize;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */
public class Clipboard {
    private static Clipboard INSTANCE;
    public final JFrame jFrame = new JFrame();
    public JList jList;
    private Point initialClick;
    private final KeyListener keyListener = new KeyListener() {
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

    private Clipboard() {
        create();
        jFrame.setVisible(true);
    }

    public void setVisible(boolean visible) {
        jFrame.setVisible(visible);
    }

    public static Clipboard getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new Clipboard();
        return INSTANCE;
    }

    private void create() {
        int width = ScreenSize.getWidth();
        int height = ScreenSize.getHeight();

        jFrame.setUndecorated(true);
        jFrame.setSize(300, 600);
        jFrame.setLocation(width/2 - jFrame.getWidth()/2,height/2 - jFrame.getHeight()/2);

        jList = new JList();
        jFrame.add(jList, BorderLayout.NORTH);

        jFrame.addKeyListener(keyListener);
        jList.addKeyListener(keyListener);
        addWindowMover();
    }

    private void addWindowMover() {
        jFrame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                jFrame.getComponentAt(initialClick);
            }
        });

        jFrame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = jFrame.getLocation().x;
                int thisY = jFrame.getLocation().y;

                int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                jFrame.setLocation(X, Y);
            }
        });
    }
}
