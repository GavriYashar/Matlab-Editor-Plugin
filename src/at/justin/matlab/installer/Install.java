package at.justin.matlab.installer;

import at.justin.matlab.util.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** Created by Andreas Justin on 2016-08-22. */
public class Install {
    private Install() {

    }

    public static void main(String[] args) throws InterruptedException {
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

    public static File getFileOfClass() throws IOException {
        try {
            return new File(Install.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            throw new IOException(e.toString());
        }
    }

    public static File getDefaultPropertyFile() throws IOException {
        try {
            String folder = new File(Install.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
            return new File(folder + "/DefaultProps.properties");
        } catch (URISyntaxException e) {
            throw new IOException(e.toString());
        }
    }

    public static File getCustomPropertyFile() throws IOException {
        try {
            String folder = new File(Install.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
            return new File(folder + "/CustomProps.properties");
        } catch (URISyntaxException e) {
            throw new IOException(e.toString());
        }
    }

    static File getJavaClassPathTxt() throws IOException {
        return new File(System.getProperty("user.home") + "\\Documents\\MATLAB\\javaclasspath.txt");
    }

    public static File getBookmarks() throws IOException {
        try {
            String folder = new File(Install.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
            return new File(folder + "/Bookmarks.properties");
        } catch (URISyntaxException e) {
            throw new IOException(e.toString());
        }
    }

    public static void appendJCPT(File javaClassPathText, String s) throws IOException {
        FileUtils.appendFileText(javaClassPathText, s);
    }
}
