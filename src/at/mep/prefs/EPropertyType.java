package at.mep.prefs;

public enum EPropertyType {
    BOOLEAN(0),
    INTEGER(1),
    STRING(2),
    STRING_DROPDOWN(3),
    ONOFF(4),
    PATH(5),
    COLOR(6);

    private final int type;

    EPropertyType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

}
