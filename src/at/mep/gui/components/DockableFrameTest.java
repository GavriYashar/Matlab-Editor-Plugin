package at.mep.gui.components;

import com.mathworks.mde.desk.MLDesktop;
import com.mathworks.widgets.desk.DTClientBase;

import javax.swing.*;
import java.awt.*;

public class DockableFrameTest extends DTClientBase {
    public DockableFrameTest() {
        this.setLayout(new BorderLayout());
        JLabel jLabel = new JLabel("Yay!");
        jLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.add(jLabel, "Center");

        MLDesktop.getInstance().addClient(this, "Test - Yay!");
    }
}
