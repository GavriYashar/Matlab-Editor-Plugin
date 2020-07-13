package at.mep.util;

import java.util.List;
import java.util.Objects;

/** Created by Andreas Justin on 2016-09-27. */
public class StringUtils {
    private static final int WRONG_INDEX = -1;

    public static String capitalizeStart(final String string) {
        return string.length() == 0 ? string : string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String stripEnd(final String string, final String strip) {
        int end = string.length();
        if (end == 0) return string;
        if (strip == null || strip.length() == 0) {
            while (end != 0 && Character.isWhitespace(string.charAt(end - 1))) {
                end--;
            }
        } else {
            while (end != 0 && strip.indexOf(string.charAt(end - 1)) != WRONG_INDEX) {
                end--;
            }
        }
        return string.substring(0, end);
    }

    public static String stripStart(final String string, final String strip) {
        int strLen = string.length();
        if (strLen == 0) return string;
        int start = 0;
        if (strip == null || strip.length() == 0) {
            while (start != strLen && Character.isWhitespace(string.charAt(start))) {
                start++;
            }
        } else {
            while (start != strLen && strip.indexOf(string.charAt(start)) != WRONG_INDEX) {
                start++;
            }
        }
        return string.substring(start);
    }

    public static String trimStart(final String string) {
        return stripStart(string, null);
    }

    public static String trimEnd(final String string) {
        return stripEnd(string, null);
    }

    public static String blanks(int length) {
        return StringUtils.repeat(" ", length);
    }

    public static String repeat(String string, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++){
            sb.append(string);
        }
        return sb.toString();
    }
    
    /**
     * Support for Java 7
     * String.join(delimiter, elements)
     */
    public static String join(String delimiter, List<String> elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        
        StringBuilder joiner = new StringBuilder(delimiter);
        for (CharSequence cs: elements) {
            joiner.append(cs);
        }
        return joiner.toString();
    }
}
