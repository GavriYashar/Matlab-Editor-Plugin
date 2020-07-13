package at.mep.installer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/** Created by Andreas Justin on 2016-08-22. */
public class JPanelInstall extends JPanel {
    private final static Color INVALID = new Color(224, 125, 112);
    private final static Color VALID = new Color(139, 255, 109);
    final JFileChooser fc = new JFileChooser();
    private Install install;

    private JTextField jtJAR = null;
    private JTextField jtJCP = null;
    private JTextField jtID = null;

    public JPanelInstall() {
        install = Install.getInstance();
        setLayout();
    }

    private void setLayout() {
        setLayout(new GridBagLayout());
        addPathSelectionPanel();
        addButtons();
    }

    private void addPathSelectionPanel() {
        File file = null;
        try {
            file = Install.getJarFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        {
            // label for JAR files
            GridBagConstraints cLabel = new GridBagConstraints();
            cLabel.gridy = 0;
            cLabel.gridx = 0;
            cLabel.weightx = 1;
            cLabel.insets = new Insets(10, 10, 0, 0);
            cLabel.fill = GridBagConstraints.HORIZONTAL;
            add(new JLabel("Location of MEP and matconsolectl .JAR-files"), cLabel);

            // jar textfield
            GridBagConstraints cJARPath = new GridBagConstraints();
            cJARPath.gridy = 1;
            cJARPath.gridx = 0;
            cJARPath.weightx = 1;
            cJARPath.insets = new Insets(0, 10, 0, 10);
            cJARPath.fill = GridBagConstraints.HORIZONTAL;
            jtJAR = new JTextField(file.getParent());
            jtJAR.setEditable(false);
            add(jtJAR, cJARPath);
            searchJars(file.getParentFile());

            // jar browser button
            GridBagConstraints cJARPathBrowse = new GridBagConstraints();
            cJARPathBrowse.gridy = 1;
            cJARPathBrowse.gridx = 1;
            cJARPathBrowse.weightx = 0.1;
            cJARPathBrowse.insets = new Insets(0, 0, 0, 10);
            final JButton jb = new JButton("...");
            jb.addActionListener(e -> {
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fc.showOpenDialog(jb);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file13 = fc.getSelectedFile();
                    jtJAR.setText(file13.toString());
                    searchJars(file13);
                }
            });
            add(jb, cJARPathBrowse);
        }
        {
            // label for javaclasspath.txt
            GridBagConstraints cLabel = new GridBagConstraints();
            cLabel.gridy = 2;
            cLabel.gridx = 0;
            cLabel.weightx = 1;
            cLabel.insets = new Insets(10, 10, 0, 0);
            cLabel.fill = GridBagConstraints.HORIZONTAL;
            add(new JLabel("Location of javaclasspath.txt"), cLabel);

            // jcp textfield
            File file2 = null;
            try {
                file2 = Install.getJavaClassPathTxt();
                jtJCP = new JTextField(file2.toString());
                install.setJavaClassPathText(file2);
            } catch (IOException ignored) {
                jtJCP = new JTextField();
            }
            jtJCP.setEditable(false);
            GridBagConstraints cJCPPath = new GridBagConstraints();
            cJCPPath.gridy = 3;
            cJCPPath.gridx = 0;
            cJCPPath.weightx = 1;
            cJCPPath.insets = new Insets(0, 10, 0, 10);
            cJCPPath.fill = GridBagConstraints.HORIZONTAL;
            add(jtJCP, cJCPPath);

            // jcp browser button
            GridBagConstraints cJCPPathBrowse = new GridBagConstraints();
            cJCPPathBrowse.gridy = 3;
            cJCPPathBrowse.gridx = 1;
            cJCPPathBrowse.weightx = 0.1;
            cJCPPathBrowse.insets = new Insets(0, 0, 0, 10);
            final JButton jb = new JButton("...");
            jb.addActionListener(e -> {
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = fc.showOpenDialog(jb);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file12 = fc.getSelectedFile();
                    install.setJavaClassPathText(file12);
                    jtJCP.setText(file12.toString());
                    checkJCPPath();
                }
            });
            add(jb, cJCPPathBrowse);
        }
        {
            // label for install dir
            GridBagConstraints cLabel = new GridBagConstraints();
            cLabel.gridy = 4;
            cLabel.gridx = 0;
            cLabel.weightx = 1;
            cLabel.insets = new Insets(10, 10, 0, 0);
            cLabel.fill = GridBagConstraints.HORIZONTAL;
            add(new JLabel("Installation directory"), cLabel);

            GridBagConstraints cID = new GridBagConstraints();
            cID.gridy = 5;
            cID.gridx = 0;
            cID.weightx = 1;
            cID.insets = new Insets(0, 10, 0, 10);
            cID.fill = GridBagConstraints.HORIZONTAL;
            jtID = new JTextField("", 50);
            jtID.setEditable(false);
            add(jtID, cID);

            // install dir browser button
            GridBagConstraints cIDPathBrowse = new GridBagConstraints();
            cIDPathBrowse.gridy = 5;
            cIDPathBrowse.gridx = 1;
            cIDPathBrowse.weightx = 0.1;
            cIDPathBrowse.insets = new Insets(0, 0, 0, 10);
            final JButton jb = new JButton("...");
            jb.addActionListener(e -> {
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = fc.showOpenDialog(jb);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file1 = fc.getSelectedFile();
                    install.setInstallDir(file1.toString() + File.separator + "MEP");
                    jtID.setText(install.getInstallDir().toString());
                }
            });
            add(jb, cIDPathBrowse);
        }

        checkJARPath();
        checkJCPPath();
    }

    private void searchJars(File folder) {
        install.setJarDirectory(folder);
        checkJARPath();
    }

    private void addButtons() {
        JPanel jp = new JPanel();
        jp.setLayout(new GridBagLayout());

        // install Button
        GridBagConstraints cBI = new GridBagConstraints();
        cBI.gridy = 0;
        cBI.gridx = 0;
        cBI.weightx = 1;
        cBI.insets = new Insets(10, 10, 10, 0);
        cBI.anchor = GridBagConstraints.LINE_START;
        JButton jbi = new JButton("Install");
        jp.add(jbi, cBI);

        jbi.addActionListener(e -> doInstall());

        // // uninstall Button
        // GridBagConstraints cBU = new GridBagConstraints();
        // cBU.gridy = 0;
        // cBU.gridx = 1;
        // cBU.weightx = 1;
        // cBU.insets = new Insets(10, 10, 10, 0);
        // cBU.anchor = GridBagConstraints.LINE_END;
        // JButton jbu = new JButton("Uninstall");
        // jp.add(jbu, cBU);

        GridBagConstraints cp = new GridBagConstraints();
        cp.gridy = 6;
        cp.gridx = 0;
        cp.weightx = 1;
        cp.insets = new Insets(10, 10, 10, 10);
        cp.fill = GridBagConstraints.HORIZONTAL;
        add(jp, cp);
    }

    private void checkJCPPath() {
        if (install.isValidJCP()) {
            jtJCP.setBackground(INVALID);
        } else {
            jtJCP.setBackground(VALID);
        }
    }

    private void checkJARPath() {
        if (install.isValidJarDirectory()) {
            jtJAR.setBackground(INVALID);
        } else {
            jtJAR.setBackground(VALID);
        }
    }

    private void doInstall() {
        install.install(true);
    }
}
