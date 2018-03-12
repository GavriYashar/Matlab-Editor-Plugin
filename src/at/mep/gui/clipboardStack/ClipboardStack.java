package at.mep.gui.clipboardStack;

import at.mep.CommandWindow;
import at.mep.editor.EditorWrapper;
import at.mep.gui.components.UndecoratedFrame;
import at.mep.prefs.Settings;
import at.mep.util.ClipboardUtil;
import at.mep.util.KeyStrokeUtil;
import at.mep.util.RunnableUtil;
import at.mep.util.ScreenSize;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

/** Created by Andreas Justin on 2016 - 02 - 09. */
public class ClipboardStack extends UndecoratedFrame {
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static final String ENTER_ACTION = "ENTER";
    private static ClipboardStack INSTANCE;
    private JList<String> jList;
    private JTextArea jTextArea;
    private String[] strings = new String[Settings.getPropertyInt("clipboardStack.size")];
    private EClipboardParent eClipboardParent = EClipboardParent.INVALID;

    private ClipboardStack() {
        Runnable runnable = () -> {
            create();
            setVisible(false);
        };
        RunnableUtil.invokeInDispatchThreadIfNeeded(runnable);
    }

    @Override
    protected void storeDimension(Dimension dimension) {
        Settings.setPropertyDimension("dim.clipboardStackViewer", dimension);
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static ClipboardStack getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new ClipboardStack();
        return INSTANCE;
    }

    public void add(final String string) {
        // if already added skip adding given string
        for (String s : strings) {
            if (s == null) break;
            if (s.equals(string)) return;
        }

        // first string is the new one, old ones will be moved one down in the list
        String[] newStrings = new String[Settings.getPropertyInt("clipboardStack.size")];
        newStrings[0] = string;
        System.arraycopy(strings, 0, newStrings, 1, strings.length - 1);
        strings = newStrings;
        jList.setListData(strings);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (jList.getSelectedIndex() < 0) {
            jList.setSelectedIndex(0);
        }
    }

    private void create() {
        setTitle("ClipboardStack");
        setSize(Settings.getPropertyDimension("dim.clipboardStackViewer"));
        setResizable(true);
        setLocation(ScreenSize.getCenter(getSize()));
        setLayout(new GridBagLayout());

        {
            jList = new JList<>();
            jList.setBackground(getBackground());
            jList.setCellRenderer(new ClipboardCellRenderer());
            JScrollPane jsp = new JScrollPane(jList);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.weighty = 0.5;
            gbc.fill = GridBagConstraints.BOTH;
            add(jsp, gbc);
        }

        {
            jTextArea = new JTextArea();
            jTextArea.setEditable(false);
            jTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(jTextArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 1;
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.weighty = 0.5;
            gbc.fill = GridBagConstraints.BOTH;
            add(scrollPane,gbc);
        }
        jList.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0, 0), 10));

        addListeners();
    }

    private void addListeners() {
        addMouseListener(mlClick);
        addMouseMotionListener(mlMove);
        jList.addMouseListener(mlClick);
        jList.addMouseMotionListener(mlMove);

        MouseAdapter mlClick = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() > 1) {
                    insertSelectedText();
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

        KeyStroke ksU = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ENTER);
        jList.getInputMap(IFW).put(ksU, ENTER_ACTION);
        jList.getActionMap().put(ENTER_ACTION, new AbstractAction(ENTER_ACTION) {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertSelectedText();
            }
        });
    }

    public void moveTextToPosition(int sourceIdx, int targetIdx) {
        String target = strings[targetIdx];
        strings[targetIdx] = strings[sourceIdx];
        strings[sourceIdx] = target;
        jList.setListData(strings);
        jList.repaint();
    }

    private void insertSelectedText() {
        String txtToInsert = strings[jList.getSelectedIndex()];
        ClipboardUtil.addToClipboard(txtToInsert);

        switch (eClipboardParent) {
            case INVALID:
                System.out.println("MEP: ClipboardStack has no parent; EClipboardParent.INVALID");
                break;
            case EDITOR:
                EditorWrapper.setSelectedTxt(txtToInsert);
                moveTextToPosition(jList.getSelectedIndex(), 0);
                break;
            case COMMAND:
                CommandWindow.insertTextAtPos(txtToInsert, CommandWindow.getCaretPosition());
                break;
        }
        ClipboardStack.getInstance().setVisible(false);
    }

    public void setEClipboardParent(EClipboardParent eClipboardParent) {
        this.eClipboardParent = eClipboardParent;
    }
}
