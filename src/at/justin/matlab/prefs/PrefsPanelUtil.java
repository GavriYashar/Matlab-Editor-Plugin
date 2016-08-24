package at.justin.matlab.prefs;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

/**
 * Created by Andreas Justin on 2016-08-23.
 */
public class PrefsPanelUtil {
    private static final JFileChooser fc = new JFileChooser();
    /**
     * @param property
     * @param type
     * @param help
     * @return [0] the property changer(e.g.: checkbox) [1] label
     */
    public static Component[] getComponentsForSetting(String property, PropertyType type, String help) {
        Component[] components = new Component[2];
        switch (type) {
            case BOOLEAN:
                components[0] = getCheckBox(property);
                break;
            case INTEGER:
                break;
            case STRING:
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

        components[1] = new JLabel(help);
        return components;
    }

    private static JTextField getPathChooser(final String property) {
        final JTextField jtf = new JTextField(Settings.getProperty(property));
        jtf.setName(property);
        jtf.setEditable(false);
        Dimension d = new Dimension(20,20);
        jtf.setPreferredSize(d);
        jtf.setMaximumSize(d);
        jtf.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = fc.showOpenDialog(jtf);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    jtf.setText(file.toString());
                }
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

        return jtf;
    }

    private static JPanel getColorChooser(final String property) {
        final JPanel jp = new JPanel();
        jp.setName(property);
        Dimension dimension = new Dimension(15, 15);
        jp.setSize(dimension);
        jp.setBorder(new LineBorder(new Color(0x505050), 1));
        jp.setBackground(Settings.getPropertyColor(property));
        jp.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color color = JColorChooser.showDialog(null, "Choose color for Breakpoint Bar", Settings.getPropertyColor(property));
                if (color == null) {
                    return;
                }
                jp.setBackground(color);
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
}
