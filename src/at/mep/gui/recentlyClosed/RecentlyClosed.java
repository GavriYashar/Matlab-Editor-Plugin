package at.mep.gui.recentlyClosed;

import at.mep.editor.EditorWrapper;
import at.mep.gui.components.JTextFieldSearch;
import at.mep.gui.components.UndecoratedFrame;
import at.mep.util.KeyStrokeUtil;
import at.mep.util.RunnableUtil;
import at.mep.util.ScreenSize;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** Created by Andreas Justin on 2017-10-11. */
public class RecentlyClosed extends UndecoratedFrame {
    private static final String ENTER_ACTION = "enterAction";
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static RecentlyClosed INSTANCE;
    private static Dimension dimension = new Dimension(600, 400);
    private static List<File> fileList = new ArrayList<>(20);
    private static JList<Object> jList;
    private final AbstractAction enterAction = new AbstractAction(ENTER_ACTION) {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectFile();
        }
    };

    private RecentlyClosed() {
        Runnable runnable = new Runnable() {
            public void run() {
                setLayout();
            }
        };
        RunnableUtil.invokeInDispatchThreadIfNeeded(runnable);
    }

    public static RecentlyClosed getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new RecentlyClosed();
        return INSTANCE;
    }

    public static void addFile(File file) {
        if (!file.exists()) return;
        fileList.add(file);
    }

    public static void remFile(File file) {
        fileList.remove(file);
    }

    public void showDialog() {
        this.setVisible(true);
        this.setLocation(ScreenSize.getCenter(this.getSize()));
        updateList();
    }

    public void updateList() {
        jList.setListData(fileList.toArray());
    }

    private void setLayout() {
        setTitle("Recently Closed");
        setResizable(true);
        setSize(dimension);
        setPreferredSize(dimension);
        rootPane.setLayout(new GridBagLayout());

        addSearchBar();
        addToolBar();
        addViewPanel();
    }

    @SuppressWarnings("Duplicates")
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

        JButton jbDelete = new JButton("Remove");
        jbDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Object> files = jList.getSelectedValuesList();
                for (Object o : files) {
                    File file = (File) o;
                    remFile(file);
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
        jList = new JList<>(fileList.toArray());
        jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jList.setLayoutOrientation(JList.VERTICAL);
        jList.setVisibleRowCount(-1);
        jList.setCellRenderer(new DefaultListCellRenderer());
        jList.addMouseListener(mlClick);
        jList.addMouseMotionListener(mlMove);
        jList.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) selectFile();
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

    private void selectFile() {
        File file = (File) jList.getSelectedValue();
        if (file == null) return;
        EditorWrapper.openEditor(file);
        remFile(file);
        setVisible(false);
    }
}
