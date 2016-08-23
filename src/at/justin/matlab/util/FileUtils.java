package at.justin.matlab.util;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andreas Justin on 2016-08-23.
 */
public class FileUtils {

    public static void copyFile(File source, File target) {
        copyFile(source.toPath(), target.toPath());
    }

    public static void copyFile(Path source, Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    e.getMessage(),
                    "something went wrong, very very wrong",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static List<String> readFileToStringList(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }

    public static void appendFileText(File source, String s) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(source, true));
        writer.append(System.lineSeparator());
        writer.append(s);
        writer.close();
    }

    public static void exportResource(String resourceName, File target) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {
            stream = FileUtils.class.getResourceAsStream(resourceName);
            if (stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(target);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
    }
}
