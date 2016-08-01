package at.justin.matlab.clipboardStack;

import at.justin.matlab.EditorWrapper;
import at.justin.matlab.util.ScreenSize;
import at.justin.matlab.gui.components.UndecoratedFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */
public class ClipboardStack {
    private static ClipboardStack INSTANCE;
    public final UndecoratedFrame undecoratedFrame = new UndecoratedFrame();
    public JList jList;
    private DefaultListModel<String> stringListModel;
    public JTextArea jTextArea;

    private String[] strings = new String[10];

    private ClipboardStack() {
        create();
        undecoratedFrame.setVisible(false);
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
        undecoratedFrame.setVisible(visible);
        undecoratedFrame.setAlwaysOnTop (visible);
    }

    public static ClipboardStack getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new ClipboardStack();
        return INSTANCE;
    }

    private void create() {
        int width = ScreenSize.getWidth();
        int height = ScreenSize.getHeight();

        undecoratedFrame.setUndecorated(true);
        undecoratedFrame.setSize(300, 600);
        undecoratedFrame.setLocation(width/2 - undecoratedFrame.getWidth()/2,height/2 - undecoratedFrame.getHeight()/2);

        jList = new JList();
        jList.setBackground(undecoratedFrame.getBackground());
        stringListModel = new DefaultListModel<>();
        jList.setModel(stringListModel);
        jTextArea = new JTextArea();
        jTextArea.setEditable(false);
        jTextArea.setFont(new Font(Font.MONOSPACED,Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(jTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridLayout layout = new GridLayout(2, 3, 10, 20);
        undecoratedFrame.setLayout(layout);
        undecoratedFrame.add(jList);
        undecoratedFrame.add(scrollPane);
        jList.setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0),10));

        addListeners();
    }

    private void addListeners() {
        undecoratedFrame.addMouseListener(undecoratedFrame.mlClick);
        undecoratedFrame.addMouseMotionListener(undecoratedFrame.mlMove);
        jList.addMouseListener(undecoratedFrame.mlClick);
        jList.addMouseMotionListener(undecoratedFrame.mlMove);

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

        // Text Selection listener
        jList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int index = jList.getSelectedIndex();
                if (index < 0) return;
                jTextArea.setText(strings[index]);
            }
        });

        jList.addMouseListener(mlClick);
    }
}