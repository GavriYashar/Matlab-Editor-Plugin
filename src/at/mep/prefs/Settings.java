package at.mep.prefs;

import at.mep.util.ColorUtils;
import at.mep.util.KeyStrokeUtil;
import com.mathworks.services.Prefs;
import com.mathworks.services.settings.Setting;
import com.mathworks.services.settings.SettingNotFoundException;
import com.mathworks.services.settings.SettingPath;
import com.mathworks.services.settings.SettingTypeException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.TreeSet;

/** Created by Andreas Justin on 2016 - 02 - 07. */
public class Settings {
    public static boolean DEBUG = false;
    private static long RELOAD_TIME = 5000;
    private static Properties internalProps = new Properties();
    private static Properties customProps = new Properties();
    private static Properties defaultProps = new Properties();
    private static String customSettingsName;
    private static String defaultSettingsName;
    private static boolean autoReload = false;
    private static long lastReload = System.currentTimeMillis();

    /** throws the CTRL+F2 error only once until next startup */
    public static boolean issue57DisplayMessage = true;

    /** throws the load error only once, and does not try to reload properties until next startup */
    private static boolean hasThrownLoadError = false;

    static {
        internalProps = load(Settings.class.getResourceAsStream("/properties/Internal.properties"));
        defaultProps = load(Settings.class.getResourceAsStream("/properties/DefaultProps.properties"));
    }

    public static void loadSettings(String customSettings, String defaultSettings) {
        if (Settings.DEBUG) {
            Properties debugProps = load(Settings.class.getResourceAsStream("/properties/DEBUG.properties"));
            defaultProps = mergeProps(defaultProps, debugProps);
        }

        try {
            customProps = load(customSettings);
            defaultProps = mergeProps(defaultProps, load(defaultSettings));
            customSettingsName = customSettings;
            defaultSettingsName = defaultSettings;
            autoReload = getPropertyBoolean("autoReloadProps");
        } catch (Exception e) {
            if (!hasThrownLoadError) {
                hasThrownLoadError = true;
                e.printStackTrace();
            }
            autoReload = false;
        }
    }

    public static void setCustomProps(final String key, final String val) {
        customProps.setProperty(key, val);
    }

