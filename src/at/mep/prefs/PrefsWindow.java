package at.mep.prefs;

import at.mep.gui.components.UndecoratedFrame;
import at.mep.util.ScreenSize;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Andreas Justin on 2016-08-25.
 * this class exists if if the jar file is not in javaclasspath.txt
 */
public class PrefsWindow extends UndecoratedFrame {
    private static final PrefsWindow INSTANCE = new PrefsWindow();

    private PrefsWindow() {
        setLayout();
    }

    @Override
    protected void storeDimension(Dimension dimension) {
        
    }

    public static PrefsWindow getInstance() {
        return new PrefsWindow();
    }

    private void setLayout() {
        setSize(new Dimension(800,600));
        setResizable(false);
        setLocation(ScreenSize.getCenter(getSize()));
        setLayout(new GridBagLayout());

        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.gridx = 0;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = GridBagConstraints.BOTH;
            add(new PrefsPanel(), gbc);
        }
        {
            JButton save = new JButton("Save");
            save.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    PrefsPanel.commitPrefsChanges(true);
                    setVisible(false);
                }
            });
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 1;
            gbc.gridx = 0;
            add(save, gbc);
        }
    }

    public static void showDialog() {
        getInstance().setVisible(true);

    }
}
