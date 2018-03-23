package at.mep.gui.bookmarks;

import at.mep.editor.EditorWrapper;
import at.mep.gui.components.DockableFrame;
import at.mep.gui.components.JTextFieldSearch;
import at.mep.util.KeyStrokeUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Created by Andreas Justin on 2016-08-25. */
public class BookmarksViewer extends DockableFrame {
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static BookmarksViewer instance;
    private static Bookmarks bookmarks = Bookmarks.getInstance();
    private static JList<Object> jList;
    private static JTextFieldSearch jTFS;
    private static JButton jbRename;
    private final AbstractAction enterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectBookmark();
            EditorWrapper.getActiveEditor().getTextComponent().requestFocus();
            if (!isDockable() || isFloating()) {
                setVisible(false);
            }
        }
    };

    private AbstractAction escAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            jTFS.setText("");
            if (!isDockable() || isFloating()) {
                setVisible(false);
            }
            EditorWrapper.getActiveEditor().getTextComponent().requestFocus();
        }
    };

    private AbstractAction updateAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            findPattern(jTFS.getText());
            if (jList.getVisibleRowCount() > 1) {
                jList.setSelectedIndex(1);
            }
        }
    };

    private BookmarksViewer() {
        setLayout();
    }

    public static BookmarksViewer getInstance() {
        if (instance == null) instance = new BookmarksViewer();
        return instance;
    }

    public void showDialog() {
        setVisible(true, EViewer.BOOKMARKS);
        updateList();
    }

    public void updateList() {
        jList.setListData(bookmarks.getBookmarkList().toArray());
    }

    private void setLayout() {
        setLayout(new GridBagLayout());

        createSearchField();
        addToolBar();
        addViewPanel();

        addFocusListener(jTFS);
    }

    @SuppressWarnings("Duplicates")
    private void createSearchField() {
        jTFS = new JTextFieldSearch(30);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 0, 10);
        add(jTFS, gbc);

        jTFS.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAction.actionPerformed(null);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAction.actionPerformed(null);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        KeyStroke ksU = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_UP);
        jTFS.getInputMap(JComponent.WHEN_FOCUSED).put(ksU, "UP");
        jTFS.getActionMap().put("UP", new AbstractAction("UP") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jList.getSelectedIndex(); // single selection
                if (row < 0) {
                    jList.setSelectedIndex(0);
                }
                jList.setSelectedIndex(row - 1); // zero is top, so up means -1
            }
        });

        KeyStroke ksD = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_DOWN);
        jTFS.getInputMap(JComponent.WHEN_FOCUSED).put(ksD, "DOWN");
        jTFS.getActionMap().put("DOWN", new AbstractAction("DOWN") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = jList.getSelectedIndex(); // single selection
                if (row < 0) {
                    jList.setSelectedIndex(0);
                }
                if (bookmarks.getBookmarkList().size() - 1 > row) {
                    jList.setSelectedIndex(row + 1); // zero is top, so down means +1
                }
            }
        });

        KeyStroke ksESC = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ESCAPE);
        jTFS.getInputMap(JComponent.WHEN_FOCUSED).put(ksESC, "ESC");
        jTFS.getActionMap().put("ESC", escAction);
    }

    private void findPattern(String pattern) {
        try {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            jTFS.setForeground(null);
            jList.setListData(bookmarks.filter(p).toArray());
        } catch (PatternSyntaxException e) {
            jTFS.setForeground(Color.RED);
        }
    }

    private void addToolBar() {
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));

        jbRename = new JButton("Rename");
        jbRename.addActionListener(e -> {
            Bookmark bookmark = (Bookmark) jList.getSelectedValue();
            if (bookmark == null) return;
            String name = JOptionPane.showInputDialog(
                    new JFrame(),
                    "Enter Short bookmark description",
                    "Bookmark description",
                    JOptionPane.QUESTION_MESSAGE);
            if (name == null) return;
            bookmark.setName(name);
            updateList();
        });
        jp.add(jbRename);

        JButton jbDelete = new JButton("Remove");
        jbDelete.addActionListener(e -> {
            java.util.List<Object> bookmarkList = jList.getSelectedValuesList();
            for (Object o : bookmarkList) {
                Bookmark bookmark = (Bookmark) o;
                Bookmarks.getInstance().removeBookmark(bookmark);
                Bookmarks.getInstance().setEditorBookmarks(bookmark.getEditor());
            }
            updateList();
        });
        jp.add(jbDelete);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 0, 10);
        add(jp, gbc);
    }

    private void addViewPanel() {
        jList = new JList<>(bookmarks.getBookmarkList().toArray());
        jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setVisibleRowCount(-1);
        jList.setCellRenderer(new BookmarkCellRenderer());
        jList.addListSelectionListener(e -> {
            int[] indices = jList.getSelectedIndices();
            jbRename.setEnabled(indices.length == 1);
        });
        jList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                if (e.getClickCount() > 1 && e.getButton() == 1) {
                    enterAction.actionPerformed(new ActionEvent(e, 0, null));
                }
            }
        });

        KeyStroke ks = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ENTER);
        getInputMap(IFW).put(ks, "ENTER");
        getActionMap().put("ENTER", enterAction);

        JScrollPane jsp = new JScrollPane(jList);
        jsp.getVerticalScrollBar().setUnitIncrement(20);
        jsp.getHorizontalScrollBar().setUnitIncrement(20);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 10, 10, 10);

        add(jsp, gbc);
    }

    private void selectBookmark() {
        Bookmark bookmark = (Bookmark) jList.getSelectedValue();
        if (bookmark == null) return;
        bookmark.goTo();
    }
}

/*

e = at.mep.editor.EditorWrapper.getEditorSyntaxTextPane
i = e.getInputMap(0)
a = e.getActionMap()

ak = a.allKeys();
ik = i.allKeys();
for ii = 1:numel(ak)
    aks(ii) = string(ak(ii));
    ik(ii)
end
idx = aks == "MEP_SHOW_BOOKMARKS";
ik(idx)
 */