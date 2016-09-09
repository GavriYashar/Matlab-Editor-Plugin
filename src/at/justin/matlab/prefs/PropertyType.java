package at.justin.matlab.prefs;

public enum PropertyType {
    BOOLEAN(0),
    INTEGER(1),
    STRING(2),
    STRING_DROPDOWN(3),
    ONOFF(4),
    PATH(5),
    COLOR(6);

    private final int type;

    PropertyType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}
