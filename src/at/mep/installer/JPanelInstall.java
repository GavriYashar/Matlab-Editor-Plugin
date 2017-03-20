package at.mep.installer;

import at.mep.prefs.Settings;
import at.mep.util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/** Created by Andreas Justin on 2016-08-22. */
public class JPanelInstall extends JPanel {
    private final static Color INVALID = new Color(224, 125, 112);
    private final static Color VALID = new Color(139, 255, 109);
    final JFileChooser fc = new JFileChooser();
    private File jarMEP = null;
    private File jarMCTL = null;
    private File txtJCP = null;
    private File id = null;
    private File jarMEPID = null;
    private File jarMCTLID = null;

    private JTextField jtJAR = null;
    private JTextField jtJCP = null;
    private JTextField jtID = null;

    public JPanelInstall() {
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
            jb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnVal = fc.showOpenDialog(jb);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        jtJAR.setText(file.toString());
                        searchJars(file);
                    }
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
                txtJCP = file2;
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
            jb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    int returnVal = fc.showOpenDialog(jb);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        if (file.getName().startsWith("javaclasspath")) {
                            txtJCP = file;
                        } else {
                            txtJCP = null;
                        }
                        jtJCP.setText(file.toString());
                        checkJCPPath();
                    }
                }
            });
            add(jb, cJCPPathBrowse);
        }
        {
            // label for installdir
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

            // jcp browser button
            GridBagConstraints cIDPathBrowse = new GridBagConstraints();
            cIDPathBrowse.gridy = 5;
            cIDPathBrowse.gridx = 1;
            cIDPathBrowse.weightx = 0.1;
            cIDPathBrowse.insets = new Insets(0, 0, 0, 10);
            final JButton jb = new JButton("...");
            jb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int returnVal = fc.showOpenDialog(jb);

                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        id = new File(file.toString() + "\\MEP");
                        jtID.setText(id.toString());
                    }
                }
            });
            add(jb, cIDPathBrowse);
        }

        checkJARPath();
        checkJCPPath();
    }

    private void searchJars(File folder) {
        File[] files = folder.listFiles();
        String version = null;
        try {
            version = Install.getVersion();
        } catch (IOException e) {
            version = "";
        }
        for (File f : files) {
            String s = f.getName();
            if (s.startsWith("matconsolectl")) {
                if (jarMCTL != null) {
                    // if there are more than one files matching this, only one allowed
                    jarMCTL = null;
                    break;
                }
                jarMCTL = f;
            }
            if (s.startsWith("MEP_" + version)) {
                if (jarMEP != null) {
                    // if there are more than one files matching this, only one allowed
                    jarMEP = null;
                    break;
                }
                jarMEP = f;
            }
        }
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

        jbi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doInstall();
            }
        });

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
        if (txtJCP == null) {
            jtJCP.setBackground(INVALID);
        } else {
            jtJCP.setBackground(VALID);
        }
    }

    private void checkJARPath() {
        if (jarMCTL == null || jarMEP == null) {
            jtJAR.setBackground(INVALID);
        } else {
            jtJAR.setBackground(VALID);
        }
    }

    private void doInstall() {
        if (jarMCTL == null || jarMEP == null || txtJCP == null) {
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    "Please make sure that there is only one .jar file of both MEP_xxxxa and matconsolectl-v.v.v.jar in path.\nAlso make sure that the path to javaclasspath.txt is correct",
                    "Invalid paths",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (id == null) {
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    "No installation path selected",
                    "Invalid installation path",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        id.mkdir();
        copyFiles();
        modifyJCPT();
    }

    private void modifyJCPT() {
        boolean addMEP = true;
        boolean addMCTL = true;
        java.util.List<String> lines;
        try {
            lines = FileUtils.readFileToStringList(txtJCP);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    e.getMessage()
                            + "\n\n"
                            + "please modify javaclasspath.txt by hand and add both jars from installdirectory"
                            + "\nstart Matlab and type 'edit javaclasspath.txt' and add both jars (full qualified name)"
                            + "\nthen restart matlab",
                    "unable to read javaclasspath.txt",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (String s : lines) {
            if (!s.startsWith("#") && s.contains("MEP_")) addMEP = false;
            if (!s.startsWith("#") && s.contains("matconsolectl")) addMCTL = false;
            if (!addMCTL && !addMEP) break;
        }
        if (addMEP) appendJCPT(jarMEPID);
        if (addMCTL) appendJCPT(jarMCTLID);
        if (!addMCTL && !addMEP) {
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    "already changed",
                    "unable to read javaclasspath.txt",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void appendJCPT(File file) {
        try {
            Install.appendJCPT(txtJCP, file.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    e.getMessage(),
                    "Uh Oh " + file.getName(),
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyFiles() {
        {
            // copy MEP
            File f = new File(id + "\\" + jarMEP.getName());
            FileUtils.copyFile(jarMEP, f);
            jarMEPID = f;
        }
        {
            // copy matconsolectl
            File f = new File(id + "\\" + jarMCTL.getName());
            FileUtils.copyFile(jarMCTL, f);
            jarMCTLID = f;
        }
        {
            // copy props
            File ft1 = new File(id.getPath() + "\\" + "DefaultProps.properties");
            File ft2 = new File(id.getPath() + "\\" + "CustomProps.properties");

            try {
                FileUtils.exportResource("/properties/DefaultProps.properties", ft1);
                FileUtils.exportResource("/properties/DefaultProps.properties", ft2);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        new JFrame(""),
                        e.getMessage(),
                        "something went wrong, very very wrong",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        // copy MEP[RV]
        try {
            FileUtils.exportRegex(id.getPath(), "^Replacements");
            Settings.setProperty("path.mepr.rep", id.getPath() + "/Replacements");
            Settings.setProperty("path.mepr.var", id.getPath() + "/Replacements/Variables");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    e.getMessage(),
                    "something went wrong, very very wrong",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
