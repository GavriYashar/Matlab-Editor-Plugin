package at.justin.matlab.prefs;

import at.justin.matlab.util.ColorUtils;

import java.awt.*;
import java.io.*;
import java.util.Properties;

/**
 * Created by Andreas Justin on 2016 - 02 - 07.
 */
public class Settings {
    public static boolean DEBUG = true;
    private static Properties customProps = new Properties();
    private static Properties defaultProps = new Properties();
    private static String customSettingsName;
    private static String defaultSettingsName;
    private static boolean autoReload = false;

    static {
        defaultProps = load(Settings.class.getResourceAsStream("/properties/DefaultProps.properties"));
        if (Settings.DEBUG) {
            defaultProps = load(Settings.class.getResourceAsStream("/properties/DEBUG.properties"));
        }
    }

    public static void loadSettings(String customSettings, String defaultSettings) {
        customProps = load(customSettings);
        defaultProps = load(defaultSettings);
        customSettingsName = customSettings;
        defaultSettingsName = defaultSettings;

        autoReload = getPropertyBoolean("autoReloadProps");
    }

    public static void setCustomProps(final String key, final String val) {
        customProps.setProperty(key, val);
    }

    public static void store() throws IOException {
        Writer writer = new FileWriter(customSettingsName, false);
        customProps.store(writer, "Custom Settings Documentation in DefaultProps.properties");
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
    }

    public static String getProperty(String key) {
        if (autoReload) {
            autoReload = false;
            loadSettings(customSettingsName, defaultSettingsName);
        }
        if (customProps.containsKey(key)) return customProps.getProperty(key);
        if (defaultProps.containsKey(key)) return defaultProps.getProperty(key);
        return "";
    }

    public static void setProperty(String key, String val) {
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
}
