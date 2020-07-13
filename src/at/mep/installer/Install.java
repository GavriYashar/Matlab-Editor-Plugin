package at.mep.installer;

import at.mep.prefs.Settings;
import at.mep.util.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Andreas Justin on 2016-08-22. */
public class Install {
    private final static Install INSTANCE = new Install();
    private File jarMEP;
    private File jarMCTL;
    private File txtJCP;
    private File installDir;
    private File jarMEPId;
    private File jarMCTLId;

    private java.util.List<String> jcptext;

    private Install() {
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length == 3) {
            INSTANCE.install(args[0], args[1], args[2], false);
        } else {
            installViaGUI();
        }
    }

    void setJarDirectory(String jarDirectory) {
        setJarDirectory(new File(jarDirectory));
    }

    void setJarDirectory(File folder) {
        File[] files = folder.listFiles();
        String version;
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
    }

    void setInstallDir(String installDir) {
        if (!(installDir.endsWith("MEP") || installDir.endsWith(File.separator + "MEP"))) {
            installDir += File.separator + "MEP";
        }
        this.installDir = new File(installDir);
        jarMCTLId = new File(installDir + File.separator + jarMCTL.getName());
        jarMEPId = new File(installDir + File.separator + jarMEP.getName());
    }

    void setJavaClassPathText(String jcp) {
        setJavaClassPathText(new File(jcp));
    }

    void setJavaClassPathText(File jcp) {
        if (!jcp.getName().startsWith("javaclasspath")) {
            txtJCP = null;
            return;
        }
        txtJCP = jcp;
        try {
            jcptext = FileUtils.readFileToStringList(txtJCP);
        } catch (IOException e) {
            jcptext = null;
            e.printStackTrace();
        }
    }

    boolean isValidJarDirectory() {
        return jarMCTL == null || jarMEP == null;
    }

    boolean isValidJCP() {
        return txtJCP == null;
    }

    private void verifyInstall(boolean withGUI) {
        if (jarMCTL == null || jarMEP == null || txtJCP == null) {
            String message = "Please make sure that there is only one .jar file of both MEP_XX.jar and matconsolectl*.jar in path.\nAlso check the path to javaclasspath.txt";
            if (withGUI) {
                JOptionPane.showMessageDialog(new JFrame(""), message, "Invalid paths", JOptionPane.ERROR_MESSAGE);
            } else {
                System.out.println(message);
            }
        }
        if (jcptext == null) {
            String message = "Please modify javaclasspath.txt by hand and add both jars from install directory"
                    + "\nstart Matlab and type 'edit javaclasspath.txt' and add both jars (full qualified name)"
                    + "\nthen restart matlab";

            if (withGUI) {
                JOptionPane.showMessageDialog(new JFrame(""), message, "unable to read javaclasspath.txt", JOptionPane.ERROR_MESSAGE);
            }
            System.out.println(message);
        }
    }


    File getInstallDir() {
        return installDir;
    }

    public void install(String jar, String jcp, String installDir, boolean withGUI) {
        setJarDirectory(jar);
        setInstallDir(installDir);
        setJavaClassPathText(jcp);
        install(withGUI);
    }

    public void install(boolean withGUI) {
        verifyInstall(withGUI);
        modifyJCPT(withGUI);
        copyFiles(withGUI);

        String message = "Successfully installed MEP";
        if (withGUI) {
            JOptionPane.showMessageDialog(new JFrame(""), message, "unable to read javaclasspath.txt", JOptionPane.INFORMATION_MESSAGE);
        }
        System.out.println(message);
    }

    private void copyFiles(boolean isGUI) {
        installDir.mkdir();
        FileUtils.copyFile(jarMCTL, jarMCTLId);
        FileUtils.copyFile(jarMEP, jarMEPId);

        {
            // copy props
            File ft1 = new File(installDir.getPath() + File.separator + "DefaultProps.properties");
            File ft2 = new File(installDir.getPath() + File.separator + "CustomProps.properties");

            try {
                FileUtils.exportResource("/properties/DefaultProps.properties", ft1);
                FileUtils.exportResource("/properties/DefaultProps.properties", ft2);

                Settings.loadSettings(ft2.getPath(), ft1.getPath());
            } catch (Exception e) {
                if (isGUI){
                    JOptionPane.showMessageDialog(
                            new JFrame(""),
                            e.getMessage(),
                            "something went wrong, very very wrong",
                            JOptionPane.ERROR_MESSAGE);
                }
                e.printStackTrace();
                return;
            }
        }
        // copy MEP[RV]
        try {
            FileUtils.exportRegex(installDir.getPath(), "^Replacements");
            Settings.setProperty("path.mepr.rep", installDir.getPath() + "/Replacements");
            Settings.setProperty("path.mepr.var", installDir.getPath() + "/Replacements/Variables");
        } catch (IOException e) {
            if (isGUI) {
                JOptionPane.showMessageDialog(
                        new JFrame(""),
                        e.getMessage(),
                        "something went wrong, very very wrong",
                        JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
            return;
        }
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void modifyJCPT(boolean withGUI) {
        boolean addMEP = true;
        boolean addMCTL = true;
        for (String s : jcptext) {
            if (!s.startsWith("#") && s.contains("MEP_")) addMEP = false;
            if (!s.startsWith("#") && s.contains("matconsolectl")) addMCTL = false;
            if (!addMCTL && !addMEP) break;
        }
        if (addMCTL) appendJCPT(jarMCTLId, withGUI);
        if (addMEP) appendJCPT(jarMEPId, withGUI);
        if (!addMCTL && !addMEP) {
            String message = "Javaclasspath text contains both MEP and matconsolectl";
            if (withGUI) {
                JOptionPane.showMessageDialog(new JFrame(""), message, "javaclasspath.txt", JOptionPane.ERROR_MESSAGE);
            }
            System.out.println(message);
        }
    }

    private void appendJCPT(File file, boolean withGUI) {
        try {
            Install.appendJCPT(txtJCP, file.toString());
        } catch (IOException e) {
            if (withGUI) {
                JOptionPane.showMessageDialog(
                        new JFrame(""),
                        e.getMessage(),
                        "Uh Oh " + file.getName(),
                        JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
    }

    public static void installViaGUI() throws InterruptedException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }

        JFrame jf = new JFrame("Installing MEP");
        jf.getContentPane().add(new JPanelInstall());
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.pack();
        jf.setVisible(true);

        DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
        Date dateObj = new Date();
        while (jf.isVisible()) {
            Thread.sleep(500);
            dateObj.setTime(System.currentTimeMillis());
            jf.setTitle("running " + df.format(dateObj));
        }
        System.exit(0);
    }

    public static File getJarFile() throws IOException {
        try {
            return new File(Install.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            throw new IOException(e.toString());
        }
    }

    public static File getDefaultPropertyFile() throws IOException {
        try {
            String folder = new File(Install.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
            return new File(folder, "DefaultProps.properties");
        } catch (URISyntaxException e) {
            throw new IOException(e.toString());
        }
    }

    public static File getCustomPropertyFile() throws IOException {
        try {
            String folder = new File(Install.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
            return new File(folder, "CustomProps.properties");
        } catch (URISyntaxException e) {
            throw new IOException(e.toString());
        }
    }

    static File getJavaClassPathTxt() throws IOException {
        return new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "MATLAB" + File.separator + "javaclasspath.txt");
    }

    public static File getBookmarks() throws IOException {
        String folder = Settings.getUserDirectory().getAbsolutePath();
        return new File(folder, "Bookmarks.properties");
    }
    
    public static File getRecentlyClosedLastSessions() throws IOException {
        String folder = Settings.getUserDirectory().getAbsolutePath();
        return new File(folder, "RecentlyClosedLS.properties");
    }

    public static void appendJCPT(File javaClassPathText, String s) throws IOException {
        FileUtils.appendFileText(javaClassPathText, s);
    }

    public static String getVersion() throws IOException {
        String versionString = Install.getJarFile().toString();
        Pattern pattern = Pattern.compile("(\\d{4}[a-z]|\\d+\\.\\d+)");
        Matcher matcher = pattern.matcher(versionString);
        if (matcher.find()) {
            int s = matcher.start();
            versionString = versionString.substring(s, s + 5);
            if (versionString.endsWith("."))
                versionString = versionString.substring(0, versionString.length() - 1);
        } else {
            versionString = "To do a great right do a little wrong. <William Shakespeare>";
        }
        return versionString;
    }

    public static Install getInstance() {
        return INSTANCE;
    }
}
