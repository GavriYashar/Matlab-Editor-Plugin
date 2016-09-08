package at.justin.matlab.mesr;

import at.justin.matlab.EditorWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreas Justin on 2016-09-06.
 */
public class MESR {
    private static final MESR INSTANCE = new MESR();
    private static final Pattern actionPattern = Pattern.compile("%\\w+(\\([\\w\\.,]+\\))?");
    private static EditorWrapper ew = EditorWrapper.getInstance();

    private MESR() {
    }

    public static void doYourThing() {
        // System.out.println("MESR.doYourThing");
        //
        // int s = ew.getSelectionPositionStart();
        // int e = ew.getSelectionPositionEnd();
        // int[] lc = ew.pos2lc(s);
        // lc[1] -= 1;
        //
        // String lineString = ew.getCurrentLineText();
        // lineString = lineString.substring(0, lc[1]);
        // Matcher matcher = actionPattern.matcher(lineString);
        //
        // int[] se = {-1, -1};
        // while (matcher.find()) {
        //     se[0] = matcher.start();
        //     se[1] = matcher.end() + 1;
        // }
        // if (se[0] == -1 || se[1] != lc[1]) return; // only currently typed action
        // String action = lineString.substring(se[0], se[1]);
        // System.out.println(action);
    }

    public static MESR getInstance() {
        return INSTANCE;
    }

    public static String replaceString(String string, String command) {
        return "";
    }
}
