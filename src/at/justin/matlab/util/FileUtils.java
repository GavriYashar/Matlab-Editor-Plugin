package at.justin.matlab.util;

import at.justin.matlab.installer.Install;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
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
        return readFileToStringList(file, null);
    }

    public static List<String> readFileToStringList(File file, Pattern regexBreakCondition) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (regexBreakCondition != null) {
                Matcher matcher = regexBreakCondition.matcher(line);
                if (matcher.find()) {
                    br.close();
                    return lines;
                }
            }
            lines.add(line);
        }
        br.close();
        return lines;
    }

    public static String readFileToString(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s = readBufferedReaderToString(br, TrimE.TRAILING);
        br.close();
        return s;
    }

    public static String readBufferedReaderToString(BufferedReader br, TrimE trim) throws IOException {
        String line;
        String s = "";
        while ((line = br.readLine()) != null) {
            switch (trim) {
                case BOTH:
                    line = line.trim();
                    break;
                case LEADING:
                    line = StringUtils.trimStart(line);
                    break;
                case TRAILING:
                    line = StringUtils.trimEnd(line);
                    break;
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

    public static void exportNames(String destDir, String[] names) throws IOException {
        JarFile jar = new JarFile(Install.getJarFile());
        Enumeration enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();

            if (!jarEntryEqNames(file.getName(), names)) continue;

            File f = new File(destDir + File.separator + file.getName());
            if (file.isDirectory()) {
                f.mkdir();
                continue;
            }
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            InputStream is = jar.getInputStream(file);
            FileOutputStream fos = new FileOutputStream(f);
            while (is.available() > 0) {
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
    }

    public static void exportRegex(String destDir, String regex) throws IOException {
        JarFile jar = new JarFile(Install.getJarFile());
        Enumeration enumEntries = jar.entries();

        Pattern p = Pattern.compile(regex);
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();

            Matcher m = p.matcher(file.getName());
            if (!m.find()) continue;

            File f = new File(destDir + File.separator + file.getName());
            if (file.isDirectory()) {
                f.mkdir();
                continue;
            }
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            InputStream is = jar.getInputStream(file);
            FileOutputStream fos = new FileOutputStream(f);
            while (is.available() > 0) {
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
    }

    private static boolean jarEntryEqNames(String name, String[] names) {
        for (String n : names) {
            if (n.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static File searchForFileInFolder(File folder, String name, boolean exact) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                boolean found = false;
                if (exact) {
                    found = file.getName().equals(name);
                } else {
                    found = file.getName().toLowerCase().equals(name.toLowerCase());
                }
                if (found) {
                    return file;
                }
            }
        }
        return null;
    }
}
