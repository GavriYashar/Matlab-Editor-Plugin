package at.mep.gui.components;

import com.mathworks.widgets.desk.DTClientBase;

import javax.swing.*;
import java.awt.*;

/**
 * +----------+----------+-------------+---------------------------------------------------+
 * | Scenario | restore  | calls       | scenario description                              |
 * |          | position | constructor |                                                   |
 * +----------+----------+-------------+---------------------------------------------------+
 * |     1    |     -    |      -      |  DFT was either never added or has been removed   |
 * +----------+----------+-------------+---------------------------------------------------+
 * |     2    |     x    |      x      |  DFT was added floating                           |
 * +----------+----------+-------------+---------------------------------------------------+
 * |     3    |     x    |      x      |  DFT was added docked as own panel                |
 * +----------+----------+-------------+---------------------------------------------------+
 * |     4    |     x    |      -      |  DFT was added as hidden tab                      |
 * |          |          |      x      |  calls constructor upon activation                |
 * +----------+----------+-------------+---------------------------------------------------+
 * |     5    |     x    |      x      |  DFT was added as active tab                      |
 * +----------+----------+-------------+---------------------------------------------------+
 *
 */
public class DockableFrameTest extends DTClientBase {
    private static final DockableFrameTest instance = new DockableFrameTest();
    private DockableFrameTest() {
        this.setLayout(new BorderLayout());
        JLabel jLabel = new JLabel("Yay!");
        jLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.add(jLabel, "Center");

        System.out.println("MLDesktop.getInstance().addClient(this, \"Test - Yay!\"");
    }

    public static DockableFrameTest getInstance() {
        return instance;
    }
}
