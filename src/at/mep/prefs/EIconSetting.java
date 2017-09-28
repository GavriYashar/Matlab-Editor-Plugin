package at.mep.prefs;

/** Created by Andreas Justin on 2017-09-28. */
public enum EIconSetting {
    DEFAULT("default"),
    INTELLIJ("intellij"),
    ECLIPSE("eclipse"),
    MATLAB("matlab");

    String setting;

    EIconSetting(String setting) {
        this.setting = setting;
    }
}
