package at.justin.matlab.prefs;

import com.mathworks.mwswing.MJPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andreas Justin on 2016-08-23.
 */
public class PrefsWelcome extends MJPanel {
    public PrefsWelcome(Dimension dim) {
        this.setName("MatlabEditorPluginSettings");
        this.setSize(dim);

        this.setLayout(new FlowLayout());
        this.add(new JLabel("Check for newest releases:"));
        JTextField jtf = new JTextField("https://github.com/GavriYashar/Matlab-Editor-Plugin/releases");
        jtf.setEditable(false);
        this.add(jtf);
    }

    public PrefsWelcome() {
        this(new Dimension(100, 200));
    }

    public static MJPanel createPrefsPanel() {
        return new PrefsWelcome();
    }

    public static void commitPrefsChanges(boolean save) {
        if (save) {
        }
    }
}
