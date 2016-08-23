package at.justin.matlab.prefs;

import com.mathworks.mwswing.MJPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andreas Justin on 2016-08-12.
 */

// import com.mathworks.mlwidgets.prefs.MATLABProductNodePrefsPanel;
public class PrefsPanel extends MJPanel {
    /**
     * @param dim it seems matlab passes in Dimension in Constructor
     */
    public PrefsPanel(Dimension dim) {
        this.setName("MatlabEditorPluginSettings");
        this.setSize(dim);

        this.add(new JLabel("YAY!"));
    }

    public PrefsPanel() {
        this(new Dimension(100, 200));
    }

    public static MJPanel createPrefsPanel() {
        return new PrefsPanel();
    }

    public static void commitPrefsChanges(boolean save) {
        if (save) {
            System.out.println("saving prefs");
        } else {
            System.out.println("cancel prefs");
        }
    }
}
