package at.mep.prefs;

import at.mep.editor.EditorApp;
import com.mathworks.mwswing.MJPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/** Created by Andreas Justin on 2016-08-12. */

// import com.mathworks.mlwidgets.prefs.MATLABProductNodePrefsPanel;

public class PrefsPanel extends MJPanel {
    private static java.util.List<Component> componentList = new ArrayList<>(20);
    private static java.util.List<EPropertyType> componentTypeList = new ArrayList<>(20);
    private static java.util.List<String> componentKeyList = new ArrayList<>(20);
    private JScrollPane jsp;
    private JPanel jp;

    /**
     * @param dim it seems matlab passes in Dimension in Constructor
     */
    public PrefsPanel(Dimension dim) {
        this.setName("MatlabEditorPluginSettings");
        this.setSize(dim);
        this.setPreferredSize(dim);
        setLayout();
    }

    public PrefsPanel() {
        this(new Dimension(800, 600));
    }

    public static MJPanel createPrefsPanel() {
        return new PrefsPanel();
    }

    public static void commitPrefsChanges(boolean save) {
        if (!save) return;
        for (int i = 0; i < componentList.size(); i++) {
            Component c = componentList.get(i);
            String key = componentKeyList.get(i);
            switch (componentTypeList.get(i)) {
                case BOOLEAN:
                    JCheckBox jcb1 = (JCheckBox) c;
                    Settings.setPropertyBoolean(key, jcb1.isSelected());
                    break;
                case INTEGER:
                    JTextField jtf2 = (JTextField) c;
                    Settings.setProperty(key, jtf2.getText());
                    break;
                case STRING:
                    JTextField jtf3 = (JTextField) c;
                    Settings.setProperty(key, jtf3.getText());
                    break;
                case STRING_DROPDOWN:
                    JComboBox jComboBox = (JComboBox) c;
                    Settings.setProperty(key, (String) jComboBox.getSelectedItem());
                    break;
                case ONOFF:
                    JCheckBox jcb2 = (JCheckBox) c;
                    Settings.setProperty(key, jcb2.isSelected() ? "on" : "off");
                    break;
                case PATH:
                    JTextField jtf1 = (JTextField) c;
                    Settings.setProperty(key, jtf1.getText());
                    break;
                case COLOR:
                    JPanel jp1 = (JPanel) c;
                    Settings.setPropertyColor(key, jp1.getBackground());
                    if (key.equals("bpColor")) {
                        EditorApp.getInstance().colorizeBreakpointView(jp1.getBackground());
                    }
                    break;
            }
        }
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.fill = GridBagConstraints.BOTH;

        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));

        jsp = new JScrollPane(jp);
        jsp.setLayout(new ScrollPaneLayout());
        jsp.getVerticalScrollBar().setUnitIncrement(20);
        jsp.getHorizontalScrollBar().setUnitIncrement(20);

        if (Settings.getPropertyBoolean("jar.isOnStaticPath")) {
            jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        } else {
            jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }

        add(jsp, gbc);

        addFeatureSelector();
        addMEPProps();
        addUserProps();
        if (Settings.getPropertyBoolean("isPublicUser")) {
            return;
        }
        addOtherProps();
        addLMProps();
        // addTTHListProps();
    }

    private void addFeatureSelector() {
        JPanel ps = new JPanel();
        ps.setLayout(new GridBagLayout());
        ps.setBorder(BorderFactory.createTitledBorder("MEP-Features"));

        String[] properties = {"feature.enableClipboardStack",
                "feature.enableFileStructure",
                "feature.enableBookmarksViewer",
                "feature.enableDuplicateLine",
                "feature.enableDeleteCurrentLine",
                "feature.enableReplacements",
                "feature.enableAutoDetailViewer",
                "feature.enableAutoCurrentFolder",
                "feature.enableExecuteCurrentLine",
                "feature.enableRecentlyClosed",
                "feature.enableClickHistory"
        };
        EPropertyType[] types = {EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN
        };

        addPropsToPanel(ps, properties, types);
        jp.add(ps);
    }

    private void addPropsToPanel(JPanel ps, String[] properties, EPropertyType[] types) {
        for (int i = 0; i < properties.length; i++) {
            Component[] components = PrefsPanelUtil.getComponentsForSetting(properties[i], types[i]);

            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridy = i;
            gbc1.gridx = 0;
            gbc1.weightx = 1;
            gbc1.insets = new Insets(10, 10, 0, 0);
            gbc1.fill = GridBagConstraints.HORIZONTAL;

            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridy = i;
            gbc2.gridx = 1;
            gbc2.weightx = 1;
            gbc2.insets = new Insets(10, 10, 0, 0);
            gbc2.fill = GridBagConstraints.HORIZONTAL;

            ps.add(components[0], gbc1);
            ps.add(components[1], gbc2);

            componentList.add(components[0]);
            componentTypeList.add(types[i]);
            componentKeyList.add(properties[i]);
        }
    }

    private void addOtherProps() {
        JPanel ps = new JPanel();
        ps.setLayout(new GridBagLayout());
        ps.setBorder(BorderFactory.createTitledBorder("Other"));

        String[] properties = {"startup.autoLoadShortcuts",
                "startup.enable",
                "UseLDS",
                "showQuotes",
                "FreeCommander",
                "DefaultFigureGraphicsSmoothing"
        };
        EPropertyType[] types = {EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.PATH,
                EPropertyType.ONOFF
        };

        addPropsToPanel(ps, properties, types);
        jp.add(ps);
    }

    private void addMEPProps() {
        JPanel ps = new JPanel();
        ps.setLayout(new GridBagLayout());
        ps.setBorder(BorderFactory.createTitledBorder("Extend Matlab Editor Functionality"));

        String[] properties;
        EPropertyType[] types;
        properties = new String[]{"isPublicUser",
                "autoReloadProps",
                "bpColor",
                "fs.iconSet",
                "clipboardStack.size",
                "ch.sizeMax"
        };
        types = new EPropertyType[]{EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.COLOR,
                EPropertyType.STRING_DROPDOWN,
                EPropertyType.INTEGER,
                EPropertyType.INTEGER
        };
        addPropsToPanel(ps, properties, types);

        jp.add(ps);
    }

    private void addUserProps() {
        JPanel ps = new JPanel();
        ps.setLayout(new GridBagLayout());
        ps.setBorder(BorderFactory.createTitledBorder("User"));

        String[] properties = {"user.name",
                "user.department",
                "user.company",
                "user.email"
        };
        EPropertyType[] types = {EPropertyType.STRING,
                EPropertyType.STRING,
                EPropertyType.STRING,
                EPropertyType.STRING
        };
        addPropsToPanel(ps, properties, types);

        jp.add(ps);
    }

    private void addLMProps() {
        JPanel ps = new JPanel();
        ps.setLayout(new GridBagLayout());
        ps.setBorder(BorderFactory.createTitledBorder("LoadMessDaten"));

        String[] properties = {"LoadMessDaten_Shortener",
                "LoadMessDaten_NoExprManipulate",
                "LoadMessDaten_EnableSimpleView"
        };
        EPropertyType[] types = {EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN,
                EPropertyType.BOOLEAN
        };
        addPropsToPanel(ps, properties, types);

        jp.add(ps);
    }

    private void addTTHListProps() {
        JPanel ps = new JPanel();
        ps.setLayout(new GridBagLayout());
        ps.setBorder(BorderFactory.createTitledBorder("TT-StundenListe"));

        jp.add(ps);
    }

}
