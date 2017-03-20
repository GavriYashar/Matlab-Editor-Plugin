package com.mathworks.mlwidgets.prefs;

import at.mep.installer.Install;
import at.mep.prefs.PrefsWindow;
import at.mep.prefs.Settings;

import java.io.IOException;

/** Created by Andreas Justin on 2016-08-23. */
public class PrefsChanger extends PrefsDialog {
    public static void addPrefs() throws ClassNotFoundException {
        try {
            PrefsDialog prefsDialog = (PrefsDialog) getMLPrefsDialog();
            prefsDialog.registerPanel("MEP", "at.mep.prefs.PrefsWelcome", false);
            prefsDialog.registerPanel("MEP.Settings", "at.mep.prefs.PrefsPanel", false);
            Settings.setPropertyBoolean("jar.isOnStaticPath", true);
        } catch (IllegalAccessError e) {
            e.printStackTrace();
            System.out.println();
            System.out.println("MEP is not in Static path. Please Run the jar file or add MEP to javaclasspath.txt");
            try {
                System.out.println(Install.getJarFile().toString());
            } catch (IOException ignored) {
            }
            System.out.println();
            System.out.println("with this (below) you can still access a property editor, if you don't want to change this");
            System.out.println("p = " + PrefsWindow.class.getName() + ".getInstance(); p.showDialog()");
            System.out.println();
            Settings.setPropertyBoolean("jar.isOnStaticPath", false);
        }
    }
}
