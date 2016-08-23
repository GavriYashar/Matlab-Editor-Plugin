package com.mathworks.mlwidgets.prefs;

/**
 * Created by Andreas Justin on 2016-08-23.
 */
public class PrefsChanger extends PrefsDialog {
    public static void addPrefs() throws ClassNotFoundException {
        PrefsDialog prefsDialog = (PrefsDialog) getMLPrefsDialog();
        prefsDialog.registerPanel("MEP", "at.justin.matlab.prefs.PrefsWelcome", false);
        prefsDialog.registerPanel("MEP.Settings", "at.justin.matlab.prefs.PrefsPanel", false);
    }
}
