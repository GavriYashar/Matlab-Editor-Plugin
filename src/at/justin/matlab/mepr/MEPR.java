package at.justin.matlab.mepr;

import at.justin.matlab.Matlab;
import at.justin.matlab.editor.EditorWrapper;
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
public class MEPR {
    public static final Pattern actionPattern = Pattern.compile("%\\w+(\\([\\w\\.,]+\\))?");
    public static final Pattern commentPatternBegin = Pattern.compile("%%.*MEPRBEGIN");
    public static final Pattern commentPatternEnd = Pattern.compile("%%.*MEPREND");
    public static final Pattern funcHandlPatternBegin = Pattern.compile("%%.*FUNCHANDLEBEGIN");
    public static final Pattern funcHandlPatternEnd = Pattern.compile("%%.*FUNCHANDLEEND");
    public static final Pattern funcHandlVar = Pattern.compile("\\$\\w+\\$");
    public static final Pattern funcHandlFunc = Pattern.compile("\\$\\{@[^${]+\\}\\$");
    public static final Pattern variablePattern = Pattern.compile("\\$\\{[^}]+\\}");
    private static final MEPR INSTANCE = new MEPR();
    private static File repPath = new File(Settings.getProperty("path.mepr.rep"));
    private static File varPath = new File(Settings.getProperty("path.mepr.var"));

    // replacement variables
    private static List<Integer> varStarts;
    private static List<Integer> varEnds;
    private static List<String> variables;
    private static List<String> varReps;

    // the doFunctionHandles() method somehow lets the document event finish and execute doReplace()
    // before prepareReplace() is done. if this happens the doReplaceTime is greater than prepareReplaceTime.
    // this shouldn't be the case at the end if prepareReplace(), since prepareReplaceTime is set "after"
    // the doReplaceTime therefore prepareReplaceTime should always be greater at the end of prepareReplace().
    private static long prepareReplaceTime = 0L;
    private static long doReplaceTime = 0L;

    // will update in KeyRealeaseHandler.doYourThing()
    private static String repText = "";
    private static int[] selectionSE;

    private MEPR() {
    }

    public static void setRepText(String repText) {
        MEPR.repText = repText;
    }

    public static void setSelectionSE(int[] selectionSE) {
        MEPR.selectionSE = selectionSE;
    }

    public static void doYourThing() {
        if (!repPath.exists() || !varPath.exists()) return;

        // prepareReplace and doReplace are separated because matconsolectl enters an endless loop otherwise.
        // Also KeyReleaseHandler is notified before DocumentEvent (sometimes). So theoretically the KeyReleaseHandler could
        // replace %action% but then the last "%" is not deleted.
        prepareReplace(false);
    }

    public static void doReplace() {
        doReplaceTime = System.nanoTime();
        if (repText.length() < 1) return;
        EditorWrapper.setSelectionPosition(selectionSE[0], selectionSE[1]);
        EditorWrapper.setSelectedTxt(repText);
        repText = "";
    }

    public static void prepareReplace(boolean srcIsViewer) {
        String action = getAction();
        if (action.length() < 1) return;
        int s = EditorWrapper.getSelectionPositionStart();
        selectionSE = new int[]{s - action.length(), s + 1};
        prepareReplace(action, srcIsViewer);
    }

    public static String getAction() {
        int s = EditorWrapper.getSelectionPositionStart();
        int[] lc = EditorWrapper.pos2lc(s);
        lc[1] -= 1;

        String lineString = EditorWrapper.getCurrentLineText();
        if (lineString.length() < 1) return "";
        lineString = lineString.substring(0, lc[1]);
        Matcher matcher = actionPattern.matcher(lineString);

        int[] se = {-1, -1};
        while (matcher.find()) {
            se[0] = matcher.start();
            se[1] = matcher.end(); // +1 bei %asdaf% (documentEvent)
        }
        if (se[0] == -1 || se[1] != lc[1]) return ""; // only currently typed action
        String action = lineString.substring(se[0], se[1]);
        return action;
    }

    public static void prepareReplace(String action, boolean srcIsViewer) {
        prepareReplaceTime = System.nanoTime();
        repText = "";
        varStarts = new ArrayList<>(10);
        varEnds = new ArrayList<>(10);
        variables = new ArrayList<>(10);
        varReps = new ArrayList<>(10);

        String actionVar = "";
        File actionFile = FileUtils.searchForFileInFolder(repPath, "MEPR_" + action.substring(1, action.length()) + ".m", false);
        if (actionFile == null) {
            int index = action.indexOf('(');
            if (index < 0) {
                System.out.println("unknown action: \"" + action + "%\"");
                return;
            }
            actionFile = FileUtils.searchForFileInFolder(repPath, "MEPR_" + action.substring(1, index) + ".m", false);
            if (actionFile == null) {
                System.out.println("unknown action: \"" + action + "%\"");
                return;
            }
            actionVar = action.substring(index, action.length());
        }
        String txt = "";
        try {
            txt = FileUtils.readFileToString(actionFile);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        }
        if (txt.length() >= 1) {
            txt = txt.substring(0, txt.length() - 1); // remove last \n
        }
        txt = trimCommentBlock(txt);
        txt = doFunctionHandles(txt);
        repText = replaceVariables(txt, actionVar);
        int s = EditorWrapper.getSelectionPositionStart();

        // at this line ine the code it looks  like it is useless, but trust me it's not.
        // @see comment above of prepareReplaceTime and doReplaceTime.
        if (doReplaceTime > prepareReplaceTime) {
            doReplace();
        } else if (srcIsViewer) {
            selectionSE = new int[]{s, s};
        }
    }

