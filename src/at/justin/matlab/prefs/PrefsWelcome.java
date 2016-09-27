package at.justin.matlab.prefs;

import at.justin.matlab.installer.Install;
import at.justin.matlab.util.FileUtils;
import com.mathworks.mlwidgets.prefs.PrefsChanger;
import com.mathworks.mwswing.MJPanel;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/** Created by Andreas Justin on 2016-08-23. */
public class PrefsWelcome extends MJPanel {
    public PrefsWelcome(Dimension dim) {
        this.setName("MatlabEditorPluginSettings");
        this.setSize(dim);
        this.setLayout(new GridBagLayout());

        try {
            addVersion();
        } catch (IOException e) {
            e.printStackTrace();
        }
        addUpdate();
        try {
            addLicense();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void addLicense() throws IOException {
        InputStream stream = PrefsWelcome.class.getResourceAsStream("/MEP_license.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String license = FileUtils.readBufferedReaderToString(br, true);
        br.close();

        JTextPane jtp = new JTextPane();
        jtp.setText(license);
        jtp.setEditable(false);
        jtp.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()));

        StyledDocument doc = jtp.getStyledDocument();
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), attributeSet, false);

        JScrollPane jsp = new JScrollPane(jtp);
        jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.9;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        this.add(jsp, gbc);
    }

    private void addUpdate() {
        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout());
        jp.add(new JLabel("Check for newest releases:"));
        JTextField jtf = new JTextField("https://github.com/GavriYashar/Matlab-Editor-Plugin/releases");
        jtf.setEditable(false);
        jp.add(jtf);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.05;
        gbc.insets = new Insets(5, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(jp, gbc);
    }

    private void addVersion() throws IOException {
        String versionString = Install.getVersion();

        JPanel jp = new JPanel();
        jp.setLayout(new FlowLayout());
        jp.add(new JLabel("Version: " + versionString));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.05;
        gbc.insets = new Insets(5, 5, 0, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.add(jp, gbc);
    }
}
