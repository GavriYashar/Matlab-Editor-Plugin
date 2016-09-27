package at.justin.matlab.util;

/** Created by Andreas Justin on 2016-09-27. */
public enum TrimE {

    BOTH(0),
    LEADING(1),
    TRAILING(2);

    private final int value;

    TrimE(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
