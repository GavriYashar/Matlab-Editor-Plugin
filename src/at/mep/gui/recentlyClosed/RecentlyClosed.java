package at.mep.gui.recentlyClosed;

import at.mep.editor.EditorWrapper;
import at.mep.gui.components.JTextFieldSearch;
import at.mep.gui.components.UndecoratedFrame;
import at.mep.installer.Install;
import at.mep.prefs.Settings;
import at.mep.util.KeyStrokeUtil;
import at.mep.util.RunnableUtil;
import at.mep.util.ScreenSize;
import com.mathworks.matlab.api.editor.Editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/** Created by Andreas Justin on 2017-10-11. */
public class RecentlyClosed extends UndecoratedFrame {
    private static final String ENTER_ACTION = "enterAction";
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static RecentlyClosed INSTANCE;
    private static Dimension dimension;
    private static JTabbedPane tabbedPane;
    private static JList<Object> jListTS;
    private static JList<Object> jListLS;
    private static List<File> fileListTS = new ArrayList<>(20);
    private static List<File> fileListLS = new ArrayList<>(20);
    private final AbstractAction enterAction = new AbstractAction(ENTER_ACTION) {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectFile();
        }
    };

    private RecentlyClosed() {
        dimension = Settings.getPropertyDimension("dim.recentlyClosedViewer");
        Runnable runnable = this::setLayout;
        RunnableUtil.invokeInDispatchThreadIfNeeded(runnable);
        loadLastSessions();
    }

    public static RecentlyClosed getInstance() {
        if (INSTANCE != null) return INSTANCE;
        INSTANCE = new RecentlyClosed();
        return INSTANCE;
    }

    private static void loadLastSessions() {
        Properties rcLS = new Properties();
        try {
            InputStream in = new FileInputStream(Install.getRecentlyClosedLastSessions());
            rcLS.load(in);
            in.close();
        } catch (FileNotFoundException ignored) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // reading props
        int count = 0;
        try {
            count = Integer.parseInt(rcLS.getProperty("rcCount"));
        } catch (Exception ignored) {
            return;
        }
        for (int i = 0; i < count; i++) {
            String prop = "rc_" + i;
            try {
                fileListLS.add(new File(rcLS.getProperty(prop + ".AbsolutePath")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // remove file from last session which are already open.
        //  on Matlab Exit, all editors are being closed, i don't know yet how to difference between a session kill and
        //  a simple closing of an editor or the editor application.
        List<Editor> editors = EditorWrapper.getOpenEditors();
        for (Editor editor : editors) {
            fileListLS.remove(EditorWrapper.getFile(editor));
        }
    }

    private static void saveLastSessions() {
        Writer writer = null;
        try {
            writer = new FileWriter(Install.getRecentlyClosedLastSessions(), false);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Properties rcLS = new Properties();
        rcLS.setProperty("rcCount", Integer.toString(fileListLS.size()));
        for (int i = 0; i < fileListLS.size(); i++) {
            File f = fileListLS.get(i);
            String prop = "rc_" + i;
            rcLS.setProperty(prop + ".AbsolutePath", f.getAbsolutePath());
        }
        try {
            rcLS.store(writer, "Stored Recently Closed Last Session(s)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void storeDimension(Dimension dimension) {
        Settings.setPropertyDimension("dim.recentlyClosedViewer", dimension);
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addFile(File file) {
        if (!file.exists()) return;
        fileListTS.add(file);
        fileListLS.add(file);
        saveLastSessions();
    }

    public static void remFile(File file) {
        fileListTS.remove(file);
        fileListLS.remove(file);
    }

    public void showDialog() {
        this.setVisible(true);
        this.setLocation(ScreenSize.getCenter(this.getSize()));
        updateList();
    }

    public void updateList() {
        jListTS.setListData(fileListTS.toArray());
        jListLS.setListData(fileListLS.toArray());
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
                List<Object> files;
                if (RecentlyClosed.tabbedPane.getSelectedIndex() == 0) {
                    files = jListTS.getSelectedValuesList();
                } else {
                    files = jListLS.getSelectedValuesList();
                }
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
        MouseListener ml = new MouseListener() {
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
        };
        jListTS = new JList<>(fileListTS.toArray());
        jListTS.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jListTS.setLayoutOrientation(JList.VERTICAL);
        jListTS.setVisibleRowCount(-1);
        jListTS.setCellRenderer(new DefaultListCellRenderer());
        jListTS.addMouseListener(mlClick);
        jListTS.addMouseMotionListener(mlMove);
        jListTS.addMouseListener(ml);

        jListLS = new JList<>(fileListTS.toArray());
        jListLS.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jListLS.setLayoutOrientation(JList.VERTICAL);
        jListLS.setVisibleRowCount(-1);
        jListLS.setCellRenderer(new DefaultListCellRenderer());
        jListLS.addMouseListener(mlClick);
        jListLS.addMouseMotionListener(mlMove);
        jListLS.addMouseListener(ml);

        KeyStroke ks = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ENTER);
        getRootPane().getInputMap(IFW).put(ks, ENTER_ACTION);
        getRootPane().getActionMap().put(ENTER_ACTION, enterAction);

        JScrollPane jspTS = new JScrollPane(jListTS);
        jspTS.getVerticalScrollBar().setUnitIncrement(20);
        jspTS.getHorizontalScrollBar().setUnitIncrement(20);
        jspTS.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jspTS.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JScrollPane jspLS = new JScrollPane(jListLS);
        jspLS.getVerticalScrollBar().setUnitIncrement(20);
        jspLS.getHorizontalScrollBar().setUnitIncrement(20);
        jspLS.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jspLS.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("This Session", jspTS);
        tabbedPane.addTab("Last Session(s)", jspLS);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 10, 10, 10);

        rootPane.add(tabbedPane, gbc);
    }

    private void selectFile() {
        File file;
        if (RecentlyClosed.tabbedPane.getSelectedIndex() == 0) {
            file = (File) jListTS.getSelectedValue();
        } else {
            file = (File) jListLS.getSelectedValue();
        }
        if (file == null) return;
        EditorWrapper.openEditor(file);
        remFile(file);
        setVisible(false);
    }
}
