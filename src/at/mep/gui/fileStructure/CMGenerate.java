package at.mep.gui.fileStructure;

import at.mep.debug.Debug;
import at.mep.editor.EditorWrapper;
import at.mep.util.StringUtils;

import javax.swing.*;
import java.awt.event.*;

/** Created by Andreas Justin on 2018-02-23.
 * Code generation context menu
 * */
public class CMGenerate extends JPopupMenu {
    /**
     *
     * function $GS$PROP($IN)
     *     $CMD;
     * end
     *
     * enter correct getter/setter function type
     * GS ... $PROP = get
     *                set
     *
     * enter correct input type depending on getter/setter
     * IN ... obj
     *        obj, val
     *
     * entter correct command type depending on getter/setter
     * CMD ... obj.$PROP = val
     *         $PROP = obj.$PROP
     *
     * */
    private static final String GENERIC_SET_GET_STRING = "function $GS$PROP($IN)\n    $CMD;\nend";
    private static final String GENERIC_SET_CMD = "obj.$PROP = val";
    private static final String GENERIC_GET_CMD = "$PROP = obj.$PROP";

    JMenuItem jmiSetterDot;
    JMenuItem jmiSetter;

    JMenuItem jmiGetterDot;
    JMenuItem jmiGetter;

    // JTextField jtfSetter;
    // JTextField jtfGetter;

    String property = "";

    /** will delete default string */
    // MouseListener flJTF = new MouseListener() {
    //     @Override
    //     public void mouseClicked(MouseEvent e) {
    //         JTextField jTextField = ((JTextField) e.getComponent());
    //         if (jTextField.getText().contains("generate custom")) {
    //             jTextField.setText("");
    //         }
    //     }
    //
    //     @Override
    //     public void mousePressed(MouseEvent e) {
    //     }
    //
    //     @Override
    //     public void mouseReleased(MouseEvent e) {
    //     }
    //
    //     @Override
    //     public void mouseEntered(MouseEvent e) {
    //     }
    //
    //     @Override
    //     public void mouseExited(MouseEvent e) {
    //     }
    // };

    ActionListener menuListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (Debug.isDebugEnabled()) {
                System.out.println("Popup menu item ["
                        + e.getActionCommand() + "] was pressed.");
            }

            String str = e.getActionCommand();
            if (str == null || str.length() < 1) {
                return;
            }
            str = str.replace("generate: ", "");
            str = str.replace(" ", "");
            String cmd = GENERIC_SET_GET_STRING;

            JMenuItem jmi = (JMenuItem) e.getSource();
            if (jmi == jmiGetter) {
                cmd = cmd.replace("$GS", "$PROP = get");
                cmd = cmd.replace("$IN", "obj");
                cmd = cmd.replace("$CMD", GENERIC_GET_CMD);
                cmd = cmd.replace("$PROP", str.replace("get",""));
            } else if (jmi == jmiGetterDot) {
                cmd = cmd.replace("$GS", "$PROP = get.");
                cmd = cmd.replace("$IN", "obj");
                cmd = cmd.replace("$CMD", GENERIC_GET_CMD);
                cmd = cmd.replace("$PROP", str.replace("get.",""));
            } else if (jmi == jmiSetterDot) {
                cmd = cmd.replace("$GS", "set.");
                cmd = cmd.replace("$IN", "obj, val");
                cmd = cmd.replace("$CMD", GENERIC_SET_CMD);
                cmd = cmd.replace("$PROP", str.replace("set.",""));
            } else if (jmi == jmiSetter) {
                cmd = cmd.replace("$GS", "set");
                cmd = cmd.replace("$IN", "obj, val");
                cmd = cmd.replace("$CMD", GENERIC_SET_CMD);
                cmd = cmd.replace("$PROP", str.replace("set",""));
            }

            // TODO: cmd needs to be adjusted to match current indentation
            EditorWrapper.setSelectedTxt(cmd);
        }
    };

    public CMGenerate() {
        jmiSetterDot = new JMenuItem("generate: set.");
        jmiGetterDot = new JMenuItem("generate: get.");

        jmiSetter = new JMenuItem("generate: set");
        jmiGetter = new JMenuItem("generate: get");

        // jtfSetter = new JTextField("generate custom setter");
        // jtfGetter = new JTextField("generate custom getter");
        // jtfSetter.addMouseListener(flJTF);
        // jtfGetter.addMouseListener(flJTF);

        jmiSetterDot.addActionListener(menuListener);
        jmiGetterDot.addActionListener(menuListener);
        jmiSetter.addActionListener(menuListener);
        jmiGetter.addActionListener(menuListener);
        // jtfSetter.addActionListener(menuListener);
        // jtfGetter.addActionListener(menuListener);

        add(jmiSetterDot);
        add(jmiGetterDot);
        add(jmiSetter);
        add(jmiGetter);
        // add(jtfSetter);
        // add(jtfGetter);
    }

    public void modifyProperty(String property) {
        this.property = property;

        jmiSetterDot.setText("generate: set." + property);
        jmiGetterDot.setText("generate: get." + property);

        property = StringUtils.capitalizeStart(property);
        jmiSetter.setText("generate: set" + property);
        jmiGetter.setText("generate: get" + property);

        // jtfSetter.setText("generate custom setter");
        // jtfGetter.setText("generate custom getter");
    }

}
