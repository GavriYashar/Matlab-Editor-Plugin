package at.mep.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Created by Gavri on 2017-03-21. */
public enum EAccess {
    INVALID(-1),
    PRIVATE(0),
    PROTECTED(1),
    PUBLIC(2),
    META(3),
    IMMUTABLE(4),
    NONE(5), // e.g. Constant properties
    TRUE(6),
    FALSE(7);

    private final int access;

    private static List<String> names = new ArrayList<>(0);

    EAccess(int access) {
        this.access = access;
    }

    public int getAccess() {
        return access;
    }

    public boolean convertBoolean() {
        return access == TRUE.access;
    }

    public static List<String> getNames() {
        // public static List<String> getNames(Class<? extends Enum<?>> e)
        if (names.size() == 0) {
            names = Arrays.asList(
                    Arrays.toString(EAccess.class.getEnumConstants())
                            .replaceAll("^.|.$", "")
                            .split(", "));
        }
        return names;
    }
}
