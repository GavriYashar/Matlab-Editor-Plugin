package at.justin.matlab.prefs;

import at.justin.matlab.gui.components.UndecoratedFrame;
import at.justin.matlab.util.ScreenSize;

import java.awt.*;

/**
 * Created by Andreas Justin on 2016-08-25.
 * this class exists if if the jar file is not in javaclasspath.txt
 */
public class PrefsWindow extends UndecoratedFrame {
    private static final PrefsWindow INSTANCE = new PrefsWindow();

    private PrefsWindow() {
        setLayout();
    }

    public static PrefsWindow getInstance() {
        return new PrefsWindow();
    }

    private void setLayout() {
        setSize(new Dimension(800,600));
        setLocation(ScreenSize.getCenter(getSize()));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new PrefsPanel(), gbc);
        setResizable(false);
    }

    public static void showDialog() {
        getInstance().setVisible(true);

    }
}
