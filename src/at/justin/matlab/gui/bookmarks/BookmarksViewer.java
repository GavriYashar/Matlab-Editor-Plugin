package at.justin.matlab.gui.bookmarks;

import at.justin.matlab.gui.components.JTextFieldSearch;
import at.justin.matlab.gui.components.UndecoratedFrame;
import at.justin.matlab.util.KeyStrokeUtil;
import at.justin.matlab.util.ScreenSize;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Andreas Justin on 2016-08-25.
 */
public class BookmarksViewer extends UndecoratedFrame {

    private static final String ENTER_ACTION = "enterAction";
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    public final AbstractAction enterAction = new AbstractAction(ENTER_ACTION) {
        @Override
        public void actionPerformed(ActionEvent e) {
            Bookmark bookmark = (Bookmark) jList.getSelectedValue();
            if (bookmark == null) return;
            bookmark.goTo();
        }
    };

    private static BookmarksViewer INSTANCE;
    private static Dimension dimension = new Dimension(600, 400);
    private static Bookmarks bookmarks = Bookmarks.getInstance();
    private static JList<Object> jList;

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

        JButton jbName = new JButton("Rename");
        jbName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Bookmark bookmark = (Bookmark) jList.getSelectedValue();
                if (bookmark == null) return;
                String name = JOptionPane.showInputDialog(
                        new JFrame(),
                        "Enter Short bookmark description",
                        "Bookmark Descirption",
                        JOptionPane.QUESTION_MESSAGE);
                if (name == null) return;
                bookmark.setName(name);
                updateList();
            }
        });
        jp.add(jbName);

        JButton jbDelete = new JButton("Remove");
        jbDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Bookmark bookmark = (Bookmark) jList.getSelectedValue();
                if (bookmark == null) return;
                Bookmarks.getInstance().removeBookmark(bookmark);
                Bookmarks.getInstance().setEditorBookmarks(bookmark.getEditor());
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
        jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setVisibleRowCount(-1);
        jList.setCellRenderer(new BookmarkCellRenderer());
        jList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() < 2) return;
                Bookmark bookmark = (Bookmark) jList.getSelectedValue();
                if (bookmark == null) return;
                bookmark.goTo();
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
}
