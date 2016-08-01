package at.justin.matlab.gui.components;

import at.justin.matlab.util.KeyStrokeUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Andreas Justin on 2016 - 02 - 24.
 */
public class UndecoratedFrame extends JFrame {
    private Point initialClick;

    private static final String CLOSE_ACTION = "closeAction";
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;

    public final AbstractAction closeAction = new AbstractAction(CLOSE_ACTION) {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
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

        // escape hiding window
        String ksStr = KeyStrokeUtil.getKeyText(KeyEvent.VK_ESCAPE,false,false,true);
        getRootPane().getInputMap(IFW).put(KeyStroke.getKeyStroke(ksStr), CLOSE_ACTION);
        getRootPane().getActionMap().put(CLOSE_ACTION, closeAction);

        // mouse listeners
        addMouseListener(mlClick);
        addMouseMotionListener(mlMove);
    }

}