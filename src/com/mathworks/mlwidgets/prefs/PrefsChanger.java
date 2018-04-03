package com.mathworks.mlwidgets.prefs;

import at.mep.Start;
import at.mep.prefs.Settings;

/** Created by Andreas Justin on 2016-08-23. */
public class PrefsChanger extends PrefsDialog {
    public static void addPrefs() throws ClassNotFoundException {
        try {
            PrefsDialog prefsDialog = (PrefsDialog) getMLPrefsDialog();
            prefsDialog.registerPanel("MEP", "at.mep.prefs.PrefsWelcome", false);
            prefsDialog.registerPanel("MEP.Settings", "at.mep.prefs.PrefsPanel", false);
            Settings.setPropertyBoolean("jar.isOnStaticPath", true);
        } catch (IllegalAccessError ignored) {
            System.out.println("MEP is on Dynamic Path: with the following line you can still access the property editor");
            System.out.println(Start.class.getName() + ".openPrefsPanel();");
            System.out.println();
            Settings.setPropertyBoolean("jar.isOnStaticPath", false);
        }
    }
}
