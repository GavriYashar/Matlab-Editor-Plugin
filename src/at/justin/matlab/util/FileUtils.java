package at.justin.matlab.util;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/** Created by Andreas Justin on 2016-08-23. */
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
        br.close();
        return lines;
    }

    public static String readFileToString(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = readBufferedReaderToString(br, true);
        br.close();
        return s;
    }

    public static String readBufferedReaderToString(BufferedReader br, boolean trim) throws IOException {
        String line;
        String s = "";
        while ((line = br.readLine()) != null) {
            if (trim) {
                line = line.trim();
            }
            s += line + "\n";
        }
        return s;
    }

    public static void appendFileText(File source, String s) throws IOException {
        Writer writer = new BufferedWriter(new FileWriter(source, true));
        writer.append(System.lineSeparator());
        writer.append(s);
        writer.close();
    }

    /**
     * replaces all lines where patternRegex is found with s
     */
    public static void replaceFileLine(File source, String patternRegex, String s) throws IOException {
        if (!source.exists() || !source.canRead() || !source.canWrite()) {
            throw new IOException("File does not exists or can't read nor write file \"" + source + "\"");
        }
        Pattern p = Pattern.compile(patternRegex, Pattern.CASE_INSENSITIVE);

        List<String> strings = new ArrayList<>(10);
        BufferedReader reader = new BufferedReader(new FileReader(source));
        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            strings.add(currentLine);
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new FileWriter(source));
        for (int i = 0; i < strings.size(); i++) {
            String line = strings.get(i);
            if (i > 0 && i < strings.size()) {
                writer.append(System.lineSeparator());
            }

            if (p.matcher(line).find()) {
                writer.append(s);
                continue;
            }
            writer.append(line);
        }
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
