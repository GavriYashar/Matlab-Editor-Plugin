package at.mep.gui.components;

import at.mep.util.KeyStrokeUtil;
import at.mep.util.ScreenSize;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** Created by Andreas Justin on 2016 - 02 - 24. */
public abstract class UndecoratedFrame extends JFrame {
    private static final String CLOSE_ACTION = "closeAction";
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    public final AbstractAction closeAction = new AbstractAction(CLOSE_ACTION) {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    };

    private boolean hideWhenFocusLost = true;
    public final WindowFocusListener focusLostHide = new WindowAdapter() {
        @Override
        public void windowLostFocus(WindowEvent e) {
            if (hideWhenFocusLost){
                setVisible(false);
            }
        }
    };
    private Point initialClick;
    private Dimension initialDimension;
    // drag listener
    public final MouseAdapter mlClick = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            initialClick = e.getPoint();
            initialDimension = getSize();
            getComponentAt(initialClick);
        }
    };
    private boolean resizing = false;
    public final MouseMotionListener mlMove = new MouseMotionAdapter() {
        @Override
        public void mouseDragged(MouseEvent e) {
            int thisX = getLocation().x;
            int thisY = getLocation().y;

            int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
            int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

            if (!resizing) {
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            } else if (isResizable()) {
                int newWidth = (int) initialDimension.getWidth() + xMoved;
                int newHeight = (int) initialDimension.getHeight() + yMoved;
                Dimension minSize = getMinimumSize();
                Dimension maxSize = getMaximumSize();

                if (newWidth < minSize.getWidth()) newWidth = (int) minSize.getWidth();
                if (newWidth > maxSize.getWidth()) newWidth = (int) maxSize.getWidth();
                if (newHeight < minSize.getHeight()) newHeight = (int) minSize.getHeight();
                if (newHeight > maxSize.getHeight()) newHeight = (int) maxSize.getHeight();

                Dimension dimension = new Dimension(newWidth, newHeight);
                setSize(dimension);
                storeDimension(dimension);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (isResizable()) {
                if (e.getX() > getWidth() - 10 && e.getY() > getHeight() - 10) {
                    setCursor(new Cursor(Cursor.SE_RESIZE_CURSOR));
                    resizing = true;
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    resizing = false;
                }
            }
        }
    };

    public UndecoratedFrame() {
        setUndecorated(true);

        // escape hiding window
        KeyStroke ks = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ESCAPE, false, false, false, false, true);
        getRootPane().getInputMap(IFW).put(ks, CLOSE_ACTION);
        getRootPane().getActionMap().put(CLOSE_ACTION, closeAction);

        // mouse listeners
        addMouseListener(mlClick);
        addMouseMotionListener(mlMove);

        setMinimumSize(new Dimension(282, 200));
        setMaximumSize(ScreenSize.getSize());

        // losing focus hiding window
        addWindowFocusListener(focusLostHide);
    }

    protected abstract void storeDimension(Dimension dimension);

    @Override
    public void setResizable(boolean resizable) {
        super.setResizable(resizable);
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (isResizable()) {
            int[] x = {getWidth() - 10, getWidth(), getWidth(), getWidth() - 10};
            int[] y = {getHeight(), getHeight() - 10, getHeight(), getHeight()};
            g.setColor(getBackground().darker());
            g.fillPolygon(x, y, x.length);
        }
    }

    public void hideWhenFocusLost(boolean bool) {
        hideWhenFocusLost = bool;
    }
}
