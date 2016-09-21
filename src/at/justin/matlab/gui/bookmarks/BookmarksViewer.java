package at.justin.matlab.gui.bookmarks;

import at.justin.matlab.gui.components.JTextFieldSearch;
import at.justin.matlab.gui.components.UndecoratedFrame;
import at.justin.matlab.util.KeyStrokeUtil;
import at.justin.matlab.util.ScreenSize;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

/** Created by Andreas Justin on 2016-08-25. */
public class BookmarksViewer extends UndecoratedFrame {

    private static final String ENTER_ACTION = "enterAction";
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static BookmarksViewer INSTANCE;
    private static Dimension dimension = new Dimension(600, 400);
    private static Bookmarks bookmarks = Bookmarks.getInstance();
    private static JList<Object> jList;
    private static JButton jbRename;
    private final AbstractAction enterAction = new AbstractAction(ENTER_ACTION) {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectBookmark();
        }
    };

    private BookmarksViewer() {
        setLayout();
    }

    public static BookmarksViewer getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new BookmarksViewer();
        return INSTANCE;
    }

    public void showDialog() {
        this.setVisible(true);
        this.setLocation(ScreenSize.getCenter(this.getSize()));
        updateList();
    }

    public void updateList() {
        jList.setListData(bookmarks.getBookmarkList().toArray());
    }

    private void setLayout() {
        setTitle("BookmarksViewer");
        setResizable(true);
        setSize(dimension);
        setPreferredSize(dimension);
        rootPane.setLayout(new GridBagLayout());

        addSearchBar();
        addToolBar();
        addViewPanel();
    }

    private void addSearchBar() {
        JTextFieldSearch jtfs = new JTextFieldSearch(20);
        jtfs.setEnabled(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 0, 10);
        rootPane.add(jtfs, gbc);
    }

    private void addToolBar() {
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));

        jbRename = new JButton("Rename");
        jbRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });
        jp.add(jbRename);

        JButton jbDelete = new JButton("Remove");
        jbDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.List<Object> bookmarkList = jList.getSelectedValuesList();
                for (Object o : bookmarkList) {
                    Bookmark bookmark = (Bookmark) o;
                    Bookmarks.getInstance().removeBookmark(bookmark);
                    Bookmarks.getInstance().setEditorBookmarks(bookmark.getEditor());
                }
                updateList();
            }
        });
        jp.add(jbDelete);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 0, 10);
        rootPane.add(jp, gbc);
    }

    private void addViewPanel() {
        jList = new JList<>(bookmarks.getBookmarkList().toArray());
        jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setVisibleRowCount(-1);
        jList.setCellRenderer(new BookmarkCellRenderer());
        jList.addMouseListener(mlClick);
        jList.addMouseMotionListener(mlMove);
        jList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) selectBookmark();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        jList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                int[] indices = jList.getSelectedIndices();
                jbRename.setEnabled(indices.length == 1);
            }
        });

        KeyStroke ks = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ENTER);
        getRootPane().getInputMap(IFW).put(ks, ENTER_ACTION);
        getRootPane().getActionMap().put(ENTER_ACTION, enterAction);

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

        rootPane.add(jsp, gbc);
    }

    private void selectBookmark() {
        Bookmark bookmark = (Bookmark) jList.getSelectedValue();
        if (bookmark == null) return;
        bookmark.goTo();
        setVisible(false);
    }
}