    public static void store() throws IOException {
        Properties tmp = new Properties() {
            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<>(super.keySet()));
            }
        };
        tmp.putAll(customProps);
        Writer writer = new FileWriter(customSettingsName, false);
        tmp.store(writer, "Custom Settings Documentation in DefaultProps.properties");
    }

    public static String getCustomSettingsName() {
        return customSettingsName;
    }

    public static String getProperty(String key) {
        if (internalProps.containsKey(key)) return internalProps.getProperty(key);

        if (autoReload && (System.currentTimeMillis() - lastReload) > RELOAD_TIME) {
            lastReload = System.currentTimeMillis();
            autoReload = false;
            loadSettings(customSettingsName, defaultSettingsName);
        }
        if (customProps.containsKey(key)) return customProps.getProperty(key);
        if (defaultProps.containsKey(key)) return defaultProps.getProperty(key);
        return "";
    }

    public static String[] getPropertyStringArray(String key) {
        String val = getProperty(key);
        if (val.length() == 0) return new String[]{};
        return val.split(",\\s*");
    }

    public static void setProperty(String key, String val) {
        if (internalProps.containsKey(key)) {
            internalProps.setProperty(key, val);
            return;
        }
        customProps.setProperty(key, val);
    }

    public static boolean containsKey(String key) {
        return customProps.containsKey(key) || defaultProps.containsKey(key);
    }

    public static boolean getPropertyBoolean(String key) {
        String value = getProperty(key);
        value = value.toLowerCase();
        switch (value) {
            case "true":
                return true;
            case "false":
                return false;
            case "1":
                return true;
            case "0":
                return false;
        }
        throw new IllegalArgumentException("Key " + key + " is not found or not boolean");
    }

    public static void setPropertyBoolean(String key, boolean val) {
        setProperty(key, val ? "true" : "false");
    }

    public static int getPropertyInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public static Color getPropertyColor(String key) {
        String value = getProperty(key);
        value = value.toUpperCase();
        if (!value.matches("#[0-9A-F]{6}")) {
            throw new IllegalArgumentException("Property " + key + " is not a valid hex code #FFFFFF, instead " + value);
        }
        return new Color(
                Integer.parseInt(value.substring(1, 3), 16),
                Integer.parseInt(value.substring(3, 5), 16),
                Integer.parseInt(value.substring(5, 7), 16));
    }

    public static void setPropertyColor(String key, Color color) {
        customProps.setProperty(key, ColorUtils.colorToHex(color));
    }

    private static Properties load(String name) {
        try {
            InputStream in = new FileInputStream(name);
            return load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Properties();
    }

    private static Properties load(InputStream in) {
        Properties props = new Properties();
        try {
            props.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }

    /**
     * will merge two Properties into a new single Properties.
     * The second one overwrites properties of the first one if both have the same property key
     */
    private static Properties mergeProps(Properties p1, Properties p2) {
        Properties p = new Properties();
        Object[] keys1 = p1.keySet().toArray();
        Object[] keys2 = p2.keySet().toArray();
        Object[] vals1 = p1.values().toArray();
        Object[] vals2 = p2.values().toArray();

        for (int i = 0; i < keys1.length; i++) {
            p.setProperty((String) keys1[i], (String) vals1[i]);
        }
        for (int i = 0; i < keys2.length; i++) {
            p.setProperty((String) keys2[i], (String) vals2[i]);
        }

        return p;
    }

    public static String getMatlabCustomFolderPath() throws SettingNotFoundException, SettingTypeException {
        // com.mathworks.mlwidgets.prefs.GeneralPrefsPanel
        SettingPath settingPath = new SettingPath("matlab", "workingfolder");
        Setting setting = new Setting(settingPath, String.class, "CustomFolderPath");
        return (String) setting.get();
    }

    public static File getJavaClassPathText() throws IOException {
        File file = null;
        for (int i = 0; i < 1; i++) {
            if (i == 0) {
                file = new File(System.getProperty("user.home") + "\\Documents\\MATLAB\\javaclasspath.txt");
            } else if (i == 1) {
                file = new File(Prefs.getPropertyDirectory() + "\\javaclasspath.txt");
            } else if (i == 2) {
                try {
                    file = new File(Settings.getMatlabCustomFolderPath());
                } catch (SettingNotFoundException | SettingTypeException ignored) {
                    file = null;
                }
                if (file != null && file.exists()) break;
            }
            if (file == null) {
                throw new IOException("no javaclasspath.txt found");
            }
        }
        return file;
    }

    public static EIconSetting getFSIconSet() {
        String setting = Settings.getProperty("fs.iconSet");
        try {
            return EIconSetting.valueOf(setting.toUpperCase());
        } catch (Exception e) {
            // throw new IllegalStateException("Settings:getFSIconSet  fs.iconSet is not defined in EIconSetting");
            return EIconSetting.DEFAULT;
        }
    }

    public static KeyStroke getPropertyKeyStroke(String key) {
        return KeyStrokeUtil.getKeyStroke(getProperty(key));
    }
}

// Code i don't want to delete for some reason *rolling eyes*

/*
 *   Writer writer = new FileWriter(customSettingsName,false);
 *   writer.append("# enables/disables messages from the at.justin.matlab packages\n");
 *   writer.append("verbose = ").append(customProps.getProperty("verbose")).append("\n\n");
 *   writer.append("# if enabled properties will be reloaded before each getProperty* call\n");
 *   writer.append("autoReloadProps = ").append(customProps.getProperty("autoReloadProps")).append("\n\n");
 *   writer.append("# if enabled operators such as ++ will be replaced accordingly. e.g.: \"i++\" -> \"i = i + \"").append("\n");
 *   writer.append("enableDoubleOperator = ").append(customProps.getProperty("enableDoubleOperator")).append("\n\n");
 *   writer.append("# background color for the breakpointview as int 0..255\n");
 *   writer.append("bpColorR = ").append(customProps.getProperty("bpColorR")).append("\n");
 *   writer.append("bpColorG = ").append(customProps.getProperty("bpColorG")).append("\n");
 *   writer.append("bpColorB = ").append(customProps.getProperty("bpColorB")).append("\n\n");
 *   writer.append("# Automatically switches file for detailviewer (matlabs internal filestructure\n");
 *   writer.append("autoDetailViewer = ").append(customProps.getProperty("autoDetailViewer")).append("\n");
 *   writer.flush();
 *   writer.close();
 */
