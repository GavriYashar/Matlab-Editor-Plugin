package at.justin.matlab.util;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Properties;

/**
 * Created by Andreas Justin on 2016 - 02 - 07.
 */
public class Settings {
    public static Properties customProps;
    public static Properties defaultProps;

    public static String customSettingsName;
    public static String defaultSettingsName;

    private static boolean autoReload = false;

    public static void loadSettings(String customSettings, String defaultSettings) {
        customProps = load(customSettings);
        defaultProps = load(defaultSettings);
        customSettingsName = customSettings;
        defaultSettingsName = defaultSettings;

        // it's internally used, and default should be false;
        defaultProps.put("verbose","false");
        defaultProps.put("autoReloadProps","false");

        autoReload = getPropertyBoolean("autoReloadProps");
    }

    public static void setCustomProps(final String key, final String val){
        customProps.setProperty(key,val);
    }

    public static void store() throws IOException {
        Writer writer = new FileWriter(customSettingsName,false);
        writer.append("# enables/disables messages from the at.justin.matlab packages\n");
        writer.append("verbose = ").append(customProps.getProperty("verbose")).append("\n\n");
        writer.append("# if enabled properties will be reloaded before each getProperty* call\n");
        writer.append("autoReloadProps = ").append(customProps.getProperty("autoReloadProps")).append("\n\n");
        writer.append("# background color for the breakpointview as int 0..255\n");
        writer.append("bpColorR = ").append(customProps.getProperty("bpColorR")).append("\n");
        writer.append("bpColorG = ").append(customProps.getProperty("bpColorG")).append("\n");
        writer.append("bpColorB = ").append(customProps.getProperty("bpColorB")).append("\n\n");
        writer.append("# Automatically switches file for detailviewer (matlabs internal filestructure\n");
        writer.append("autoDetailViewer = ").append(customProps.getProperty("autoDetailViewer")).append("\n");
        writer.flush();
        writer.close();
    }

    public static String getProperty(String key) {
        if (autoReload) {
            autoReload = false;
            loadSettings(customSettingsName,defaultSettingsName);
        }
        if (customProps.containsKey(key)) return customProps.getProperty(key);
        if (defaultProps.containsKey(key)) return defaultProps.getProperty(key);
        return "";
    }

    public static boolean getPropertyBoolean(String key) {
        String value = getProperty(key);
        value = value.toLowerCase();
        switch (value) {
            case "true": return true;
            case "false": return false;
            case "1": return true;
            case "0": return false;
        }
        throw new IllegalArgumentException("Key " + key + " is not found or not boolean");
    }

    public static int getPropertyInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    private static Properties load(String name) {
        Properties props = new Properties();
        try {
            InputStream in = new FileInputStream(name);
            props.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }
}
