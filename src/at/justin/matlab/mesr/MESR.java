package at.justin.matlab.mesr;

import at.justin.matlab.EditorWrapper;
import at.justin.matlab.Matlab;
import at.justin.matlab.installer.Install;
import at.justin.matlab.prefs.Settings;
import at.justin.matlab.util.FileUtils;
import matlabcontrol.MatlabInvocationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created by Andreas Justin on 2016-09-06. */
public class MESR {
    private static final MESR INSTANCE = new MESR();
    private static final Pattern actionPattern = Pattern.compile("%\\w+(\\([\\w\\.,]+\\))?");
    private static final Pattern commentPatternBegin = Pattern.compile("%%.*MEPRBEGIN");
    private static final Pattern commentPatternEnd = Pattern.compile("%%.*MEPREND");
    private static final Pattern variablePattern = Pattern.compile("\\$\\{[^}]+\\}");
    private static EditorWrapper ew = EditorWrapper.getInstance();
    private static File repPath = new File(Settings.getProperty("path.mepr.rep"));
    private static File varPath = new File(Settings.getProperty("path.mepr.var"));

    private MESR() {
    }

    public static void doYourThing() {
        // if (!repPath.exists() || !varPath.exists()) return;
        //
        // int s = ew.getSelectionPositionStart();
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
        // File actionFile = FileUtils.searchForFileInFolder(repPath, "MEPR_" + action.substring(1, action.length() - 1) + ".m", false);
        // if (actionFile == null) return;
        // String txt = "";
        // try {
        //     txt = FileUtils.readFileToString(actionFile);
        // } catch (IOException e1) {
        //     e1.printStackTrace();
        //     return;
        // }
        // txt = txt.substring(0, txt.length() - 1); // remove last \n
        // txt = trimCommentBlock(txt);
        // txt = replaceVariables(txt);
        // EditorWrapper.getInstance().setSelectionPosition(s - action.length(), s);
        // EditorWrapper.getInstance().setSelectedTxt(txt);
    }

    private static String getVariableRep(String var) throws MatlabInvocationException {
        String version = null;
        try {
            version = Install.getVersion();
        } catch (IOException e) {
            version = "SOMETHING";
        }
        String txtVar = "MEP_" + version + "_TXT";
        String varVar = "MEP_" + version + "_VAR";
        String commandClear = "clear " + txtVar + " " + varVar;
        String cmd = txtVar + " = MEPV_" + var.substring(2, var.length() - 1) + "(" + varVar + ");";

        String txt = var;
        // Matlab.getInstance().proxyHolder.get().eval("a = 1+1");
        // Debug.assignObjectsToMatlab();
        // Matlab.getInstance().proxyHolder.get().feval("assignin", "base", varVar, var);
        // Matlab.getInstance().proxyHolder.get().feval("assignin", "base", "varVar", var);
        // Matlab.getInstance().proxyHolder.get().eval(cmd);
        // txt = (String) Matlab.getInstance().proxyHolder.get().getVariable(txtVar);
        // Matlab.getInstance().proxyHolder.get().eval(commandClear);
        return txt;
    }

    private static String replaceVariables(String txt) {
        Matcher matcher = variablePattern.matcher(txt);
        List<Integer> starts = new ArrayList<>(10);
        List<Integer> ends = new ArrayList<>(10);
        List<String> variables = new ArrayList<>(10);
        List<String> reps = new ArrayList<>(10);

        while (matcher.find()) {
            int s = matcher.start();
            int e = matcher.end();
            String variable = txt.substring(s, e);
            starts.add(s);
            ends.add(e);

            if (!variables.contains(variable)) {
                try {
                    reps.add(getVariableRep(variable));
                } catch (MatlabInvocationException e1) {
                    e1.printStackTrace();
                    reps.add(variable);
                }
            } else {
                // if variable is already added reuse replacement string
                reps.add(reps.get(variables.indexOf(variable)));
            }
            variables.add(variable);

        }
        return txt;
    }

    private static String trimCommentBlock(String txt) {
        Matcher cB = commentPatternBegin.matcher(txt);
        if (!cB.find()) return txt;
        Matcher cE = commentPatternEnd.matcher(txt);
        if (!cE.find()) return txt;
        int s = cB.start();
        int e = cE.end();

        String text = "";
        if (s >= 1) {
            text = txt.substring(0, s - 1);
        }
        if (e < txt.length()) {
            text += txt.substring(e + 1, txt.length());
        }
        text = trimCommentBlock(text);
        return text;
    }

    public static MESR getInstance() {
        return INSTANCE;
    }

    public static String replaceString(String string, String command) {
        return "";
    }
}
