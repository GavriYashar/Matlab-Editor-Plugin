package at.mep.gui.mepr;

import at.mep.mepr.MEPR;
import at.mep.prefs.Settings;
import at.mep.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Created by Andreas Justin on 2016-09-20. */
public class MEPREntries {
    private static List<String> ALL_TAGS;
    public static final String NOTAG = Settings.getProperty("mepr.tag.noTag");

    public static List<MEPREntry> getAllEntries() {
        ALL_TAGS = new ArrayList<>(10);
        File folder = new File(Settings.getProperty("path.mepr.rep"));
        File[] files = folder.listFiles();
        if (files == null) return new ArrayList<>(0);

        List<MEPREntry> retVal = new ArrayList<>(files.length);
        for (File file : files) {
            String action = getAction(file);
            String tags = "SOMETHING WENT WRONG";
            String comment = "";
            if (action.endsWith(".m")) continue;
            if (!file.isFile()) continue;

            List<String> lines;
            try {
                lines = FileUtils.readFileToStringList(file, MEPR.commentPatternEnd);
            } catch (IOException e) {
                comment = e.getMessage();
                retVal.add(new MEPREntry(action, tags, comment));
                continue;
            }

            tags = getTags(lines);
            comment = getComment(lines);
            MEPREntry entry = new MEPREntry(action, tags, comment);
            retVal.add(entry);

            for (String tag : entry.getTags()) {
                if (!ALL_TAGS.contains(tag))
                    ALL_TAGS.add(tag);
            }
        }
        Collections.sort(ALL_TAGS);

        return retVal;
    }

    public static List<String> getAllTags() {
        return ALL_TAGS;
    }

    private static String getComment(List<String> lines) {
        if (lines.size() < 1) return "";
        int index = lines.get(1).indexOf('%');
        if (index < 0) return "";
        String retVal = lines.get(1).substring(index + 1);
        return retVal.trim();
    }

    private static String getTags(List<String> lines) {
        for (String line : lines) {
            if (line.contains("Tags:") || line.contains("tags:")) {
                return line;
            }
        }
        return NOTAG;
    }

    private static String getAction(File file) {
        String name = file.getName();
        int s = name.indexOf("MEPR_") + 5;
        if (s < 0) return name;
        int e = name.indexOf(".m");
        if (e < 0) return name;
        return name.substring(s, e);
    }
}
