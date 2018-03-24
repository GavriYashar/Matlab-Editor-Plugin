package at.mep.gui.recentlyClosed;

import at.mep.editor.EditorWrapper;
import at.mep.gui.components.DockableFrame;
import at.mep.gui.components.JTextFieldSearch;
import at.mep.installer.Install;
import at.mep.util.FileUtils;
import at.mep.util.KeyStrokeUtil;
import at.mep.util.ScreenSize;
import com.mathworks.matlab.api.editor.Editor;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/** Created by Andreas Justin on 2017-10-11. */
public class RecentlyClosed extends DockableFrame {
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
    private static RecentlyClosed instance;
    private static JTabbedPane tabbedPane;
    private static JList<Object> jListTS;
    private static JList<Object> jListLS;
    private static List<File> fileListTS = new ArrayList<>(20);
    private static List<File> fileListLS = new ArrayList<>(20);
    private static JTextFieldSearch jTFS;
    private final AbstractAction enterAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            selectFile();
            escAction.actionPerformed(new ActionEvent(e, 0, null));
        }
    };

    private AbstractAction updateAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            findPattern(jTFS.getText());
            if (jListLS.getVisibleRowCount() > 1) {
                jListLS.setSelectedIndex(1);
            }
            if (jListTS.getVisibleRowCount() > 1) {
                jListTS.setSelectedIndex(1);
            }
        }
    };

    private RecentlyClosed() {
        super(EViewer.RECENTLY_CLOSED);
        setLayout();
        loadLastSessions();
        addFocusListener(jTFS);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                updateList();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
    }

    public static RecentlyClosed getInstance() {
        if (instance == null) instance = new RecentlyClosed();
        return instance;
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
        int count;
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
        Writer writer;
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
        setLayout(new GridBagLayout());

        addSearchBar();
        addToolBar();
        addViewPanel();
    }

    @SuppressWarnings("Duplicates")
    private void addSearchBar() {
        jTFS = new JTextFieldSearch(20);
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
                {
                    int row = jListLS.getSelectedIndex(); // single selection
                    if (row < 0) {
                        jListLS.setSelectedIndex(0);
                    }
                    jListLS.setSelectedIndex(row - 1); // zero is top, so up means -1
                }
                {
                    int row = jListTS.getSelectedIndex(); // single selection
                    if (row < 0) {
                        jListTS.setSelectedIndex(0);
                    }
                    jListTS.setSelectedIndex(row - 1); // zero is top, so up means -1
                }
            }
        });

        KeyStroke ksD = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_DOWN);
        jTFS.getInputMap(JComponent.WHEN_FOCUSED).put(ksD, "DOWN");
        jTFS.getActionMap().put("DOWN", new AbstractAction("DOWN") {
            @Override
            public void actionPerformed(ActionEvent e) {
                {
                    int row = jListLS.getSelectedIndex(); // single selection
                    if (row < 0) {
                        jListLS.setSelectedIndex(0);
                    }
                    if (fileListLS.size() - 1 > row) {
                        jListLS.setSelectedIndex(row + 1); // zero is top, so down means +1
                    }
                }
                {
                    int row = jListTS.getSelectedIndex(); // single selection
                    if (row < 0) {
                        jListTS.setSelectedIndex(0);
                    }
                    if (fileListTS.size() - 1 > row) {
                        jListTS.setSelectedIndex(row + 1); // zero is top, so down means +1
                    }
                }
            }
        });

        addEnterAction(jTFS, enterAction);
    }

    private void findPattern(String pattern) {
        try {
            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            jTFS.setForeground(null);
            jListLS.setListData(FileUtils.filter(fileListLS, p).toArray());
            jListTS.setListData(FileUtils.filter(fileListTS, p).toArray());
        } catch (PatternSyntaxException e) {
            jTFS.setForeground(Color.RED);
        }
    }

    private void addToolBar() {
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.X_AXIS));

        JButton jbDelete = new JButton("Remove");
        jbDelete.addActionListener(e -> {
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
        MouseAdapter ml = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) selectFile();
            }
        };
        jListTS = new JList<>(fileListTS.toArray());
        jListTS.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jListTS.setLayoutOrientation(JList.VERTICAL);
        jListTS.setVisibleRowCount(-1);
        jListTS.setCellRenderer(new DefaultListCellRenderer());
        jListTS.addMouseListener(ml);

        jListLS = new JList<>(fileListTS.toArray());
        jListLS.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jListLS.setLayoutOrientation(JList.VERTICAL);
        jListLS.setVisibleRowCount(-1);
        jListLS.setCellRenderer(new DefaultListCellRenderer());
        jListLS.addMouseListener(ml);

        KeyStroke ks = KeyStrokeUtil.getKeyStroke(KeyEvent.VK_ENTER);
        getInputMap(IFW).put(ks, "ENTER");
        getActionMap().put("ENTER", enterAction);

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

        add(tabbedPane, gbc);
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
    }
}
