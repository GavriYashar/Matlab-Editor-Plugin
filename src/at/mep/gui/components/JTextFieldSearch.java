package at.mep.gui.components;

import at.mep.gui.EIcons;

import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by z0032f1t on 28.07.2016.
 */
public class JTextFieldSearch extends JTextField {
    private static final long serialVersionUID = 1L;
    private final boolean isRounded;

    public JTextFieldSearch(int size) {
        super(size);
        this.isRounded = false;
        addClearButtonListener();
    }

    public JTextFieldSearch(int size, boolean isRounded) {
        super(size);
        this.isRounded = isRounded;
        if (isRounded) {
            // set to transparent and leave 5px space before the text input
            setMargin(new Insets(0, 5, 0, 5));
            setOpaque(false);
        }
        addClearButtonListener();
    }

    private void addClearButtonListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (e.getX() > getWidth() - 20)
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                else
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getX() > getWidth() - 20) {
                    setText("");
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        // paint rounded rectangle if isRounded
        Graphics2D g1 = (Graphics2D) g.create();
        if (isRounded) {
            g1.setColor(getBackground());
            g1.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
            //repaint();
        }
        super.paintComponent(g1);

        // paint string "Search ..." and search icon if empty and not-focused
        if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.lightGray);
            g2.setFont(getFont().deriveFont(Font.BOLD));
            EIcons.SEARCH_15.getIcon().paintIcon(this, g, this.getWidth() - 21, 3);
            g2.drawString(" Search...", 7, 15);
            g2.dispose();
            //repaint();
        }

        // set listener for emptying the search bar on click to the right end of the field
        if (!getText().isEmpty()) {
            EIcons.TOOLBAR_DELETE_16.getIcon().paintIcon(this, g, this.getWidth() - 17, 2);
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g1 = (Graphics2D) g.create();
        if (isRounded) {
            g1.setColor(getBackground());
            g1.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);
        } else
            super.paintBorder(g1);
    }
}