    /** trims function handle block and executes them */
    private static String doFunctionHandles(String txt) {
        Matcher fB = funcHandlPatternBegin.matcher(txt);
        if (!fB.find()) return txt;
        Matcher fE = funcHandlPatternEnd.matcher(txt);
        if (!fE.find()) return txt;
        int s = fB.start();
        int e = fE.end();

        String funcBlock = txt.substring(s, e);
        String[] varFuncs = funcBlock.split("\\n");
        txt = remText(txt, s, e);

        List<String> varTemp = new ArrayList<>(3);
        List<String> repTemp = new ArrayList<>(3);
        for (int i = 1; i < varFuncs.length - 1; i++) {
            // i == 0 is comment line as is the last i
            String[] varFunc = varFuncs[i].split("\\$\\s*\\$");
            varTemp.add(varFunc[0].trim() + "$");
            String rep;
            try {
                String func = varFunc[1].substring(1, varFunc[1].length() - 1);
                func = func.replace("'", "''");
                Object[] out = Matlab.getInstance().proxyHolder.get().returningEval(
                        "feval(str2func('" + func + "'))",
                        1);
                rep = (String) out[0];
            } catch (Exception e1) {
                e1.printStackTrace();
                rep = varFunc[0];
            }
            repTemp.add(rep);
        }

        Matcher variableMatcher = funcHandlVar.matcher(txt);
        while (variableMatcher.find()) {
            int sVarName = variableMatcher.start();
            int eVarName = variableMatcher.end();
            String varName = txt.substring(sVarName, eVarName);
            varStarts.add(sVarName);
            varEnds.add(eVarName);
            variables.add(varName);

            int index = varTemp.indexOf(varName);
            varReps.add(repTemp.get(index));
        }

        return txt;
    }

    /** removes text from int start to int end */
    private static String remText(String txt, int start, int end) {
        String text = "";
        if (start >= 1) {
            text = txt.substring(0, start - 1);
        }
        if (end < txt.length()) {
            text += txt.substring(end + 1, txt.length());
        }
        return text;
    }

    /**
     * loads the replacement for variables
     *
     * @param var       MEPV_[VAR]
     * @param actionVar MEPV_[VAR] additional input
     */
    private static String getVariableRep(String var, String actionVar) throws MatlabInvocationException {
        String oldPath;
        Object[] outs;
        outs = Matlab.getInstance().proxyHolder.get().returningFeval("cd", 1, varPath.getAbsolutePath());
        oldPath = (String) outs[0];

        String repVar = var;
        if (!Settings.getPropertyBoolean("isPublicUser") && var.matches("\\$\\{H\\d\\}") && actionVar.length() == 0) {
            // speziell für überschriften
            actionVar = var.substring(3, 4);
            var = "${H}";
        }
        if (actionVar.length() != 0) {
            repVar = var.substring(0, var.length() - 1) + actionVar + var.substring(var.length() - 1);
        }
        outs = Matlab.getInstance().proxyHolder.get().returningFeval("MEPV_" + var.substring(2, var.length() - 1), 1, repVar);
        String txt = (String) outs[0];

        Matlab.getInstance().proxyHolder.get().returningFeval("cd", 1, oldPath);
        return txt;
    }

    /** actually replacing variables from txt */
    private static String replaceVariables(String txt, String actionVar) {
        Matcher matcher = variablePattern.matcher(txt);
        while (matcher.find()) {
            int s = matcher.start();
            int e = matcher.end();
            String variable = txt.substring(s, e);
            varStarts.add(s);
            varEnds.add(e);

            if (!variables.contains(variable)) {
                try {
                    varReps.add(getVariableRep(variable, actionVar));
                } catch (MatlabInvocationException e1) {
                    e1.printStackTrace();
                    varReps.add(variable);
                }
            } else {
                // if variable is already added reuse replacement string
                varReps.add(varReps.get(variables.indexOf(variable)));
            }
            variables.add(variable);
        }

        String retTxt = txt;
        for (int i = 0; i < variables.size(); i++) {
            retTxt = retTxt.replace(variables.get(i), varReps.get(i));
        }
        return retTxt;
    }

    private static String trimCommentBlock(String txt) {
        Matcher cB = commentPatternBegin.matcher(txt);
        if (!cB.find()) return txt;
        Matcher cE = commentPatternEnd.matcher(txt);
        if (!cE.find()) return txt;
        int s = cB.start();
        int e = cE.end();

        txt = remText(txt, s, e);
        txt = trimCommentBlock(txt);
        return txt;
    }

    public static MEPR getInstance() {
        return INSTANCE;
    }
}
