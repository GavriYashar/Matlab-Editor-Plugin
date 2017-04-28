package at.mep.util;

/** Created by Andreas Justin on 2016-09-27. */
public enum ETrim {

    BOTH(0),
    LEADING(1),
    TRAILING(2);

    private final int value;

    ETrim(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
