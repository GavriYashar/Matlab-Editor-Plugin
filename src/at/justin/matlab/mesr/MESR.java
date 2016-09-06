package at.justin.matlab.mesr;

import at.justin.matlab.EditorWrapper;

import javax.swing.event.DocumentEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreas Justin on 2016-09-06.
 */
public class MESR {
    private static final MESR INSTANCE = new MESR();
    private static EditorWrapper ew = EditorWrapper.getInstance();
    private static final Pattern actionPattern = Pattern.compile("%\\w+(\\([\\w\\.,]+\\))?");

    private MESR() {
    }

    public static void doYourThing() {
        // int s = ew.getSelectionPositionStart();
        // int e = ew.getSelectionPositionEnd();
        // int[] lc = ew.pos2lc(s);
        //
        // String lineString = ew.getCurrentLineText();
        // System.out.println(lineString);
        // lineString = lineString.substring(0,lc[1]-1);
        // System.out.println(lineString);
        //
        //
        // Pattern actionPattern = Pattern.compile("%\\w+(\\([\\w\\.,]+\\))?%");
        // Matcher matcher = actionPattern.matcher(lineString);
        // if (matcher.find()) {
        //     System.out.println(matcher.groupCount());
        //     System.out.println(matcher.start());
        //     System.out.println(matcher.end());
        // }
    }

    public static MESR getInstance() {
        return INSTANCE;
    }

    public static String replaceString(String string, String command) {
        return "";
    }
}
