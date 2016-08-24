package at.justin.matlab.prefs;

public enum PropertyType {
    BOOLEAN(0),
    INTEGER(1),
    STRING(2),
    ONOFF(3),
    PATH(4),
    COLOR(5);

    private final int type;

    PropertyType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}
