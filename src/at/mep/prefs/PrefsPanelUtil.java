package at.mep.prefs;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;

/** Created by Andreas Justin on 2016-08-23. */
public class PrefsPanelUtil {
    private static final JFileChooser fc = new JFileChooser();
    /**
     * @return [0] the property changer(e.g.: checkbox) [1] label
     */
    public static Component[] getComponentsForSetting(String property, EPropertyType type) {
        Component[] components = new Component[2];
        switch (type) {
            case BOOLEAN:
                components[0] = getCheckBox(property);
                break;
            case INTEGER:
                components[0] = getIntegerField(property);
                break;
            case STRING:
                components[0] = getStringField(property);
                break;
            case STRING_DROPDOWN:
                components[0] = getDropDownString(property);
                break;
            case ONOFF:
                components[0] = getCheckBoxOnOff(property);
                break;
            case PATH:
                components[0] = getPathChooser(property);
                break;
            case COLOR:
                components[0] = getColorChooser(property);
                break;
        }

        String help = Settings.getProperty("help." + property);
        components[1] = new JLabel(help);
        return components;
    }

    private static Component getStringField(String property) {
        final JTextField jtf = new JTextField(Settings.getProperty(property));
        jtf.setName(property);
        jtf.setEditable(true);
        Dimension d = new Dimension(20,20);
        jtf.setPreferredSize(d);
        jtf.setMaximumSize(d);
        return jtf;
    }

    private static JTextField getIntegerField(final String property) {
        final JTextField jtf = new JTextField(Settings.getProperty(property));
        jtf.setName(property);
        jtf.setEditable(true);
        Dimension d = new Dimension(20,20);
        jtf.setPreferredSize(d);
        jtf.setMaximumSize(d);
        jtf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // copy paste Code focusLost
                boolean isInt = stringIsIntegerDialog(jtf.getText());
                if (!isInt) {
                    jtf.setText(Settings.getProperty(property + "Values"));
                }
            }
        });
        jtf.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                // copy paste Code actionPerformed
                boolean isInt = stringIsIntegerDialog(jtf.getText());
                if (!isInt) {
                    jtf.setText(Settings.getProperty(property + "Values"));
                }
            }
        });

        return jtf;
    }

    private static JComboBox getDropDownString(String property) {
        String currentValue = Settings.getProperty(property);
        String[] strings = Settings.getPropertyStringArray(property + "Values");
        if (strings.length == 0) return null;

        int index = Arrays.asList(strings).indexOf(currentValue);
        if (index < 0) index = 0;
        JComboBox jComboBox = new JComboBox(strings);
        jComboBox.setSelectedIndex(index);
        return jComboBox;
    }

    private static JTextField getPathChooser(final String property) {
        final JTextField jtf = new JTextField(Settings.getProperty(property));
        jtf.setName(property);
        jtf.setEditable(false);
        Dimension d = new Dimension(20,20);
        jtf.setPreferredSize(d);
        jtf.setMaximumSize(d);
        jtf.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = fc.showOpenDialog(jtf);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    jtf.setText(file.toString());
                }
            }
        });

        return jtf;
    }

    private static JPanel getColorChooser(final String property) {
        final JPanel jp = new JPanel();
        jp.setName(property);
        Dimension dimension = new Dimension(15, 15);
        jp.setSize(dimension);
        jp.setBorder(new LineBorder(new Color(0x505050), 1));
        jp.setBackground(Settings.getPropertyColor(property));
        jp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(null, "Choose color for Breakpoint Bar", Settings.getPropertyColor(property));
                if (color == null) {
                    return;
                }
                jp.setBackground(color);
            }
        });
        return jp;
    }

    private static JCheckBox getCheckBoxOnOff(String property) {
        String val = Settings.getProperty(property);
        val = val.toLowerCase();
        return getCheckBox(property, val.equals("on"));
    }

    private static JCheckBox getCheckBox(String property) {
        return getCheckBox(property, Settings.getPropertyBoolean(property));
    }

    private static JCheckBox getCheckBox(String name, boolean value) {
        return new JCheckBox(name, value);
    }

    private static boolean stringIsIntegerDialog(String string) {
        try {
            //noinspection ResultOfMethodCallIgnored
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    e.getMessage() + "\nplease set enter a valid integer format",
                    "Invalid Integer Format",
                    JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}
