package at.justin.matlab.ClipboardStack;

import at.justin.matlab.EditorWrapper;
import at.justin.matlab.util.ScreenSize;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */
public class ClipboardStack {
    private static ClipboardStack INSTANCE;
    public final JFrame jFrame = new JFrame();
    public JList jList;
    private DefaultListModel<String> stringListModel;
    public JTextArea jTextArea;

    private String[] strings = new String[10];
    private Point initialClick;

    private ClipboardStack() {
        create();
        jFrame.setVisible(false);
    }

    public void add(final String string) {
        // if already added skip adding given string
        for (String s : strings) {
            if (s == null) break;
            if (s.equals(string)) return;
        }

        // first string is the new one, old ones will be moved one down in the list
        String[] newStrings = new String[10];
        newStrings[0] = string;
        for (int i = 1; i < strings.length; i++) {
            newStrings[i] = strings[i-1];
        }
        strings = newStrings;

        stringListModel.removeAllElements();
        for (String s: strings) {
            if (s == null) break;
            if (s.length() > 50) s = s.substring(0,49);
            stringListModel.addElement(s);
        }
    }

    public void setVisible(boolean visible) {
        jFrame.setVisible(visible);
        jFrame.setAlwaysOnTop (visible);
    }

    public static ClipboardStack getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new ClipboardStack();
        return INSTANCE;
    }

    private void create() {
        int width = ScreenSize.getWidth();
        int height = ScreenSize.getHeight();

        jFrame.setUndecorated(true);
        jFrame.setSize(300, 600);
        jFrame.setLocation(width/2 - jFrame.getWidth()/2,height/2 - jFrame.getHeight()/2);

        jList = new JList();
        jList.setBackground(jFrame.getBackground());
        stringListModel = new DefaultListModel<>();
        jList.setModel(stringListModel);
        jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        jTextArea.setFont(new Font(Font.MONOSPACED,Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(jTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridLayout layout = new GridLayout(2, 3, 10, 20);
        jFrame.setLayout(layout);
        jFrame.add(jList);
        jFrame.add(scrollPane);
        jList.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0),10));

        addListeners();
    }

    private void addListeners() {
        KeyListener keyListener = new KeyListener() {
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

        // Text Selection listener
        jList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = jList.getSelectedIndex();
                if (index < 0) return;
                jTextArea.setText(strings[index]);
            }
        });
        MouseAdapter mlClick = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() > 1) {
                    EditorWrapper.getInstance().setSelectedTxt(strings[jList.getSelectedIndex()]);
                    ClipboardStack.getInstance().setVisible(false);
                }
            }
        };

        // drag listener
        MouseAdapter mlMover = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                jFrame.getComponentAt(initialClick);
            }
        };
        MouseMotionListener mml = new MouseMotionAdapter() {
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
        };

        jFrame.addKeyListener(keyListener);
        jList.addKeyListener(keyListener);
        jTextArea.addKeyListener(keyListener);

        jFrame.addMouseListener(mlMover);
        jFrame.addMouseMotionListener(mml);
        jList.addMouseListener(mlMover);
        jList.addMouseListener(mlClick);
        jList.addMouseMotionListener(mml);
    }
}
